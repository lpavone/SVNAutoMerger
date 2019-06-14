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

package com.worldnet.automerger.commands.merge;

import com.worldnet.automerger.SvnOperationsEnum;
import com.worldnet.automerger.SvnUtils;
import com.worldnet.automerger.commands.Command;
import com.worldnet.automerger.commands.CommandExecutor;

/**
 * Retrieve SVN log of the last revision. It will be executed after merge is committed then will
 * have the information about merged files.
 *
 * @author Leonardo Pavone - 26/07/17.
 */
public class LastRevisionLog extends Command {

    private String branchName;

    public LastRevisionLog(String branchName) {
        this.branchName = branchName;
    }

    @Override
    public String execute() {
        output = CommandExecutor.run(
            SvnOperationsEnum.LAST_REVISION_LOG.command(),
            SvnUtils.TEMP_FOLDER + "/" + branchName);
        return output;
    }
}
