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

package com.worldnet.automerger;

import org.apache.commons.lang3.StringUtils;

/**
 * Interface to implement different kind of conflicts to resolve after merge.
 * Only applies to those conflicts we agreed to resolve automatically.
 *
 * @author Leonardo Pavone - 24/07/17.
 */
public class ConflictSolver {

  /**
   * Resolve the precompiled CSS conflicts automatically.
   * @param branch target branch where the conflicts are attempted to be resolved
   * @return output of the svn resolve command to identify conflicts resolved
   */
  public static String resolveCssConflicts(String branch){

    StringBuilder command = new StringBuilder(
        String.format( SvnOperationsEnum.RESOLVE.command(),
            PropertiesUtil.getString("compiled.css.path")))
        .append( SvnUtils.createSvnCredentials());

    return CommandExecutor.run(command.toString(), SvnUtils.TEMP_FOLDER + "/" + branch);
  }

  /**
   * Check if there are existing conflicts in the passed branch.
   * @param branch where the conflicts are checked
   * @return true is there are some conflicts, false otherwise.
   */
  public static boolean areConflictsResolved(String branch) {
    String output = CommandExecutor.run(
        SvnOperationsEnum.STATUS.command(),
        SvnUtils.TEMP_FOLDER + "/" + branch);
    return !StringUtils.contains(output, SvnUtils.SVN_ERROR_PREFIX)
        && !StringUtils.contains(output, SvnUtils.SVN_CONFLICTS);
  }
}
