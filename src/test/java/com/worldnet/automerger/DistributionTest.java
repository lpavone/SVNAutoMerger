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
import com.worldnet.automerger.commands.CommandExecutor;
import com.worldnet.automerger.commands.distribution.CopyDistributionFile;
import com.worldnet.automerger.commands.distribution.CreateRemoteDirectory;
import com.worldnet.automerger.commands.distribution.Md5SumCalculator;
import com.worldnet.automerger.commands.distribution.VerifyDistribution;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leonardo Pavone - 14 Jun 2019.
 */
public class DistributionTest {

    private static final String BRANCH_NAME = "VERSION_TEST_9_9_9_9";
    private static final String DIST_FILE_LOCAL_PATH = "distFile.zip";
    protected static final String DIST_FILE_REMOTE_PATH =
        PropertiesUtil.getString("distribution.file.remote.path");
    protected static final String PYDIO_LOGIN = PropertiesUtil.getString("pydio.login");
    protected static final String PYDIO_KEY = PropertiesUtil.getString("pydio.key");
    protected static final String PYDIO_HOST = PropertiesUtil.getString("pydio.host");
    protected static final String TEMP_FOLDER =
        PropertiesUtil.getString("temp.folder");

    @Test
    public void createDirectoryInRemoteServer(){
        Command createDirectory = new CreateRemoteDirectory(
            BRANCH_NAME,
            DIST_FILE_REMOTE_PATH,
            PYDIO_LOGIN,
            PYDIO_KEY,
            PYDIO_HOST);
        createDirectory.execute();
        //remove remote directory created
        String remoteFolder = DIST_FILE_REMOTE_PATH +
            (DIST_FILE_REMOTE_PATH.endsWith("/") ? "" : "/") +
            BRANCH_NAME;
        String commandToRemoveDir = "ssh -l " + PYDIO_LOGIN + " -i " + PYDIO_KEY + " " +
            PYDIO_HOST + " 'rm -Rf " + remoteFolder + "'";
        CommandExecutor.run(commandToRemoveDir, null);

        Assert.assertTrue(createDirectory.wasSuccessful());
    }

    @Test
    public void copyDistributionFileToRemoteServer(){
        //to avoid creating a distribution file that takes time just testing with a random file
        CommandExecutor.run("touch " + DIST_FILE_LOCAL_PATH, SvnUtils.TEMP_FOLDER);
        Command copyFileCmd = new CopyDistributionFile(
            "",
            DIST_FILE_LOCAL_PATH,
            DIST_FILE_REMOTE_PATH,
            PYDIO_LOGIN,
            PYDIO_KEY,
            PYDIO_HOST,
            TEMP_FOLDER);
        copyFileCmd.execute();
        //remove file copied
        String remoteFilePath = DIST_FILE_REMOTE_PATH +
            (DIST_FILE_REMOTE_PATH.endsWith("/") ? "" : "/") +
            DIST_FILE_LOCAL_PATH;
        String commandToRemoveFile = "ssh -l " + PYDIO_LOGIN + " -i " + PYDIO_KEY + " " +
            PYDIO_HOST + " 'rm " + remoteFilePath + "'";
        CommandExecutor.run(commandToRemoveFile, null);

        Assert.assertTrue(copyFileCmd.wasSuccessful());
    }

    @Test
    public void calculateMd5SumOfDistributionFile(){
        //to avoid creating a distribution file that takes time just testing with a random file
        CommandExecutor.run("touch " + DIST_FILE_LOCAL_PATH, SvnUtils.TEMP_FOLDER);
        Command md5SumCalculator = new Md5SumCalculator(
            "",
            TEMP_FOLDER,
            DIST_FILE_LOCAL_PATH
        );
        md5SumCalculator.execute();
        //remove file created
        String commandToRemoveFile = "rm " + TEMP_FOLDER + "/" + DIST_FILE_LOCAL_PATH;
        CommandExecutor.run(commandToRemoveFile, null);

        Assert.assertTrue(md5SumCalculator.wasSuccessful());
    }

    @Test
    public void sendVerificationEmail(){
        Command verifyDistributionCmd = new VerifyDistribution(
            "VERSION_5_6_0_0",
            "VERSION_5_6_0_0",
            "fsiajdfiasjdfisji4j2i3j4i2j34i"
        );
    }

}
