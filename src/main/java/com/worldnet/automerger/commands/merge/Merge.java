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
 * Merge two branches based on specified range of revisions.
 *
 * - fromRevision value is decreased by 1 to meet the requirements of subversion merge command using
 * revisions range (-r [--revision] ARG ).
 * i.e.: if eligible revisions are
 * r4709
 * r4711
 * r4712
 * then the range argument must be "-r4708:4712".
 *
 * @author Leonardo Pavone - 26/07/17.
 */
public class Merge extends Command {

  private String sourceBranch;
  private String targetBranch;
  private int fromRevision;
  private int toRevision;

  public Merge(String sourceBranch, String targetBranch, int fromRevision, int toRevision) {
    this.sourceBranch = sourceBranch;
    this.targetBranch = targetBranch;
    this.fromRevision = fromRevision;
    this.toRevision = toRevision;
  }

  @Override
  public String execute() {
    StringBuilder command = new StringBuilder(
        String.format(SvnOperationsEnum.MERGE.command(),
            fromRevision - 1,
            toRevision,
            SvnUtils.BASE_REPO + sourceBranch))
        .append(SvnUtils.createSvnCredentials());

    output = CommandExecutor.run(command.toString(),
        SvnUtils.TEMP_FOLDER + "/" + targetBranch);
    return output;
  }

  @Override
  public boolean wasSuccessful() {
    return StringUtils.contains(output, SvnUtils.SVN_RECORDED_MERGEINFO)
        && !StringUtils.contains(output, SvnUtils.SVN_ERROR_PREFIX)
        && !StringUtils.contains(output, SvnUtils.SVN_CONFLICTS);
  }
}
