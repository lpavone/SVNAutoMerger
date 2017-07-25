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
 *
 * @author Leonardo Pavone - 25/07/17.
 */
public class CssCompiler {

  private static final String CSS_COMPILER_CMD = "ant styles.compile";
  /**
   * Recompile the CSS files.
   * @param branch where the CSS files are compiled
   */
  public static String recompile(String branch) {

    return CommandExecutor.run(
        CSS_COMPILER_CMD,
        SvnUtils.TEMP_FOLDER + "/" + branch);
  }

  public static boolean hasCssCompileFailed(String output) {

    return StringUtils.contains(output,"Compilation failed")
        || !StringUtils.contains(output,"BUILD SUCCESSFUL");

  }
}
