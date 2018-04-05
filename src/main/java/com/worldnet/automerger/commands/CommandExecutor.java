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

import java.io.File;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;

/**
 * Executor of command line orders.
 *
 * @author Leonardo Pavone - 12/07/17.
 */
public class CommandExecutor {

  static final Logger logger = LogManager.getLogger();
  private static final String CMD_LOG_TMPL = ":{}$ {}";
  private static final int COMMAND_TIMEOUT_MINS = 10;

  private CommandExecutor(){}

  /**
   * Execute a command from specified path
   * @param command command to be executed
   * @param pathName path where the command should be executed
   * @return command's output
   */
  public static String run(String command, String pathName) {

    ExecutorService newFixedThreadPool = null;

    try {
      logger.info(CMD_LOG_TMPL, Optional.ofNullable(pathName).orElse(""), command);
      final Process process;
      String[] cmd = { "/bin/sh", "-c", command};
      process = Runtime.getRuntime().exec(
          cmd,
          null,
          StringUtils.isNotBlank(pathName) ? new File(pathName) : null);

      newFixedThreadPool = Executors.newFixedThreadPool(1);
      Future<String> output = newFixedThreadPool.submit(() ->
        IOUtils.toString( new InputStreamReader(process.getInputStream()))
      );
      Future<String> error = newFixedThreadPool.submit(() ->
        IOUtils.toString( new InputStreamReader(process.getErrorStream()))
      );

      if (!process.waitFor(COMMAND_TIMEOUT_MINS, TimeUnit.MINUTES)) {
        logger.info(
            String.format("Destroy process, it's been hanged out for more than %s minutes!",
            COMMAND_TIMEOUT_MINS));
        process.destroy();
      }
      logger.info(CMD_LOG_TMPL, Optional.ofNullable(pathName).orElse(""), output.get());
      if (StringUtils.isNotBlank(error.get())){
        logger.error(CMD_LOG_TMPL,  Optional.ofNullable(pathName).orElse(""), error.get());
      }
      return StringUtils.isNotBlank(output.get()) ? output.get() : error.get();

    } catch (Exception e) {
      logger.error("Error executing command: " + command, e);
    } finally {
      Optional.ofNullable(newFixedThreadPool).ifPresent(ExecutorService::shutdown);
    }
    return StringUtils.EMPTY;
  }
}
