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
 * Creates a directory in Pydio to copy the distribution file.
 * Use "-p" flag, so no error if existing and make parent directories as needed.
 * @author Leonardo Pavone - 13 Jun 2019.
 */
public class CreateRemoteDirectory extends Command {

    /*
     * The command will be something like:
     * ssh -l user -i ~/.ssh/id_rsa 10.10.10.200 'mkdir -p /var/pydio/.../VERSION_5_9_1_0'
     */
    private static final String CREATE_DIR_TPL = "ssh -l %s -i %s %s 'mkdir -p %s'";
    private String branchName;
    private String distFileRemotePath;
    private String pydioLogin;
    private String pydioKey;
    private String pydioHost;

    public CreateRemoteDirectory(final String branchName, final String distFileRemotePath,
        final String pydioLogin, final String pydioKey, final String pydioHost) {
        this.branchName = branchName;
        this.distFileRemotePath = distFileRemotePath;
        this.pydioLogin = pydioLogin;
        this.pydioKey = pydioKey;
        this.pydioHost = pydioHost;
    }

    @Override
    public String execute() {
        /*
        Result of this path will be something like:
        /var/pydio/.../VERSION_5_9_1_0
         */
        StringBuilder distFileRemoteFolder = new StringBuilder(distFileRemotePath)
            .append(distFileRemotePath.endsWith("/") ? "" : "/")
            .append(branchName);

        String createDirectoryCommand = String.format(CREATE_DIR_TPL,
            pydioLogin,
            pydioKey,
            pydioHost,
            distFileRemoteFolder);

        output = CommandExecutor.run(createDirectoryCommand, null);
        return output;
    }

    @Override
    public boolean wasSuccessful() {
        return StringUtils.isBlank(output);
    }
}
