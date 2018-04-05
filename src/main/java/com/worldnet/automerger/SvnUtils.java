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

import org.apache.commons.lang3.BooleanUtils;

/**
 *
 * @author Leonardo Pavone - 24/07/17.
 */
public class SvnUtils {

  public static final String COMMITTED_REVISION = "Committed revision";
  public static final String SVN_ERROR_PREFIX = "svn: E";
  public static final String SVN_CONFLICTS = "conflicts";
  public static final String SVN_RECORDED_MERGEINFO = "Recording mergeinfo";
  public static final String REVISION = "revision";
  public static final String CHECKED_OUT = "Checked out revision";
  public static final String TEMP_FOLDER = PropertiesUtil.getString("temp.folder");
  public static final String BASE_REPO = PropertiesUtil.getString("base.repository.path");
  public static final String SVN_ERROR_MSG_BRANCH_NOT_FOUND = "E160013";


  /**
   * Creates the String to include SVN user and password in the command if necessary.
   * @return
   */
  public static String createSvnCredentials() {
    boolean isSvnUsingCredentials =
        BooleanUtils.toBoolean( PropertiesUtil.getString("svn.enable.password.auth"));
    String credentials;
    if (isSvnUsingCredentials){
      String user = PropertiesUtil.getString("svn.username");
      String password = PropertiesUtil.getString("svn.password");
      credentials = String.format(SvnOperationsEnum.SVN_CREDENTIALS, user, password);
    } else {
      credentials = "";
    }
    return credentials;
  }

}
