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

import com.worldnet.automerger.PropertiesUtil;
import com.worldnet.automerger.SvnOperationsEnum;
import com.worldnet.automerger.SvnUtils;
import com.worldnet.automerger.commands.Command;
import com.worldnet.automerger.commands.CommandExecutor;

/**
 * Resolve different kind of conflicts after merge.
 * Only applies to those conflicts we previously agree to resolve automatically.
 *
 * @author Leonardo Pavone - 24/07/17.
 */
public class ConflictSolver extends Command {

  private String branchName;

  public ConflictSolver(String branchName) {
    this.branchName = branchName;
  }

  /**
   * Resolve the precompiled CSS conflicts automatically.
   *
   * @return output of the svn resolve command to identify conflicts resolved
   */
  @Override
  public String execute() {
    StringBuilder command = new StringBuilder(
        String.format( SvnOperationsEnum.RESOLVE.command(),
            PropertiesUtil.getString("compiled.css.path")))
        .append( SvnUtils.createSvnCredentials());

    output = CommandExecutor.run(command.toString(),
        SvnUtils.TEMP_FOLDER + "/" + branchName);
    return output;
  }

}
