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
 *
 * @author Leonardo Pavone - 26/07/17.
 */
public class MergeInfoRevisions extends Command{

  private String sourceBranch;
  private String targetBranch;
  private SvnOperationsEnum operation;

  public MergeInfoRevisions(String sourceBranch, String targetBranch,
      SvnOperationsEnum operation) {
    this.sourceBranch = sourceBranch;
    this.targetBranch = targetBranch;
    this.operation = operation;
  }

  @Override
  public String execute() {
    StringBuilder command = new StringBuilder(
        String.format( operation.command(),
            SvnUtils.BASE_REPO + sourceBranch,
            SvnUtils.BASE_REPO + targetBranch))
        .append( SvnUtils.createSvnCredentials());

    return CommandExecutor.run(command.toString(), SvnUtils.TEMP_FOLDER);
  }
}
