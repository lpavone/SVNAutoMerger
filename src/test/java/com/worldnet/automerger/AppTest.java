package com.worldnet.automerger;

import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{

    private static String AUTOMERGER = "Auto-Merger";
    private static String AUTOMERGER2 = "Auto-Merger_2";
    private static String COMMIT_MSG_FILE_PATH = "/tmp/commit_msg";
    private Merger merger = new Merger();
    static final Logger logger = LogManager.getLogger();

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
        String output = merger.checkoutBranch(AUTOMERGER);
        assertTrue( merger.isSuccessfulCheckout(output));
    }

    public void testUpdate() {
        String output = merger.updateBranch(AUTOMERGER);
        assertTrue( merger.isSuccessfulUpdate(output));
    }

    public void testRevert() {
        String output = merger.revertChanges(AUTOMERGER);
        assertTrue( true);//ok if no exception
    }

    public void testMergeInfo() {
        merger.mergeInfoEligibleRevisions(AUTOMERGER, AUTOMERGER2);
        merger.mergeInfoMergedRevisions(AUTOMERGER, AUTOMERGER2);
        assertTrue( true);//ok if no exception
    }

    public void testCheckoutOrUpdateTargetBranch() throws Exception {
        merger.checkoutOrUpdateTargetBranch(AUTOMERGER2);
        assertTrue( true);//ok if no exception
    }

    public void testMerge() {
        setRevisionsRange();
        String output = merger.merge(AUTOMERGER, AUTOMERGER2, fromRev, toRev);
        assertTrue( merger.isSuccessfulMerge(output));//ok if no exception
    }

    public void testCommitMessageFileCreation() throws IOException {
        merger.createCommitMessageFile(COMMIT_MSG_FILE_PATH, AUTOMERGER, AUTOMERGER2, 12045, 12099, "9999");
        assertTrue( true);//ok if no exception
    }

    public void testCommit(){
        String output = merger.commit(AUTOMERGER2, COMMIT_MSG_FILE_PATH);
        assertTrue( merger.isSuccessfulCommit(output));
    }

    private void setRevisionsRange(){
        String eligibleRevisions = merger.mergeInfoEligibleRevisions(AUTOMERGER, AUTOMERGER2);
        fromRev = merger.getFromRevision(eligibleRevisions);
        toRev = merger.getToRevision(eligibleRevisions);
    }

}
