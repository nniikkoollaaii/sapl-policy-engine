package io.sapl.test.coverage.api;

public class CoverageAPIFactory {

	public static CoverageHitReader constructCoverageHitReader() {
		return new CoverageHitAPIImpl();
	}
	
	public static CoverageHitRecorder constructCoverageHitRecorder() {
		return new CoverageHitAPIImpl();
	}
}
