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

import com.worldnet.automerger.commands.Command;
import com.worldnet.automerger.commands.CommandExecutor;

import org.apache.commons.lang3.StringUtils;

/**
 * Copy distribution file to Pydio server.
 * @author Leonardo Pavone - 13 Jun 2019.
 */
public class CopyDistributionFile extends Command {

    /*
     * The command will be something like:
     * scp -i ~/.ssh/id_rsa file.zip user@10.10.10.200:/home/server/leo
     */
    private static final String COPY_FILE_TPL = "scp -i %s %s %s@%s:%s";

    private String branchName;
    private String distFileLocalPath;
    private String distFileRemotePath;
    private String pydioLogin;
    private String pydioKey;
    private String pydioHost;
    private String tempFolder;

    public CopyDistributionFile(String branchName, String distFileLocalPath,
        String distFileRemotePath, String pydioLogin, String pydioKey, String pydioHost,
        String tempFolder) {
        this.branchName = branchName;
        this.distFileLocalPath = distFileLocalPath;
        this.distFileRemotePath = distFileRemotePath;
        this.pydioLogin = pydioLogin;
        this.pydioKey = pydioKey;
        this.pydioHost = pydioHost;
        this.tempFolder = tempFolder;
    }

    @Override
    public String execute() {
        /*
        Result of this path will be something like:
        /home/lpavone/branches/VERSION_5_9_1_0/build/distribution/merchant_dist.zip
         */
        StringBuilder distFileLocalFullPath = new StringBuilder(tempFolder)
            .append(tempFolder.endsWith("/") ? "" : "/")
            .append(branchName)
            .append("/")
            .append(distFileLocalPath);

        /*
        Result of this path will be something like:
        /var/pydio/.../VERSION_5_9_1_0
         */
        StringBuilder distFileRemoteFolder = new StringBuilder(distFileRemotePath)
            .append(distFileRemotePath.endsWith("/") ? "" : "/")
            .append(branchName);

        String copyFileCommand = String.format(COPY_FILE_TPL,
            pydioKey,
            distFileLocalFullPath,
            pydioLogin,
            pydioHost,
            distFileRemoteFolder);

        output = CommandExecutor.run(copyFileCommand, null);
        return output;
    }

    @Override
    public boolean wasSuccessful() {
        return StringUtils.isBlank(output);
    }
}
