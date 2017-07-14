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

        String[] branches = StringUtils.split(
            PropertiesUtil.getString("branches.map"), ";");
        Merger merger = new Merger();
        for (int i = 0; i < branches.length; i++) {
            String[] mergeArgs = StringUtils.split(branches[i], ",");
            if(mergeArgs.length != 3){
                logger.error("Incorrect branches configuration: {}", branches[i]);
                logger.error("A valid entry must be: sourceBranch,targetBranch,RedmineTaskNumber");
                System.exit(0);
            }
            try {
                merger.performMerge( mergeArgs[0], mergeArgs[1], mergeArgs[2]);
            } catch (Exception e) {
                logger.error(e);
            }
        }
        System.exit(0);
    }
}
