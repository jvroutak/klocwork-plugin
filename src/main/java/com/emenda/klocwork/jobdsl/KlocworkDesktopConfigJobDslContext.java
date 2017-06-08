package com.emenda.klocwork;

import javaposse.jobdsl.dsl.Context;
import static javaposse.jobdsl.plugin.ContextExtensionPoint.executeInContext;

import com.emenda.klocwork.config.KlocworkDesktopConfig;

class KlocworkDesktopConfigJobDslContext implements Context {

	KlocworkDesktopConfig klocworkDesktopConfig;

	public void analysisConfig(String buildSpec, String projectDir, boolean cleanupProject,
                                        String reportFile, String additionalOptions,
                                        boolean incrementalAnalysis, Runnable closure){


		KlocworkDiffAnalysisConfigJobDslContext context = new KlocworkDiffAnalysisConfigJobDslContext();
		executeInContext(closure, context);

		klocworkDesktopConfig = new KlocworkDesktopConfig(buildSpec, projectDir, cleanupProject, reportFile,
                additionalOptions, incrementalAnalysis, context.klocworkDiffAnalysisConfig);
	}


}