/* All materials herein: Copyright (c) 2017 Worldnet TPS Ltd. All Rights Reserved.
 *
 * These materials are owned by Worldnet TPS Ltd and are protected by copyright laws
 * and international copyright treaties, as well as other intellectual property laws
 * and treaties.
 *
 * All right, title and interest in the copyright, confidential information,
 * patents, design rights and all other intellectual property rights of
 * whatsoever nature in and to these materials are and shall remain the sole
 * and exclusive property of Worldnet TPS Ltd.
 */

package com.worldnet.automerger;

import com.worldnet.automerger.commands.BuildCheck;
import com.worldnet.automerger.commands.CheckoutBranch;
import com.worldnet.automerger.commands.CommandExecutor;
import com.worldnet.automerger.commands.Commit;
import com.worldnet.automerger.commands.ConflictSolver;
import com.worldnet.automerger.commands.CssCompilation;
import com.worldnet.automerger.commands.Merge;
import com.worldnet.automerger.commands.MergeInfoRevisions;
import com.worldnet.automerger.commands.RevertChanges;
import com.worldnet.automerger.commands.StatusCheck;
import com.worldnet.automerger.commands.UpdateBranch;
import com.worldnet.automerger.notification.Notifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Leonardo Pavone - 11/07/17.
 */
public class Merger {

  static final Logger logger = LogManager.getLogger();

  public void performMerge(String sourceBranch, String targetBranch, String redmineTaskNumber) throws Exception {
    logger.info("Attempting automatic merge of changes from {} to {}", sourceBranch, targetBranch);

    String eligibleRevisions =
        new MergeInfoRevisions( sourceBranch, targetBranch, SvnOperationsEnum.MERGEINFO_ELIGIBLE)
        .execute();
    if (StringUtils.isBlank( eligibleRevisions)){
      logger.info("No eligible revisions from {} to {}, merge aborted.", sourceBranch, targetBranch);
      Notifier.notifyNoEligibleVersions(sourceBranch, targetBranch);
      return;
    }

    checkoutOrUpdateBranch(sourceBranch);
    checkoutOrUpdateBranch(targetBranch);

    int fromRevision = getFromRevision( eligibleRevisions);
    int toRevision = getToRevision( eligibleRevisions);

    Merge mergeCmd = new Merge(sourceBranch, targetBranch, fromRevision, toRevision);
    mergeCmd.execute();
    String resolveConflictOutput = StringUtils.EMPTY;
    if ( !mergeCmd.wasSuccessful()){
      //resolve automatically CSS conflicts to proceed
      logger.info("Conflicts have been found during merge. Will attempt to resolve CSS conflicts and re-check status.");
      resolveConflictOutput = new ConflictSolver(targetBranch)
          .execute();
      StatusCheck statusCheckCmd = new StatusCheck(targetBranch);
      String statusOutput = statusCheckCmd.execute();
      //check if after resolution there are still conflicts
      if ( !statusCheckCmd.wasSuccessful()) {
        logger.info("Conflicts have been found during merge, no changes will be committed.");
        Notifier.notifyMergeWithConflicts(sourceBranch, targetBranch, fromRevision, toRevision,
            statusOutput);
        return;
      } else{
        logger.info("CSS conflicts have been resolved, will attempt to recompile CSS files");
        //run and check result of CSS compilation
        CssCompilation cssCompilationCmd = new CssCompilation(targetBranch);
        String cssCompilationOutput = cssCompilationCmd.execute();
        if ( !cssCompilationCmd.wasSuccessful()){
          logger.info("CSS compilation has failed, merge aborted.");
          Notifier.notifyCssCompilationFail(sourceBranch, targetBranch, fromRevision, toRevision,
              cssCompilationOutput);
          return;
        }
      }
    }
    //check if build is ok after merge
    BuildCheck buildCheckCmd = new BuildCheck(targetBranch);
    String buildOutput = buildCheckCmd.execute();
    if ( !buildCheckCmd.wasSuccessful()) {
      logger.info("Build has failed after merge. Changes will not be committed.");
      Notifier.notifyFailedBuild(sourceBranch, targetBranch, fromRevision, toRevision, buildOutput);
      return;
    }
    //commit is only done if mode is enabled from config
    boolean isCommitModeEnabled = BooleanUtils.toBoolean(
        PropertiesUtil.getString("enable.commit.mode"));
    if (isCommitModeEnabled){
      logger.info("Successful merge, changes will be committed in Redmine task #{}", redmineTaskNumber);
      String commitMessageFilePath = PropertiesUtil.getString("tmp.commit.message.file");
      createCommitMessageFile( commitMessageFilePath, sourceBranch, targetBranch,
          fromRevision, toRevision, redmineTaskNumber);
      //commit changes
      Commit commitCmd = new Commit(targetBranch, commitMessageFilePath);
      String commitOutput = commitCmd.execute();
      if ( commitCmd.wasSuccessful()){
        String mergedRevisions =
            new MergeInfoRevisions( sourceBranch, targetBranch, SvnOperationsEnum.MERGEINFO_MERGED)
                .execute();
        logger.info("Changes have been successfully committed.");
        logger.info("Merged revisions:\n%s", mergedRevisions);
        Notifier.notifySuccessfulMerge(sourceBranch, targetBranch, fromRevision, toRevision,
            mergedRevisions, resolveConflictOutput, false);
      } else {
        logger.info("Commit failed! No changes have been committed into repository");
        Notifier.notifyCommitFailure(sourceBranch, targetBranch, fromRevision, toRevision,
            commitOutput);
      }
      Utils.removeTempFile( commitMessageFilePath);
      logger.info("Finished automatic merge of changes from {} to {}", sourceBranch, targetBranch);
    } else {
      logger.info("Commit mode is disabled, no commit will be done.");
      Notifier.notifySuccessfulMerge(sourceBranch, targetBranch, fromRevision, toRevision,
          "[commit disabled]", resolveConflictOutput, true);
    }
  }

  /**
   * Creates a temp file with commit message content.
   * @param commitMessageFilePath
   * @param sourceBranch
   * @param targetBranch
   * @param fromRevision
   * @param toRevision
   * @param redmineTicketNumber
   * @throws IOException
   */
  public void createCommitMessageFile(String commitMessageFilePath, String sourceBranch,
      String targetBranch, int fromRevision, int toRevision, String redmineTicketNumber)
      throws IOException {

    String msgContent = String.format(PropertiesUtil.getString("commit.message.template"),
        redmineTicketNumber,
        sourceBranch,
        targetBranch,
        fromRevision,
        toRevision,
        redmineTicketNumber);

    Files.write(Paths.get(commitMessageFilePath), msgContent.getBytes());
  }

  /**
   * Return last revision to merge
   * @param eligibleRevisions
   * @return
   */
  public int getToRevision(String eligibleRevisions) {
    String[] revisions = StringUtils.split(eligibleRevisions, System.getProperty("line.separator"));
    return Integer.parseInt(StringUtils.remove( revisions[revisions.length - 1], "r"));
  }

  /**
   * Return initial revision to merge
   * @param eligibleRevisions 
   * @return
   */
  public int getFromRevision(String eligibleRevisions) {
    String revision = StringUtils.split(eligibleRevisions, System.getProperty("line.separator"))[0];
    return Integer.parseInt(StringUtils.remove(revision, "r"));
  }

  /**
   * Checkout the working copy of target branch.
   * If already exits will do:
   *  - Revert: to remove any possible unwanted changes
   *  - Update: to update latest changes from repository
   * @param branchName
   */
  public void checkoutOrUpdateBranch(String branchName) throws Exception {
    boolean branchDirExists = new File(SvnUtils.TEMP_FOLDER + "/" + branchName).exists();
    if (branchDirExists){
      RevertChanges revertChangesCmd = new RevertChanges(branchName);
      revertChangesCmd.execute();
      UpdateBranch updateBranchCmd = new UpdateBranch( branchName);
      updateBranchCmd.execute();
      if ( !updateBranchCmd.wasSuccessful()){
        throw new Exception("Error updating working copy");
      }
    } else {
      CheckoutBranch checkoutBranchCmd = new CheckoutBranch( branchName);
      checkoutBranchCmd.execute();
      if ( !checkoutBranchCmd.wasSuccessful()){
        throw new Exception("Error checking out working copy");
      }
      createLocalConfigFile(branchName);
    }
  }

  /**
   * Create localconf folder and properties file necessary to run build task in order to check merge integrity.
   * @param branchName
   */
  public void createLocalConfigFile(String branchName) throws Exception {
    String branchPath = SvnUtils.TEMP_FOLDER + "/" + branchName;
    //run script to set up project
    String scriptSetupPath = PropertiesUtil.getString("script.setup.path");
    String scriptCommand = String.format("%s %s", scriptSetupPath, branchName);
    CommandExecutor.run( scriptCommand, branchPath);
    logger.info("worldnettps.properties file has been created in localconf folder.");
  }

}
