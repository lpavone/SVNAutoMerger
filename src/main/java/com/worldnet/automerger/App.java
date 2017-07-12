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


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AutoMerger App.
 *
 */
public class App 
{
    static final Logger logger = LogManager.getLogger();

    public static void main( String[] args ){

        if (args.length >= 2) {
            Merger merger = new Merger();
            for (int i = 0; i < args.length - 1; i++) {
                try {
                    merger.performMerge(args[i], args[i+1]);
                } catch (Exception e) {
                    logger.error(e);
                }
            }
            System.exit(0);
        } else {
            logger.error("No arguments received.");
            System.exit(1);
        }
    }
}
