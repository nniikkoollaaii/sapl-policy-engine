package io.sapl.mavenplugin.test.coverage;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import io.sapl.test.coverage.api.CoverageAPIFactory;
import io.sapl.test.coverage.api.CoverageHitReader;

@Mojo(name = "enable-coverage-collection", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES)
public class EnableCoverageCollectionMojo extends AbstractMojo {
	
	// inject the project
	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	
    public void execute() throws MojoExecutionException
    {
    	CoverageHitReader coverageReader = CoverageAPIFactory.constructCoverageHitReader();
    	coverageReader.cleanCoverageHitFiles();
    	project.getProperties().setProperty("io.sapl.test.coverage.collect", "true");
    }
}
