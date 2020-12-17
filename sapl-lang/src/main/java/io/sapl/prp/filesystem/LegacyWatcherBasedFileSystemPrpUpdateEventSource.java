/*
 * Copyright © 2017-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.prp.filesystem;

import io.sapl.api.interpreter.SAPLInterpreter;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.prp.PrpUpdateEvent;
import io.sapl.prp.PrpUpdateEvent.Type;
import io.sapl.prp.PrpUpdateEvent.Update;
import io.sapl.prp.PrpUpdateEventSource;
import io.sapl.prp.directorywatcher.DirectoryWatchEventFluxSinkAdapter;
import io.sapl.prp.directorywatcher.DirectoryWatcher;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

import static java.nio.file.StandardWatchEventKinds.*;

@Slf4j
public class LegacyWatcherBasedFileSystemPrpUpdateEventSource implements PrpUpdateEventSource {

    private static final String POLICY_FILE_GLOB_PATTERN = "*.sapl";
    private static final Pattern POLICY_FILE_REGEX_PATTERN = Pattern.compile(".+\\.sapl");

    private final SAPLInterpreter interpreter;
    private final Path watchDir;
    private final Scheduler dirWatcherScheduler;
    private final Flux<WatchEvent<Path>> dirWatcherFlux;

    public LegacyWatcherBasedFileSystemPrpUpdateEventSource(String policyPath, SAPLInterpreter interpreter) {
        this.interpreter = interpreter;

        watchDir = fileSystemPath(policyPath);
        // Set up directory watcher

        final DirectoryWatcher directoryWatcher = new DirectoryWatcher(watchDir);
        final DirectoryWatchEventFluxSinkAdapter adapter = new DirectoryWatchEventFluxSinkAdapter(
                POLICY_FILE_REGEX_PATTERN);
        dirWatcherScheduler = Schedulers.newElastic("policyWatcher");
        dirWatcherFlux = Flux.<WatchEvent<Path>>push(sink -> {
            adapter.setSink(sink);
            directoryWatcher.watch(adapter);
        }).doOnCancel(adapter::cancel).subscribeOn(dirWatcherScheduler).share();
    }

    private final Path fileSystemPath(String policyPath) {
        String path = "";
        // First resolve actual path
        if (policyPath.startsWith("~" + File.separator) || policyPath.startsWith("~/")) {
            path = System.getProperty("user.home") + policyPath.substring(1);
        } else if (policyPath.startsWith("~")) {
            throw new UnsupportedOperationException("Home dir expansion not implemented for explicit usernames");
        } else {
            path = policyPath;
        }
        return Paths.get(path);
    }

    @Override
    public void dispose() {
        dirWatcherScheduler.dispose();
    }

    @Override
    public Flux<PrpUpdateEvent> getUpdates() {
        Map<String, SAPL> files = new HashMap<>();
        List<Update> updates = new LinkedList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(watchDir, POLICY_FILE_GLOB_PATTERN)) {
            for (var filePath : stream) {
                log.info("loading SAPL document: {}", filePath);
                var rawDocument = readFile(filePath);
                var saplDocument = interpreter.parse(rawDocument);
                files.put(filePath.toString(), saplDocument);
                updates.add(new Update(Type.PUBLISH, saplDocument, rawDocument));
            }
        } catch (IOException e) {
			throw Exceptions.propagate(e);
        }

        var seedIndex = new ImmutableFileIndex(files);
        var initialEvent = new PrpUpdateEvent(updates);
        log.debug("initial event: {}", initialEvent);
        return Mono.just(initialEvent).concatWith(directoryMonitor(seedIndex));
    }

    private Flux<PrpUpdateEvent> directoryMonitor(ImmutableFileIndex seedIndex) {
        return Flux.from(dirWatcherFlux).scan(Tuples.of(Optional.empty(), seedIndex), this::processWatcherEvent)
                .filter(tuple -> tuple.getT1().isPresent()).map(Tuple2::getT1).map(Optional::get)
                .distinctUntilChanged();
    }

    private Tuple2<Optional<PrpUpdateEvent>, ImmutableFileIndex> processWatcherEvent(
            Tuple2<Optional<PrpUpdateEvent>, ImmutableFileIndex> tuple, WatchEvent<Path> watchEvent) {
        var index = tuple.getT2();
        var kind = watchEvent.kind();
        var fileName = watchEvent.context();
        var absoluteFilePath = Path.of(watchDir.toAbsolutePath().toString(), fileName.toString());
        var absoluteFileName = absoluteFilePath.toString();

        if (kind != ENTRY_DELETE && kind != ENTRY_CREATE && kind != ENTRY_MODIFY) {
            log.debug("dropping unknown kind of directory watch event: {}", kind != null ? kind.name() : "null");
            return Tuples.of(Optional.empty(), index);
        }

        if (kind == ENTRY_DELETE) {
            log.info("unloading deleted SAPL document: {}", fileName);
            var update = new Update(Type.UNPUBLISH, index.get(absoluteFileName), "");
            var newIndex = index.remove(absoluteFileName);
            return Tuples.of(Optional.of(new PrpUpdateEvent(update)), newIndex);
        }

        if (absoluteFilePath.toFile().length() == 0) {
            log.debug("dropping potential duplicate event. {}", kind);
            return Tuples.of(Optional.empty(), index);
        }

        String rawDocument = "";
        SAPL saplDocument = null;
        try {
            rawDocument = readFile(absoluteFilePath);
            saplDocument = interpreter.parse(rawDocument);
        } catch (IOException e) {
			throw Exceptions.propagate(e);
        }

        // CREATE or MODIFY events
        // This is very system dependent. Different OS and tools modifying a file can
        // result in different signals.
        // e.g. modification of a file with one editor may result in MODIFY while the
        // other editor results in
        // CREATE without a matching DELETED first.
        // So both signals have to be treated similarly and we have to determine
        // ourselves what happened

        log.debug("Processing directory watch event of kind: {} for {}", kind, absoluteFileName);
        if (index.containsFile(absoluteFileName)) {
            log.debug("the file is already indexed. Treat this as a modification");
            log.info("loading updated SAPL document: {}", fileName);
            var oldDocument = index.get(absoluteFileName);
            var update1 = new Update(Type.UNPUBLISH, oldDocument, "");
            var update2 = new Update(Type.PUBLISH, saplDocument, rawDocument);
            var newIndex = index.put(absoluteFileName, saplDocument);
            return Tuples.of(Optional.of(new PrpUpdateEvent(update1, update2)), newIndex);
        } else {
            log.debug("the file is not yet indexed. Treat this as a file creation.");
            log.info("loading new SAPL document: {}", fileName);
            var update = new Update(Type.PUBLISH, saplDocument, rawDocument);
            var newIndex = index.put(absoluteFileName, saplDocument);
            return Tuples.of(Optional.of(new PrpUpdateEvent(update)), newIndex);
        }
    }

    public static String readFile(Path filePath) throws IOException {
        var fis = Files.newInputStream(filePath);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        }
    }

}
