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

import com.worldnet.automerger.SvnUtils;
import com.worldnet.automerger.commands.Command;
import com.worldnet.automerger.commands.CommandExecutor;

import org.apache.commons.lang3.StringUtils;

/**
 * Run compilation task to verify build status.
 *
 * @author Leonardo Pavone - 26/07/17.
 */
public class BuildCheck extends Command {

  private static final String COMPILE_TASK = "ant compile";
  private String branchName;

  public BuildCheck(String branchName) {
    this.branchName = branchName;
  }

  @Override
  public String execute() {
    output = CommandExecutor.run(COMPILE_TASK,
        SvnUtils.TEMP_FOLDER + "/" + branchName);
    return output;
  }

  @Override
  public boolean wasSuccessful() {
    return StringUtils.contains(output,"BUILD SUCCESSFUL") &&
        !StringUtils.contains(output,"BUILD FAILED");
  }
}