package com.worldnet.automerger;

import com.worldnet.automerger.commands.BuildCheck;
import com.worldnet.automerger.commands.CheckoutBranch;
import com.worldnet.automerger.commands.Commit;
import com.worldnet.automerger.commands.ConflictSolver;
import com.worldnet.automerger.commands.LastRevisionLog;
import com.worldnet.automerger.commands.Merge;
import com.worldnet.automerger.commands.MergeInfoRevisions;
import com.worldnet.automerger.commands.RevertChanges;
import com.worldnet.automerger.commands.StatusCheck;
import com.worldnet.automerger.commands.UpdateBranch;
import com.worldnet.automerger.notification.Notifier;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private static String SOURCE_BRANCH = "VERSION_6_0_0_0";
    private static String TARGET_BRANCH = "VERSION_5_2_0_0";
    private static String COMMIT_MSG_FILE_PATH = PropertiesUtil.getString("tmp.commit.message.file");
    private Merger merger = new Merger();

    private int fromRev;
    private int toRev;

    public void checkout() {
        CheckoutBranch cmd = new CheckoutBranch(SOURCE_BRANCH);
        cmd.execute();
    }

    public void update() {
        UpdateBranch cmd = new UpdateBranch(SOURCE_BRANCH);
        cmd.execute();

    }

    public void revert() {
        RevertChanges cmd = new RevertChanges(TARGET_BRANCH);
        cmd.execute();
    }

    public void mergeInfo() {
        MergeInfoRevisions cmdElig = new MergeInfoRevisions(SOURCE_BRANCH, TARGET_BRANCH,
            SvnOperationsEnum.MERGEINFO_ELIGIBLE);
        cmdElig.execute();
        MergeInfoRevisions cmdMerged = new MergeInfoRevisions(SOURCE_BRANCH, TARGET_BRANCH,
            SvnOperationsEnum.MERGEINFO_MERGED);
        cmdMerged.execute();
    }

    public void checkoutOrUpdateTargetBranch() throws Exception {
        merger.checkoutOrUpdateBranch(TARGET_BRANCH);
    }

    public void merge() {
        setRevisionsRange();
        Merge cmd = new Merge(SOURCE_BRANCH, TARGET_BRANCH, fromRev, toRev);
    }

    public void commitMessageFileCreation() throws IOException {
        merger.createCommitMessageFile(COMMIT_MSG_FILE_PATH, SOURCE_BRANCH, TARGET_BRANCH, 12045, 12099, "9999");
    }

    public void commit(){
        Commit cmd = new Commit(TARGET_BRANCH, COMMIT_MSG_FILE_PATH);
    }

    private void setRevisionsRange(){
        MergeInfoRevisions cmdElig = new MergeInfoRevisions(SOURCE_BRANCH, TARGET_BRANCH,
            SvnOperationsEnum.MERGEINFO_ELIGIBLE);
        String eligibleRevisions = cmdElig.execute();
        if (StringUtils.isNotBlank(eligibleRevisions)){
            fromRev = merger.getFromRevision(eligibleRevisions);
            toRev = merger.getToRevision(eligibleRevisions);
        } else {
            Assert.fail("No Eligible Revisions");
        }
    }

    public void build(){
        BuildCheck cmd = new BuildCheck(TARGET_BRANCH);
    }

    public void emailNotification(){
//        Notifier.notifyCommitFailure(SOURCE_BRANCH, TARGET_BRANCH, 23, 30,
//            "commit output...");
//        Notifier.notifyFailedBuild(SOURCE_BRANCH, TARGET_BRANCH, 38, 45,
//            "<buildOutputHere>");
//        Notifier.notifyCssCompilationFail(SOURCE_BRANCH, TARGET_BRANCH, 38, 45,
//            "output.....");
//        Notifier.notifyMergeWithConflicts(SOURCE_BRANCH, TARGET_BRANCH, 38, 45);
//        Notifier.notifyNoEligibleVersions(SOURCE_BRANCH, TARGET_BRANCH);
        String lastRevisionLog = new LastRevisionLog(TARGET_BRANCH).execute();
        Notifier.notifySuccessfulMerge(SOURCE_BRANCH, TARGET_BRANCH, 38, 45,
            "merged revisions output...", "resolve conflicts output",
            true, lastRevisionLog);
    }

    public void copyPropertiesFile() throws Exception {
        merger.createLocalConfigFile(TARGET_BRANCH);
    }

    public void conflictsResolver(){
        ConflictSolver cmd = new ConflictSolver(TARGET_BRANCH);
        cmd.execute();
        StatusCheck stCmd = new StatusCheck(TARGET_BRANCH);
        stCmd.execute();
    }

    /**
     * Main test case, will try to perform a merge and commit the changes.
     */
    public void fullMergeProcess() throws Exception {
        merger.performMerge(SOURCE_BRANCH, TARGET_BRANCH, "9999");
    }

}
