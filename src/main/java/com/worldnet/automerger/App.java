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
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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

    /**
     * URL to a Team Drive document published in the web (publicly available) which define branches
     * to be merged.
     */
    public static final String BRANCHES_URL = PropertiesUtil.getString("branches.doc.url");

    public static void main( String[] args ) throws Exception {
        Merger merger = new Merger();
        String[] branches = readBranches().split(System.getProperty("line.separator"));

        //skip first line in the iteration
        for (int i = 1; i < branches.length; i++) {
            String[] mergeArgs = StringUtils.split(branches[i], ",");
            if(mergeArgs.length != 3){
                logger.error("Incorrect branches configuration: {}", branches[i]);
                logger.error("A valid entry must be: <SOURCE_BRANCH>,<TARGET_BRANCH>,<REDMINE_TICKET>");
                System.exit(0);
            }
            try {
                merger.performMerge(mergeArgs[0].trim(), mergeArgs[1].trim(), mergeArgs[2].trim());
            } catch (Exception e) {
                logger.error(e);
            }
        }
        System.exit(0);
    }

    /**
     * Read the branches configuration to execute the automerger.
     * The document location is "Team Drive > Development > Projects > Automerger > Branches"
     * @return the branches configuration
     */
    public static String readBranches() throws Exception{
        URL url = new URL(App.BRANCHES_URL);
        URLConnection uc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String inputLine;
        StringBuilder sb = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
            sb.append("\n");
        }
        in.close();
        return sb.toString();
    }

}
