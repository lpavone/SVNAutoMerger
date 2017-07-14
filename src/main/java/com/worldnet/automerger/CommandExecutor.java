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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Executor of command line orders.
 *
 * @author Leonardo Pavone - 12/07/17.
 */
public class CommandExecutor {

  static final Logger logger = LogManager.getLogger();

  /**
   * Execute a command from specified path
   * @param command command to be executed
   * @param pathName path where the command should be executed
   * @return command's output
   */
  static String run(String command, String pathName) {
    StringBuilder output = new StringBuilder();
    try {
      logger.info("$ {}", command);
      Process p = Runtime.getRuntime().exec(command, null, new File(pathName));
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line;
      while ((line = stdInput.readLine()) != null) {
        output.append(line + "\n");
      }
      logger.info("$ {}", output.toString());

      return output.toString();

    } catch (Exception e) {
      logger.error("Error executing command: " + command, e);
    }
    return StringUtils.EMPTY;
  }

}
