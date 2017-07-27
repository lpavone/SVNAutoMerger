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

package com.worldnet.automerger.commands;

import com.worldnet.automerger.SvnOperationsEnum;
import com.worldnet.automerger.SvnUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Update a local branch with latest changes from repository.
 * 
 * @author Leonardo Pavone - 26/07/17.
 */
public class UpdateBranch extends Command {

  private String branchName;

  public UpdateBranch(String branchName) {
    this.branchName = branchName;
  }

  @Override
  public String execute() {
      StringBuilder command = new StringBuilder( SvnOperationsEnum.UPDATE.command())
          .append( SvnUtils.createSvnCredentials());

      output = CommandExecutor.run(command.toString(),
          SvnUtils.TEMP_FOLDER + "/" + branchName);
      return output;
  }

  @Override
  public boolean wasSuccessful() {
    return StringUtils.contains(output, SvnUtils.REVISION)
        && !StringUtils.contains(output, SvnUtils.SVN_ERROR_PREFIX);
  }
}
