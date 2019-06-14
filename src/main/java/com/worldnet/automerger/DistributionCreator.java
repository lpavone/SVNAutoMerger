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
package com.worldnet.automerger;

import com.worldnet.automerger.commands.Command;
import com.worldnet.automerger.commands.distribution.CopyDistributionFile;
import com.worldnet.automerger.commands.distribution.CreateDistributionFile;
import com.worldnet.automerger.commands.distribution.CreateRemoteDirectory;
import com.worldnet.automerger.commands.distribution.Md5SumCalculator;
import com.worldnet.automerger.commands.distribution.VerifyDistribution;
import com.worldnet.automerger.notification.Notifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class in charge of creating the distribution file and pushing it to Pydio server.
 * @author Leonardo Pavone - 13 Jun 2019.
 */
public class DistributionCreator {

    private static final Logger logger = LogManager.getLogger();
    private static final String DIST_FILE_LOCAL_PATH =
        PropertiesUtil.getString("distribution.file.local.path");
    protected static final String DIST_FILE_REMOTE_PATH =
        PropertiesUtil.getString("distribution.file.remote.path");
    protected static final String PYDIO_LOGIN = PropertiesUtil.getString("pydio.login");
    protected static final String PYDIO_KEY = PropertiesUtil.getString("pydio.key");
    protected static final String PYDIO_HOST = PropertiesUtil.getString("pydio.host");
    protected static final String TEMP_FOLDER =
        PropertiesUtil.getString("temp.folder");

    public void createDistribution(String sourceBranch, String targetBranch){
        logger.info("Starting distribution creation for branch: " + targetBranch);
        Command createDistCommand = new CreateDistributionFile(targetBranch);
        createDistCommand.execute();
        if (!createDistCommand.wasSuccessful()){
            logger.error("Distribution creation has failed, about to send notification to the team");
            Notifier.notifyDistributionFail(targetBranch);
            return;
        }
        Command createRemoteDirCommand = new CreateRemoteDirectory(
            targetBranch,
            DIST_FILE_REMOTE_PATH,
            PYDIO_LOGIN,
            PYDIO_KEY,
            PYDIO_HOST);
        createRemoteDirCommand.execute();

        Command copyDistFileCommand = new CopyDistributionFile(
            targetBranch,
            DIST_FILE_LOCAL_PATH,
            DIST_FILE_REMOTE_PATH,
            PYDIO_LOGIN,
            PYDIO_KEY,
            PYDIO_HOST,
            TEMP_FOLDER);
        copyDistFileCommand.execute();

        Command md5sumCalculatorCommand = new Md5SumCalculator(
            targetBranch,
            TEMP_FOLDER,
            DIST_FILE_LOCAL_PATH);
        md5sumCalculatorCommand.execute();

        if (!createRemoteDirCommand.wasSuccessful() ||
            !copyDistFileCommand.wasSuccessful() ||
            !md5sumCalculatorCommand.wasSuccessful()){
            logger.error("Error copying distribution file to remote server");
            Notifier.notifyDistributionCopyFail(targetBranch);
            return;
        }
        String md5sum = md5sumCalculatorCommand.getOutput();
        logger.info("Distribution created for branch: " + targetBranch);

        Command verifyDistribution = new VerifyDistribution(sourceBranch, targetBranch, md5sum);
        verifyDistribution.execute();
        logger.info("Distribution creation finished");
    }
}
