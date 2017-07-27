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

/**
 * Revert changes in a working copy.
 *
 * @author Leonardo Pavone - 26/07/17.
 */
public class RevertChanges extends Command {

  private String branchName;

  public RevertChanges(String branchName) {
    this.branchName = branchName;
  }

  @Override
  public String execute() {
      StringBuilder command = new StringBuilder( SvnOperationsEnum.REVERT.command())
          .append( SvnUtils.createSvnCredentials());

      return CommandExecutor.run(command.toString(), SvnUtils.TEMP_FOLDER + "/" + branchName);
  }

}
