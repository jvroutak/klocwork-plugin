package com.emenda.klocwork;

import com.emenda.klocwork.config.KlocworkReportConfig;
import com.emenda.klocwork.config.KlocworkServerLoadConfig;
import com.emenda.klocwork.util.KlocworkUtil;
<<<<<<< HEAD
import hudson.*;
=======

import jenkins.tasks.SimpleBuildStep;

import hudson.AbortException;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Proc;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractBuild;
>>>>>>> add_build_action
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
<<<<<<< HEAD
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
=======

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
>>>>>>> add_build_action
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
<<<<<<< HEAD
=======
import java.io.StringReader;
import java.lang.InterruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
>>>>>>> add_build_action

public class KlocworkServerLoadBuilder extends Builder implements SimpleBuildStep {
    // TODO - artifact build.log, parse.log, kwloaddb.log if build fails
    private final KlocworkServerLoadConfig serverConfig;
    private KlocworkReportConfig reportConfig;

    @DataBoundConstructor
    public KlocworkServerLoadBuilder(KlocworkServerLoadConfig serverConfig) {
        this.serverConfig = serverConfig;
        this.reportConfig = new KlocworkReportConfig(true);
    }

    public KlocworkServerLoadBuilder(KlocworkServerLoadConfig serverConfig,
    KlocworkReportConfig reportConfig) {
        this.serverConfig = serverConfig;
        this.reportConfig = reportConfig;
    }

    @DataBoundSetter
    public void setReportConfig(KlocworkReportConfig reportConfig) {
        this.reportConfig = reportConfig;
    }

    public KlocworkServerLoadConfig getServerConfig() {
        return serverConfig;
    }

    public KlocworkReportConfig getReportConfig() {
        return reportConfig;
    }

    @Override
    public void perform(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener)
        throws AbortException {
        EnvVars envVars = null;
        try {
            envVars = build.getEnvironment(listener);
        } catch (IOException | InterruptedException ex) {
            throw new AbortException(ex.getMessage());
        }
        perform(build, envVars, workspace, launcher, listener);
    }


    public void perform(Run<?, ?> build, EnvVars envVars, FilePath workspace, Launcher launcher, TaskListener listener)
        throws AbortException {
        KlocworkLogger logger = new KlocworkLogger("KlocworkServerLoadConfig", listener.getLogger());
        logger.logMessage("Starting Klocwork Server Analysis Load Step");

        // validate server settings needed for build-step. AbortException is
        // thrown if URL and server project are not provided as we cannot perform
        // a server analysis without these settings
        KlocworkUtil.validateServerConfigs(envVars);

        // cannot check return code of kwadmin --version as it is non-zero
        // for some reason...
        KlocworkUtil.executeCommand(launcher, listener,
                workspace, envVars,
                serverConfig.getVersionCmd(), true);

        KlocworkUtil.executeCommand(launcher, listener,
                workspace, envVars,
                serverConfig.getKwadminLoadCmd(envVars, workspace));

        createBuildAction(logger, build, envVars, launcher);

    }

    private void createBuildAction(KlocworkLogger logger, Run<?, ?> build, EnvVars envVars,
    Launcher launcher) throws AbortException {
        String request = KlocworkUtil.createKlocworkAPIRequest("search", reportConfig.getQuery(), envVars);
        logger.logMessage("Using query: " + request);
        JSONArray response = KlocworkUtil.getJSONRespose(request, envVars, launcher);
        logger.logMessage("Number of issues returned : " + Integer.toString(response.size()));

        Map<String, Integer> severityMap = new HashMap<String,Integer>();
        for (int i = 0; i < response.size(); i++) {
            String severity = response.getJSONObject(i).getString("severity");
            if (StringUtils.isEmpty(severity)) {
                logger.logMessage(String.format("WARNING: found empty severity %s", severity));
            } else {
                // increment count
                severityMap.put(severity, severityMap.getOrDefault(severity, 0) + 1);
            }
        }
        
        build.addAction(new KlocworkBuildAction(build, severityMap, envVars, serverConfig.getBuildName(), reportConfig));
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public String getDisplayName() {
            return KlocworkConstants.KLOCWORK_SERVER_LOAD_DISPLAY_NAME;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {

            save();
            return super.configure(req,formData);
        }
    }
}
