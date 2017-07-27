package com.worldnet.automerger;

import com.worldnet.automerger.commands.BuildCheck;
import com.worldnet.automerger.commands.CheckoutBranch;
import com.worldnet.automerger.commands.Commit;
import com.worldnet.automerger.commands.ConflictSolver;
import com.worldnet.automerger.commands.Merge;
import com.worldnet.automerger.commands.MergeInfoRevisions;
import com.worldnet.automerger.commands.RevertChanges;
import com.worldnet.automerger.commands.StatusCheck;
import com.worldnet.automerger.commands.UpdateBranch;
import com.worldnet.automerger.notification.Notifier;
import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{

    private static String SOURCE_BRANCH = "VERSION_4_4_0_0";
    private static String TARGET_BRANCH = "VERSION_4_5_0_0";
    private static String COMMIT_MSG_FILE_PATH = PropertiesUtil.getString("tmp.commit.message.file");
    private Merger merger = new Merger();

    private int fromRev;
    private int toRev;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public void testCheckout() {
        CheckoutBranch cmd = new CheckoutBranch(SOURCE_BRANCH);
        cmd.execute();
        assertTrue( cmd.wasSuccessful());
    }

    public void testUpdate() {
        UpdateBranch cmd = new UpdateBranch(SOURCE_BRANCH);
        cmd.execute();
        assertTrue( cmd.wasSuccessful());
    }

    public void testRevert() {
        RevertChanges cmd = new RevertChanges(TARGET_BRANCH);
        cmd.execute();
        assertTrue( cmd.wasSuccessful());
    }

    public void testMergeInfo() {
        MergeInfoRevisions cmdElig = new MergeInfoRevisions(SOURCE_BRANCH, TARGET_BRANCH,
            SvnOperationsEnum.MERGEINFO_ELIGIBLE);
        cmdElig.execute();
        MergeInfoRevisions cmdMerged = new MergeInfoRevisions(SOURCE_BRANCH, TARGET_BRANCH,
            SvnOperationsEnum.MERGEINFO_MERGED);
        cmdMerged.execute();
        assertTrue(cmdElig.wasSuccessful() && cmdMerged.wasSuccessful());
    }

    public void testCheckoutOrUpdateTargetBranch() throws Exception {
        merger.checkoutOrUpdateBranch(TARGET_BRANCH);
        assertTrue( true);//ok if no exception
    }

    public void testMerge() {
        setRevisionsRange();
        Merge cmd = new Merge(SOURCE_BRANCH, TARGET_BRANCH, fromRev, toRev);
        assertTrue( cmd.wasSuccessful());
    }

    public void testCommitMessageFileCreation() throws IOException {
        merger.createCommitMessageFile(COMMIT_MSG_FILE_PATH, SOURCE_BRANCH, TARGET_BRANCH, 12045, 12099, "9999");
        assertTrue( true);//ok if no exception
    }

    public void testCommit(){
        Commit cmd = new Commit(TARGET_BRANCH, COMMIT_MSG_FILE_PATH);
        assertTrue( cmd.wasSuccessful());
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

    public void testBuild(){
        BuildCheck cmd = new BuildCheck(TARGET_BRANCH);
        assertTrue( cmd.wasSuccessful());
    }

    public void testEmailNotification(){
//        Notifier.notifyCommitFailure(SOURCE_BRANCH, TARGET_BRANCH, 23, 30,
//            "commit output...");
//        Notifier.notifyFailedBuild(SOURCE_BRANCH, TARGET_BRANCH, 38, 45,
//            "<buildOutputHere>");
//        Notifier.notifyCssCompilationFail(SOURCE_BRANCH, TARGET_BRANCH, 38, 45,
//            "output.....");
//        Notifier.notifyMergeWithConflicts(SOURCE_BRANCH, TARGET_BRANCH, 38, 45);
//        Notifier.notifyNoEligibleVersions(SOURCE_BRANCH, TARGET_BRANCH);
        Notifier.notifySuccessfulMerge(SOURCE_BRANCH, TARGET_BRANCH, 38, 45,
            "merged revisions output...", "resolve conflicts output",
            false);
    }

    public void testCopyPropertiesFile() throws Exception {
        merger.createLocalConfigFile(TARGET_BRANCH);
        assertTrue( true);//ok if no exception
    }

    public void testConflictsResolver(){
        ConflictSolver cmd = new ConflictSolver(TARGET_BRANCH);
        cmd.execute();
        StatusCheck stCmd = new StatusCheck(TARGET_BRANCH);
        stCmd.execute();
        assertTrue( stCmd.wasSuccessful());
    }

    /**
     * Main test case, will try to perform a merge and commit the changes.
     */
    public void testFullMergeProcess() throws Exception {
        merger.performMerge(SOURCE_BRANCH, TARGET_BRANCH, "9999");
    }

}
