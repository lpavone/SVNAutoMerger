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

package com.worldnet.automerger.commands.distribution;

import com.worldnet.automerger.SvnUtils;
import com.worldnet.automerger.commands.Command;
import com.worldnet.automerger.commands.CommandExecutor;

import org.apache.commons.lang3.StringUtils;

/**
 * Run ANT task to create distribution file
 *
 * @author Leonardo Pavone - 13/06/19.
 */
public class CreateDistributionFile extends Command {

    private static final String REVERT_LOCAL_CHANGES = "svn revert . -R";
    private static final String PULL_LATEST_CHANGES = "svn update";
    private static final String CREATE_DIST_TASK = "ant cleanall distribute";
    private String branchName;

    public CreateDistributionFile(String branchName) {
      this.branchName = branchName;
    }

    @Override
    public String execute() {
        CommandExecutor.run(REVERT_LOCAL_CHANGES, SvnUtils.TEMP_FOLDER + "/" + branchName);
        CommandExecutor.run(PULL_LATEST_CHANGES, SvnUtils.TEMP_FOLDER + "/" + branchName);
        output = CommandExecutor.run(CREATE_DIST_TASK, SvnUtils.TEMP_FOLDER + "/" + branchName);
        return output;
    }

    @Override
    public boolean wasSuccessful() {
      return StringUtils.contains(output, "BUILD SUCCESSFUL") &&
          !StringUtils.contains(output, "BUILD FAILED");
    }
}