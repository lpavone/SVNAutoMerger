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

import org.apache.commons.lang3.StringUtils;

/**
 * Commit changes to repository.
 *
 * @author Leonardo Pavone - 26/07/17.
 */
public class Commit extends Command {

  private String branchName;
  private String commitMessageFilePath;

  public Commit(String branchName, String commitMessageFilePath) {
    this.branchName = branchName;
    this.commitMessageFilePath = commitMessageFilePath;
  }

  @Override
  public String execute() {
    StringBuilder command = new StringBuilder(
        String.format( SvnOperationsEnum.COMMIT.command(), commitMessageFilePath))
        .append( SvnUtils.createSvnCredentials());

    output = CommandExecutor.run(command.toString(),
        SvnUtils.TEMP_FOLDER + "/" + branchName);
    return output;
  }

  @Override
  public boolean wasSuccessful() {
    return StringUtils.contains(output, SvnUtils.COMMITTED_REVISION)
        && !StringUtils.contains(output, SvnUtils.SVN_ERROR_PREFIX);
  }
}