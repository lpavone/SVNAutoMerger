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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Leonardo Pavone - 12/07/17.
 */
public class Notifier {

  static final Logger logger = LogManager.getLogger();
  private static final String EMAIL_TMPL_CMD = "printf \"%s\" | sendmail %s";
  private static final String EMAIL_TMPL_CONTENT = "From: %s\nSubject: %s\n%s";

  private static void sendEmail(String emailSubject, String emailBody) {
    logger.info("About to notify DEV and QA teams...");
    String emailTempFileContent = String.format(
        EMAIL_TMPL_CONTENT,
        PropertiesUtil.getString("email.sender"),
        emailSubject,
        emailBody);
    try {
      String sendMailCommand = String.format(
          EMAIL_TMPL_CMD,
          emailTempFileContent,
          PropertiesUtil.getString("email.to.notify")
      );
      CommandExecutor.run( sendMailCommand, null);

    } catch (Exception e) {
      logger.error("Email notification has failed", e);
    }
  }

  public static void notifyMergeWithConflicts(String sourceBranch, String targetBranch,
      int fromRevision, int toRevision) {
    String subject = String.format("[AUTO-MERGER] Conflicts have been found during merge (%s -> %s)", sourceBranch, targetBranch);
    String body = String.format(
        "Conflicts have been found attempting to merge branch %s into %s (from revision %s to %s).\n"+
            "Manual merge is required.",
        sourceBranch, targetBranch, fromRevision, toRevision);
    sendEmail(subject, body);
  }

  public static void notifyCommitFailure(String sourceBranch, String targetBranch, int fromRevision,
      int toRevision) {
    String subject = String.format("[AUTO-MERGER] Error during merge commit (%s -> %s)", sourceBranch, targetBranch);
    String body = String.format(
        "Error attempting to commit merge result of branch %s into %s (from revision %s to %s).\n"+
            "Manual merge is required.",
        sourceBranch, targetBranch, fromRevision, toRevision);
    sendEmail(subject, body);
  }

  public static void notifySuccessfulMerge(String sourceBranch, String targetBranch,
      int fromRevision, int toRevision, String mergedRevisions) {
    String subject = String.format("[AUTO-MERGER] Changes have been merged (%s -> %s)", sourceBranch, targetBranch);
    String body = String.format(
        "Changes have been successfully merged from branch %s into %s (from revision %s to %s).\n"+
        "Current merged revisions:\n\n%s",
        sourceBranch, targetBranch, fromRevision, toRevision, mergedRevisions);
    sendEmail(subject, body);
  }

  public static void notifyFailedBuild(String sourceBranch, String targetBranch, int fromRevision,
      int toRevision) {
    String subject = String.format("[AUTO-MERGER] Failed build (%s -> %s)", sourceBranch, targetBranch);
    String body = String.format(
        "Build is broken after merge branch %s into %s (from revision %s to %s).\n"+
        "Changes have not been committed, manual investigation is required.",
        sourceBranch, targetBranch, fromRevision, toRevision);
    sendEmail(subject, body);
  }
}
