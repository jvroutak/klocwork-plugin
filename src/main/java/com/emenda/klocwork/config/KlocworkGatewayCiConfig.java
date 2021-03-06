
package com.emenda.klocwork.config;

import hudson.Extension;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Items;
import hudson.model.Run;
import org.kohsuke.stapler.DataBoundConstructor;


public class KlocworkGatewayCiConfig extends AbstractDescribableImpl<KlocworkGatewayCiConfig> {

    private final String threshold;
    private final String reportFile;
    private final boolean stopBuild;
    private final boolean failUnstable;
    
    @DataBoundConstructor
    public KlocworkGatewayCiConfig(String threshold, String reportFile, boolean stopBuild, boolean failUnstable) {
        this.threshold = threshold;
        this.reportFile = reportFile;
        this.stopBuild = stopBuild;
        this.failUnstable = failUnstable;
    }
    public String getThreshold() {
        return threshold;
    }

    public String getReportFile() {
        return reportFile;
    }

    public boolean getStopBuild() {
        return stopBuild;
    }
    
    public boolean getFailUnstable()
    {
    	return failUnstable;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<KlocworkGatewayCiConfig> {
        public String getDisplayName() { return null; }

        @Initializer(before = InitMilestone.PLUGINS_STARTED)
        public static void addAliases() {
            Items.XSTREAM2.addCompatibilityAlias("com.emenda.klocwork.config.KlocworkGatewayDesktopConfig", KlocworkGatewayCiConfig.class);
            Run.XSTREAM2.addCompatibilityAlias("com.emenda.klocwork.config.KlocworkGatewayDesktopConfig", KlocworkGatewayCiConfig.class);
        }

    }

}
