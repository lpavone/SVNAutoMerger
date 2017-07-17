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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Leonardo Pavone - 17/07/17.
 */
public class Utils {

  static final Logger logger = LogManager.getLogger();

  public static void removeTempFile(String filePath){
    try {
      Files.delete(Paths.get(filePath));
    } catch (IOException e) {
      logger.error("Error deleting temp file %s", filePath);
    }
  }
}
