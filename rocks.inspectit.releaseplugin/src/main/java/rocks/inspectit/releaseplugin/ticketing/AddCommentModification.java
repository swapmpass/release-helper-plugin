package rocks.inspectit.releaseplugin.ticketing;

import hudson.Extension;
import hudson.model.Descriptor;
import org.apache.commons.lang.text.StrSubstitutor;
import org.kohsuke.stapler.DataBoundConstructor;
import rocks.inspectit.releaseplugin.IssueUpdateBuilder;
import rocks.inspectit.releaseplugin.JIRAAccessTool;
import rocks.inspectit.releaseplugin.JIRAAccessTool.BuildingLambda;

import java.io.PrintStream;


/**
 * Modification selectable for tickets to add a version to the list of affected version.
 *
 * @author Jonas Kunz
 */
public class AddCommentModification extends TicketModification {

    /**
     * The comment text to add.
     */
    private String commentBody;

    /**
     * Constructor.
     *
     * @param commentBody the body of the comment to add
     */
    @DataBoundConstructor
    public AddCommentModification (String commentBody) {
        super();
        this.commentBody = commentBody;
    }


    public String getCommentBody () {
        return commentBody;
    }

    @Override
    public void apply (String ticketKey, JIRAAccessTool jira, final StrSubstitutor varReplacer, PrintStream logger) {
        if (commentBody != null && ! commentBody.isEmpty()) {
            //find the field definition
            jira.updateTicket(ticketKey, new BuildingLambda<IssueUpdateBuilder>() {
                @Override
                public void build (IssueUpdateBuilder b) {
                    b.addComment(varReplacer.replace(commentBody));
                }
            });
        }
    }

    /**
     * Descriptor implementation.
     *
     * @author JKU
     */
    @Extension
    public static class DescribtorImpl extends Descriptor<TicketModification> {

        @Override
        public String getDisplayName () {
            return "Add Comment";
        }

    }
}
