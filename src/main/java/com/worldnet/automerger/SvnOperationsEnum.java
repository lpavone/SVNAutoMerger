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

  COMMIT("svn commit -F %s"),
  REVERT("svn revert . -R"),
  UPDATE("svn update --force"),
  CHECKOUT("svn co %s --force"),
  MERGE("svn merge --non-interactive -r %s:%s %s -x --ignore-eol-style"),
  MERGEINFO_ELIGIBLE("svn mergeinfo --show-revs eligible %s %s"),
  MERGEINFO_MERGED("svn mergeinfo --show-revs merged %s %s"),
  STATUS("svn status"),
  RESOLVE("svn resolve --accept theirs-full %s --non-interactive");

  private final String command;
  /**
   * Used to add username and password to any command if necessary.
   */
  public static final String SVN_CREDENTIALS = " --username %s --password %s";

  SvnOperationsEnum(String command) {
    this.command = command;
  }

  public final String command() {
    return this.command;
  }

}
