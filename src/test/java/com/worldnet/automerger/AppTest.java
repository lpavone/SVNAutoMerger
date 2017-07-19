package com.worldnet.automerger;

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
        String output = merger.checkoutBranch(SOURCE_BRANCH);
        assertTrue( merger.isSuccessfulCheckout(output));
    }

    public void testUpdate() {
        String output = merger.updateBranch(SOURCE_BRANCH);
        assertTrue( merger.isSuccessfulUpdate(output));
    }

    public void testRevert() {
        String output = merger.revertChanges(TARGET_BRANCH);
        assertTrue( true);//ok if no exception
    }

    public void testMergeInfo() {
        merger.mergeInfoEligibleRevisions(SOURCE_BRANCH, TARGET_BRANCH);
        merger.mergeInfoMergedRevisions(SOURCE_BRANCH, TARGET_BRANCH);
        assertTrue( true);//ok if no exception
    }

    public void testCheckoutOrUpdateTargetBranch() throws Exception {
        merger.checkoutOrUpdateTargetBranch(TARGET_BRANCH);
        assertTrue( true);//ok if no exception
    }

    public void testMerge() {
        setRevisionsRange();
        String output = merger.updateBranch(TARGET_BRANCH);
        assertTrue( merger.isSuccessfulUpdate(output));
        String output2 = merger.merge(SOURCE_BRANCH, TARGET_BRANCH, fromRev, toRev);
        assertTrue( merger.isSuccessfulMerge(output2));//ok if no exception
    }

    public void testCommitMessageFileCreation() throws IOException {
        merger.createCommitMessageFile(COMMIT_MSG_FILE_PATH, SOURCE_BRANCH, TARGET_BRANCH, 12045, 12099, "9999");
        assertTrue( true);//ok if no exception
    }

    public void testCommit(){
        String output = merger.commit(TARGET_BRANCH, COMMIT_MSG_FILE_PATH);
        assertTrue( merger.isSuccessfulCommit(output));
    }

    private void setRevisionsRange(){
        String eligibleRevisions = merger.mergeInfoEligibleRevisions(SOURCE_BRANCH, TARGET_BRANCH);
        if (StringUtils.isNotBlank(eligibleRevisions)){
            fromRev = merger.getFromRevision(eligibleRevisions);
            toRev = merger.getToRevision(eligibleRevisions);
        } else {
            Assert.fail("No Eligible Revisions");
        }

    }

    public void testBuild(){
        boolean result = merger.isBuildSuccessful(TARGET_BRANCH);
        assertTrue( result);
    }

    public void testEmailNotification(){
        Notifier.notifyFailedBuild(SOURCE_BRANCH, TARGET_BRANCH,
            38, 45);//, "r38\nr39\nr40\nr45");
        Notifier.notifyCommitFailure(SOURCE_BRANCH, TARGET_BRANCH,
            38, 45);//, "r38\nr39\nr40\nr45");
    }

    public void testCopyPropertiesFile() throws Exception {
        merger.createLocalConfigFile(TARGET_BRANCH);
        assertTrue( true);//ok if no exception
    }

    /**
     * Main test case, will try to perform a merge and commit the changes.
     */
    public void testFullMergeProcess() throws Exception {
        merger.performMerge(SOURCE_BRANCH, TARGET_BRANCH, "9999");
    }

}
