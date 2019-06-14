/* All materials herein: Copyright (c) 2019 Worldnet TPS Ltd. All Rights Reserved.
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
package com.worldnet.automerger.commands.distribution;

import com.worldnet.automerger.PropertiesUtil;
import com.worldnet.automerger.SvnUtils;
import com.worldnet.automerger.commands.Command;
import com.worldnet.automerger.commands.CommandExecutor;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Send the e-mail to trigger the distribution verification
 * @author Leonardo Pavone - 13 Jun 2019.
 */
public class VerifyDistribution extends Command {

    static final Logger logger = LogManager.getLogger();

    private static final String EMAIL_TMPL_CMD = "sendmail -t < %s";
    private static final String EMAIL_TMPL_CONTENT =
        "From: Dev Team <%s>\nTo: %s\nSubject: %s\nContent-Type: text/html\n\n%s";
    private static final String EMAIL_SUBJECT = "%s dist for verification";
    private static final String EMAIL_BODY =
          "from=%s\n"
        + "to=%s\n"
        + "md5sum=%s";

    private String sourceBranch;
    private String targetBranch;
    private String md5sum;

    public VerifyDistribution(final String sourceBranch, final String targetBranch,
        final String md5sum) {
        this.sourceBranch = sourceBranch;
        this.targetBranch = targetBranch;
        this.md5sum = md5sum;
    }

    @Override
    public String execute() {
        try {
            String sysadminsEmail = PropertiesUtil.getString("email.sysadmins");
            String toNotify = PropertiesUtil.getString("email.to.notify") +
                (StringUtils.isNotBlank(sysadminsEmail) ? "," + sysadminsEmail : "");
            //Convert from format VERSION_5_9_1_0 to 5.9.1.0
            String distVersionForSubject = targetBranch
                .replace("VERSION_","")
                .replace("_",".");
            //Convert from format VERSION_5_9_1_0 to 5_9_1_0
            String distVersionForBody = targetBranch.replace("VERSION_","");

            String msgContent = String.format(
                EMAIL_TMPL_CONTENT,
                PropertiesUtil.getString("email.sender"),
                toNotify,
                String.format(EMAIL_SUBJECT, distVersionForSubject),
                String.format(EMAIL_BODY, sourceBranch, distVersionForBody, md5sum)
            );

            String mailContentFilePath = PropertiesUtil.getString("temp.folder")
                + "/verificatione-email.txt";
            //write down mail content to FS to be picked by sendmail command
            Files.write(Paths.get(mailContentFilePath), msgContent.getBytes());

            String sendMailCommand = String.format(
                EMAIL_TMPL_CMD,
                mailContentFilePath
            );
            return CommandExecutor.run(sendMailCommand, SvnUtils.TEMP_FOLDER);

        } catch (Exception e) {
            logger.error("Email notification has failed", e);
        }
        return null;
    }

}
