package rocks.inspectit.releaseplugin.versioning;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.apache.commons.lang.text.StrSubstitutor;
import org.kohsuke.stapler.DataBoundConstructor;
import rocks.inspectit.releaseplugin.AbstractJIRAAction;
import rocks.inspectit.releaseplugin.JIRAAccessTool;
import rocks.inspectit.releaseplugin.credentials.JIRAProjectCredentials;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Build action which allows creating and updating JIRA project versions.
 *
 * @author Jonas Kunz
 */
public class JIRAVersionEditor extends AbstractJIRAAction {

    /**
     * The modifications to apply.
     */
    private List<ModifyAddVersionTemplate> versionModifiactions;


    /**
     * Constructor, called by Jenkins.
     *
     * @param jiraCredentialsID    the jira credentials.
     * @param versionModifiactions the modification to apply.
     */
    @DataBoundConstructor
    public JIRAVersionEditor (String jiraCredentialsID, List<ModifyAddVersionTemplate> versionModifiactions) {
        super(jiraCredentialsID);
        this.versionModifiactions = versionModifiactions == null ? new ArrayList<ModifyAddVersionTemplate>() : versionModifiactions;
    }


    public List<ModifyAddVersionTemplate> getVersionModifiactions () {
        return versionModifiactions;
    }

    @Override
    public boolean perform (AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        StrSubstitutor varReplacer = getVariablesSubstitutor(build, listener);
        PrintStream logger = listener.getLogger();

        JIRAProjectCredentials cred = getJiraCredentials();
        JIRAAccessTool jira = new JIRAAccessTool(cred.getUrl(), cred.getUrlUsername(), cred.getUrlPassword(), null, cred.getProjectKey(), getJiraCredentialsID());

        for (ModifyAddVersionTemplate mod : versionModifiactions) {
            mod.applyModifications(jira, varReplacer, logger);
        }


        jira.destroy();

        return true;
    }


    /**
     * Descriptor class.
     *
     * @author JKU
     */
    @Extension
    public static class DescriptorImpl extends AbstractJIRAAction.DescriptorImpl {


        /**
         * Constructor.
         */
        public DescriptorImpl () {
            super();
            load();
        }

        @Override
        public String getDisplayName () {
            return "Add / Modify JIRA Project Versions";
        }


    }
}
