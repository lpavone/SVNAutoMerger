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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Leonardo Pavone - 11/07/17.
 */
public class Merger {

  private static String SVN_USER = PropertiesUtil.getString("svn.username");
  private static String SVN_PASSWORD = PropertiesUtil.getString("svn.password");
  private static String TEMP_FOLDER = PropertiesUtil.getString("temp.folder");
  private static String BASE_REPO = PropertiesUtil.getString("base.repository.path");
  private static String SVN_ERROR_PREFIX = "svn: E";
  private static String COMMIT_MSG_TMPL = "Feature #%s - Merge changes from %s into %s\n\n* [AUTO-MERGE] Revisions merged: -r%s:%s\n\nrefs #%s @00h05m";


  static final Logger logger = LogManager.getLogger();

  public void performMerge(String sourceBranch, String targetBranch,String redmineTaskNumber) throws Exception {
    logger.info("Attempting automatic merge of changes from {} to {}", sourceBranch, targetBranch);
    String eligibleRevisions = mergeInfoEligibleRevisions( sourceBranch, targetBranch);
    if (StringUtils.isNotBlank( eligibleRevisions)){
      checkoutOrUpdateTargetBranch(sourceBranch);
      checkoutOrUpdateTargetBranch(targetBranch);
      int fromRevision = getFromRevision(eligibleRevisions);
      int toRevision = getToRevision(eligibleRevisions);

      String mergeOutput = merge( sourceBranch, targetBranch, fromRevision, toRevision);

      if (isSuccessfulMerge(mergeOutput)){
        logger.info("Successful merge, changes will be committed in Redmine task #{}", redmineTaskNumber);
        String commitMessageFilePath = PropertiesUtil.getString("tmp.commit.message.file");
        createCommitMessageFile(
            commitMessageFilePath,
            sourceBranch,
            targetBranch,
            fromRevision,
            toRevision,
            redmineTaskNumber);

        String commitOutput = commit( targetBranch, commitMessageFilePath);

        if (isSuccessfulCommit(commitOutput)){
          logger.info("Changes have been successfully committed.");
          logger.info("Logging merged revisions:");
          String mergedRevisions = mergeInfoMergedRevisions( sourceBranch, targetBranch);
          Notifier.notifySuccessfulMerge(sourceBranch, targetBranch, fromRevision, toRevision, mergedRevisions);
        } else {
          logger.info("Commit was not successful! No changes have been committed into repository");
          Notifier.notifyCommitFailure(sourceBranch, targetBranch, fromRevision, toRevision);
        }

      } else {
        logger.info("Conflicts have been found during merge, no changes will be committed.");
        Notifier.notifyMergeWithConflicts(sourceBranch, targetBranch, fromRevision, toRevision);
      }
      
    } else {
      logger.info("No eligible revisions from {} to {}, merge aborted.",
        sourceBranch,
          targetBranch);
    }
    logger.info("Finished automatic merge of changes from {} to {}", sourceBranch, targetBranch);
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

    String msgContent = String.format( COMMIT_MSG_TMPL,
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
    //decrease the revision by 1 to include the first one eligible
    return Integer.parseInt(StringUtils.remove(revision, "r")) - 1;
  }

  /**
   * Checkout the working copy of target branch.
   * If already exits will do:
   *  - Revert: to remove any possible unwanted changes
   *  - Update: to update latest changes from repository
   * @param targetBranch
   */
  public void checkoutOrUpdateTargetBranch(String targetBranch) throws Exception {
    boolean branchDirExists = new File(TEMP_FOLDER + "/" + targetBranch).exists();
    if (branchDirExists){
      revertChanges( targetBranch);
      String output = updateBranch( targetBranch);
      if ( !isSuccessfulUpdate( output)){
        throw new Exception("Error updating working copy");
      }
    } else {
      String output = checkoutBranch( targetBranch);
      if ( !isSuccessfulCheckout( output)){
        throw new Exception("Error checking out working copy");
      }
    }
  }

  public String checkoutBranch(String branchName){
    String command = String.format( SvnOperationsEnum.CHECKOUT.command(),
        BASE_REPO + branchName,
        SVN_USER,
        SVN_PASSWORD);

    return CommandExecutor.run(command, TEMP_FOLDER);
  }

  public String updateBranch(String branchName){
    String command = String.format( SvnOperationsEnum.UPDATE.command(),
        SVN_USER,
        SVN_PASSWORD);

    return CommandExecutor.run(command, TEMP_FOLDER + "/" + branchName);
  }

  public String revertChanges(String branchName){
    String command = String.format( SvnOperationsEnum.REVERT.command(),
        SVN_USER,
        SVN_PASSWORD);

    return CommandExecutor.run(command, TEMP_FOLDER + "/" + branchName);
  }

  public String merge(String sourceBranch, String targetBranch, int fromRevision, int toRevision){
    String command = String.format( SvnOperationsEnum.MERGE.command(),
        fromRevision,
        toRevision,
        BASE_REPO + sourceBranch,
        SVN_USER,
        SVN_PASSWORD);

    return CommandExecutor.run(command, TEMP_FOLDER + "/" + targetBranch);
  }

  public String mergeInfoEligibleRevisions(String sourceBranch, String targetBranch){
    String command = String.format( SvnOperationsEnum.MERGEINFO_ELIGIBLE.command(),
        BASE_REPO + sourceBranch,
        BASE_REPO + targetBranch,
        SVN_USER,
        SVN_PASSWORD);

    return CommandExecutor.run(command, TEMP_FOLDER);
  }

  public String mergeInfoMergedRevisions(String sourceBranch, String targetBranch){
    String command = String.format( SvnOperationsEnum.MERGEINFO_MERGED.command(),
        BASE_REPO + sourceBranch,
        BASE_REPO + targetBranch,
        SVN_USER,
        SVN_PASSWORD);

    return CommandExecutor.run(command, TEMP_FOLDER);
  }

  public String commit(String branchName, String messageFilePath){
    String command = String.format( SvnOperationsEnum.COMMIT.command(),
        messageFilePath,
        SVN_USER,
        SVN_PASSWORD);

    return CommandExecutor.run(command, TEMP_FOLDER + "/" + branchName);
  }

  public boolean isSuccessfulCheckout(String output){
    return StringUtils.contains(output, "Checked out revision")
        && !StringUtils.contains(output, SVN_ERROR_PREFIX);
  }

  public  boolean isSuccessfulUpdate(String output){
    return StringUtils.contains(output, "At revision")
        && !StringUtils.contains(output, SVN_ERROR_PREFIX);
  }

  /**
   * Check if merge was successful based on command output.
   * Any kind of error or conflict is considered not successful.
   * @param mergeOutput
   * @return
   */
  public  boolean isSuccessfulMerge(String mergeOutput) {
    return StringUtils.contains(mergeOutput, "Recording mergeinfo")
        && !StringUtils.contains(mergeOutput, SVN_ERROR_PREFIX)
        && !StringUtils.contains(mergeOutput, "Summary of conflicts:");
  }

  public boolean isSuccessfulCommit(String commitOutput) {
    return StringUtils.contains(commitOutput, "Committed revision")
        && !StringUtils.contains(commitOutput, SVN_ERROR_PREFIX);
  }
}
