package io.sapl.pdp.embedded.config.filesystem;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.pdp.PolicyDecisionPointConfiguration;
import io.sapl.directorywatcher.DirectoryWatchEventFluxSinkAdapter;
import io.sapl.directorywatcher.DirectoryWatcher;
import io.sapl.directorywatcher.InitialWatchEvent;
import io.sapl.interpreter.combinators.DocumentsCombinator;
import io.sapl.pdp.embedded.config.PDPConfigurationProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class FilesystemPDPConfigurationProvider implements PDPConfigurationProvider {

	private static final String CONFIG_FILE_GLOB_PATTERN = "pdp.json";

	private static final Pattern CONFIG_FILE_REGEX_PATTERN = Pattern.compile("pdp\\.json");

	private final ObjectMapper mapper = new ObjectMapper();

	private final ReentrantLock lock = new ReentrantLock();

	private String path;

	private PolicyDecisionPointConfiguration config;

	private Scheduler dirWatcherScheduler;

	private Disposable dirWatcherFluxSubscription;

	private ReplayProcessor<WatchEvent<Path>> dirWatcherEventProcessor = ReplayProcessor
			.cacheLastOrDefault(InitialWatchEvent.INSTANCE);

	public FilesystemPDPConfigurationProvider(@NonNull String configPath) {
		this.path = configPath;
		if (configPath.startsWith("~" + File.separator)) {
			this.path = System.getProperty("user.home") + configPath.substring(1);
		}
		else if (configPath.startsWith("~")) {
			throw new UnsupportedOperationException("Home dir expansion not implemented for explicit usernames");
		}

		initializeConfig();

		final Path watchDir = Paths.get(path);
		final DirectoryWatcher directoryWatcher = new DirectoryWatcher(watchDir);

		final DirectoryWatchEventFluxSinkAdapter adapter = new DirectoryWatchEventFluxSinkAdapter(
				CONFIG_FILE_REGEX_PATTERN);
		dirWatcherScheduler = Schedulers.newElastic("configWatcher");
		final Flux<WatchEvent<Path>> dirWatcherFlux = Flux.<WatchEvent<Path>>push(sink -> {
			adapter.setSink(sink);
			directoryWatcher.watch(adapter);
		}).doOnNext(event -> {
			updateConfig(event);
			dirWatcherEventProcessor.onNext(event);
		}).doOnCancel(adapter::cancel).subscribeOn(dirWatcherScheduler);

		dirWatcherFluxSubscription = dirWatcherFlux.subscribe();
	}

	private void initializeConfig() {
		try {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path), CONFIG_FILE_GLOB_PATTERN)) {
				for (Path filePath : stream) {
					LOGGER.info("load: {}", filePath);
					config = mapper.readValue(filePath.toFile(), PolicyDecisionPointConfiguration.class);
					break;
				}
				if (config == null) {
					config = new PolicyDecisionPointConfiguration();
				}
			}
		}
		catch (IOException e) {
			LOGGER.error("Error while initializing the pdp configuration.", e);
		}
	}

	private void updateConfig(WatchEvent<Path> watchEvent) {
		final WatchEvent.Kind<Path> kind = watchEvent.kind();
		final Path fileName = watchEvent.context();
		try {
			lock.lock();

			final Path absoluteFilePath = Paths.get(path, fileName.toString());
			if (kind == ENTRY_CREATE) {
				LOGGER.info("reading pdp config from {}", fileName);
				config = mapper.readValue(absoluteFilePath.toFile(), PolicyDecisionPointConfiguration.class);
			}
			else if (kind == ENTRY_DELETE) {
				LOGGER.info("deleted pdp config file {}. Using default configuration", fileName);
				config = new PolicyDecisionPointConfiguration();
			}
			else if (kind == ENTRY_MODIFY) {
				LOGGER.info("updating pdp config from {}", fileName);
				config = mapper.readValue(absoluteFilePath.toFile(), PolicyDecisionPointConfiguration.class);
			}
			else {
				LOGGER.error("unknown kind of directory watch event: {}", kind != null ? kind.name() : "null");
			}
		}
		catch (IOException e) {
			LOGGER.error("Error while updating the pdp config.", e);
		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public Flux<DocumentsCombinator> getDocumentsCombinator() {
		// @formatter:off
		return dirWatcherEventProcessor
				.map(event -> config.getAlgorithm())
				.distinctUntilChanged()
				.map(algorithm -> {
			LOGGER.trace("|-- Current PDP config: combining algorithm = {}", algorithm);
			return convert(algorithm);
		});
		// @formatter:on
	}

	@Override
	public Flux<Map<String, JsonNode>> getVariables() {
		// @formatter:off
		return dirWatcherEventProcessor
				.map(event -> (Map<String, JsonNode>) config.getVariables())
				.distinctUntilChanged()
				.doOnNext(variables -> LOGGER.trace("|-- Current PDP config: variables = {}", variables));
		// @formatter:on
	}

}
