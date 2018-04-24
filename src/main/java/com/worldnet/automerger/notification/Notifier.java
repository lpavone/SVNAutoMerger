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

package com.worldnet.automerger.notification;

import com.worldnet.automerger.PropertiesUtil;
import com.worldnet.automerger.SvnUtils;
import com.worldnet.automerger.commands.CommandExecutor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Leonardo Pavone - 12/07/17.
 */
public class Notifier {

  static final Logger logger = LogManager.getLogger();
  private static final String EMAIL_TMPL_CMD = "sendmail -t < %s";
  private static final String EMAIL_TMPL_CONTENT =
      "From: Dev Team <%s>\nTo: %s\nSubject: %s\nContent-Type: text/html\n\n%s";

    private static void sendEmail(String emailSubject, String emailBody) {
        logger.info("About to notify DEV and QA teams...");

        try {
            String body = new String(Files.readAllBytes(
                Paths.get( PropertiesUtil.getString("email.template.path"))))
                .replace("${body}", emailBody);

            String msgContent = String.format(
                EMAIL_TMPL_CONTENT,
                PropertiesUtil.getString("email.sender"),
                PropertiesUtil.getString("email.to.notify"),
                emailSubject,
                body);

            String mailContentFilePath = PropertiesUtil.getString("temp.folder") + "/mail.txt";
            //write down mail content to FS to be picked by sendmail command
            Files.write(Paths.get(mailContentFilePath), msgContent.getBytes());

            String sendMailCommand = String.format(
                EMAIL_TMPL_CMD,
                mailContentFilePath
            );
            CommandExecutor.run(sendMailCommand, SvnUtils.TEMP_FOLDER);

        } catch (Exception e) {
            logger.error("Email notification has failed", e);
        }
    }

    public static void notifyMergeWithConflicts(String sourceBranch, String targetBranch,
        int fromRevision, int toRevision, String statusOutput) {
        String subject = String
            .format("[AUTO-MERGER] Conflicts have been found during merge (%s -> %s)",
                sourceBranch, targetBranch);
        String body = String.format(
            "<p>Conflicts have been found attempting to merge branch <mark>%s</mark> into <mark>%s</mark> "
                + "(from revision <mark>%s</mark> to <mark>%s</mark>), manual merge is required.</p>" +
                "\n\n************************************ SVN STATUS OUTPUT: ************************************\n%s",
            sourceBranch, targetBranch, fromRevision, toRevision, statusOutput);
        sendEmail(subject, body);
    }

    public static void notifyCommitFailure(String sourceBranch, String targetBranch,
        int fromRevision,
        int toRevision, String commitOutput) {
        String subject = String
            .format("[AUTO-MERGER] Error during commit (%s -> %s)", sourceBranch, targetBranch);
        String body = String.format(
            "<p>Error attempting to commit merge result of branch <mark>%s</mark> into <mark>%s</mark> "
                + "(from revision <mark>%s</mark> to <mark>%s</mark>), manual merge is required.</p>" +
                "\n\n************************************ COMMIT OUTPUT:************************************\n%s",
            sourceBranch, targetBranch, fromRevision, toRevision, commitOutput);
        sendEmail(subject, body);
    }

    public static void notifySuccessfulMerge(String sourceBranch, String targetBranch,
        int fromRevision, int toRevision, String mergedRevisions, String resolveConflictOutput,
        boolean isCommitModeDisabled) {
        String subject = String
            .format("[AUTO-MERGER] Changes have been merged (%s -> %s)", sourceBranch,
                targetBranch);
        StringBuilder body = new StringBuilder(
            String
                .format("<p>Changes have been successfully merged from branch <mark>%s</mark> into "
                        + "<mark>%s</mark> (from revision <mark>%s</mark> to <mark>%s</mark>).</p>\n\n"
                        + "<p>Output during CSS conflicts resolution: \n</p>%s\n\n"
                        + "<p>Current merged revisions: \n</p>%s",
                    sourceBranch, targetBranch, fromRevision, toRevision,
                    StringUtils.isNotBlank(resolveConflictOutput) ? resolveConflictOutput : "n/a",
                    mergedRevisions
                )
        );
        if (isCommitModeDisabled) {
            body.append(
                "\n\n******* AS 'commitMode' IS DISABLED, THIS IS A SIMULATION, NO CHANGES WILL BE COMMITTED. *******");
        } else {
            body.append("\n\n******* CHANGES HAVE BEEN COMMITTED! *******");
        }
        sendEmail(subject, body.toString());
    }

    public static void notifyFailedBuild(String sourceBranch, String targetBranch, int fromRevision,
        int toRevision, String buildOutput) {
        String subject = String
            .format("[AUTO-MERGER] Broken build (%s -> %s)", sourceBranch, targetBranch);
        String body = String.format(
            "<p>Build is broken after attempt merging branch <mark>%s</mark> into <mark>%s</mark> "
                + "(from revision <mark>%s</mark> to <mark>%s</mark>).\n" +
                "Changes have not been committed, manual investigation is required.</p>\n\n" +
                "*************************************** BUILD OUTPUT: ***************************************\n%s",
            sourceBranch, targetBranch, fromRevision, toRevision, buildOutput);
        sendEmail(subject, body);
    }

    public static void notifyNoEligibleVersions(String sourceBranch, String targetBranch) {
        String subject = String
            .format("[AUTO-MERGER] No merge required (%s -> %s)", sourceBranch, targetBranch);
        String body = String.format(
            "<p>There are no eligible revisions to merge from branch <mark>%s</mark> into <mark>%s</mark>.</p>",
            sourceBranch, targetBranch);
        sendEmail(subject, body);
    }

    public static void notifyCssCompilationFail(String sourceBranch, String targetBranch,
        int fromRevision, int toRevision, String cssCompilationOutput) {
        String subject = String
            .format("[AUTO-MERGER] CSS compilation failed (%s -> %s)", sourceBranch, targetBranch);
        String body = String.format(
            "<p>CSS compilation has failed after merge branch <mark>%s</mark> into <mark>%s</mark>"
                + " (from revision <mark>%s</mark> to <mark>%s</mark>).\n" +
                "Changes have not been committed, manual investigation is required.</p>\n\n" +
                "*************************************** Output of CSS compilation: *************************************** \n%s",
            sourceBranch, targetBranch, fromRevision, toRevision, cssCompilationOutput);
        sendEmail(subject, body);
    }

}
