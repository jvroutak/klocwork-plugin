
package com.emenda.klocwork.config;

import com.emenda.klocwork.KlocworkConstants;
import com.emenda.klocwork.util.KlocworkUtil;
<<<<<<< HEAD
=======

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import org.apache.commons.lang3.StringUtils;

>>>>>>> add_build_action
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ArgumentListBuilder;
<<<<<<< HEAD
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
=======
>>>>>>> add_build_action

public class KlocworkServerLoadConfig extends AbstractDescribableImpl<KlocworkServerLoadConfig> {

    private String tablesDir;
    private String buildName;
    private String additionalOpts;

    @DataBoundConstructor
    public KlocworkServerLoadConfig(String tablesDir) {
        this.tablesDir = tablesDir;
        this.buildName = "";
        this.additionalOpts = "";
    }

    public ArgumentListBuilder getVersionCmd() {
        ArgumentListBuilder versionCmd = new ArgumentListBuilder("kwadmin");
        versionCmd.add("--version");
        return versionCmd;
    }

    public ArgumentListBuilder getKwadminLoadCmd(EnvVars envVars, FilePath workspace) {
        ArgumentListBuilder kwadminCmd =
            new ArgumentListBuilder("kwadmin");
        kwadminCmd.add("--url", envVars.get(KlocworkConstants.KLOCWORK_URL));
        kwadminCmd.add("load");

        // add options such as --name of build
        kwadminCmd.add("--name", KlocworkUtil.getDefaultBuildName(buildName, envVars));

        kwadminCmd.add(envVars.get(KlocworkConstants.KLOCWORK_PROJECT));
        kwadminCmd.add(envVars.expand(KlocworkUtil.getDefaultKwtablesDir(tablesDir)));
        if (!StringUtils.isEmpty(additionalOpts)) {
            kwadminCmd.addTokenized(envVars.expand(additionalOpts));
        }
        return kwadminCmd;
    }

    @DataBoundSetter
    public void setTablesDir(String tablesDir) {
        this.tablesDir = tablesDir;
    }

    @DataBoundSetter
    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    @DataBoundSetter
    public void setAdditionalOpts(String additionalOpts) {
        this.additionalOpts = additionalOpts;
    }

    public String getTablesDir() { return tablesDir; }
    public String getBuildName() { return buildName; }
    public String getAdditionalOpts() { return additionalOpts; }

    @Extension
    public static class DescriptorImpl extends Descriptor<KlocworkServerLoadConfig> {
        public String getDisplayName() { return null; }
    }

}
