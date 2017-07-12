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
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Leonardo Pavone - 12/07/17.
 */
public class PropertiesUtil {

  private static Properties properties;

  private static void load(){

    properties = new Properties();
    try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
      if(is != null){
        properties.load(is);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static String getString(String propertyName){
    if(properties == null){
      load();
    }
    return properties.getProperty(propertyName);
  }
}