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

/**
 * @author Leonardo Pavone - 12/07/17.
 */
public enum SvnOperationsEnum {

  COMMIT("svn commit -F %s --username %s --password %s"),
  REVERT("svn revert . -R --username %s --password %s"),
  UPDATE("svn update --force --username %s --password %s"),
  CHECKOUT("svn co %s --force --username %s --password %s"),
  MERGE("svn merge --non-interactive -r %s:%s %s -x --ignore-eol-style --username %s --password %s"),
  MERGEINFO_ELIGIBLE("svn mergeinfo --show-revs eligible %s %s --username %s --password %s"),
  MERGEINFO_MERGED("svn mergeinfo --show-revs merged %s %s --username %s --password %s");

  private final String command;

  SvnOperationsEnum(String command) {
    this.command = command;
  }

  public final String command() {
    return this.command;
  }

}
