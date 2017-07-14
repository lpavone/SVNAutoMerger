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

import com.jcabi.email.Envelope;
import com.jcabi.email.Postman;
import com.jcabi.email.Protocol;
import com.jcabi.email.Token;
import com.jcabi.email.enclosure.EnPlain;
import com.jcabi.email.stamp.StRecipient;
import com.jcabi.email.stamp.StSender;
import com.jcabi.email.stamp.StSubject;
import com.jcabi.email.wire.Smtp;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Leonardo Pavone - 12/07/17.
 */
public class Notifier {

  static final Logger logger = LogManager.getLogger();

  private static void sendEmail(String emailSubject, String emailBody) {
    logger.info("Notifying DEV and QA teams...");
    if (1==1) return;
    Postman postman = new Postman.Default(
        new Smtp(
            new Token(
                  PropertiesUtil.getString("email.user"),
                  PropertiesUtil.getString("email.password"))
                .access(new Protocol.Smtp("smtp.gmail.com", 587)
            )
        )
    );
    try {
      postman.send(
          new Envelope.Mime()
              .with(new StSender(PropertiesUtil.getString("email.sender")))
              .with(new StRecipient("Dev Team", "dev@worldnettps.com"))
              .with(new StRecipient("QA Team", "qa@worldnettps.com"))
              .with(new StSubject(emailSubject))
              .with(new EnPlain(emailBody))
      );
    } catch (IOException e) {
      logger.error("Email notifications failed", e);
    }
  }

  public static void notifyMergeWithConflicts(String sourceBranch, String targetBranch,
      int fromRevision, int toRevision) {
    String subject = String.format("[AUTO-MERGER] Conflicts have been found during merge (%s -> %s)", sourceBranch, targetBranch);
    String body = String.format(
        "Conflicts have been found attempting to merge branch %s into %s (from revision %s to %s), manual merge is required.",
        sourceBranch, targetBranch, fromRevision, toRevision);
    sendEmail(subject, body);
  }

  public static void notifyCommitFailure(String sourceBranch, String targetBranch, int fromRevision,
      int toRevision) {
    String subject = String.format("[AUTO-MERGER] Error during merge commit (%s -> %s)", sourceBranch, targetBranch);
    String body = String.format(
        "Error attempting to commit merge result of branch %s into %s (from revision %s to %s), manual merge is required.",
        sourceBranch, targetBranch, fromRevision, toRevision);
    sendEmail(subject, body);
  }

  public static void notifySuccessfulMerge(String sourceBranch, String targetBranch,
      int fromRevision, int toRevision, String mergedRevisions) {
    String subject = String.format("[AUTO-MERGER] Changes have been Merged (%s -> %s)", sourceBranch, targetBranch);
    String body = String.format(
        "Changes have been successfully merged from branch %s into %s (from revision %s to %s).\n\n"+
        "Current merged revisions:\n\n%s",
        sourceBranch, targetBranch, fromRevision, toRevision, mergedRevisions);
    sendEmail(subject, body);
  }
}
