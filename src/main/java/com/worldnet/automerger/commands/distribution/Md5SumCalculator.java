/* All materials herein: Copyright (c) 2019 Worldnet TPS Ltd. All Rights Reserved.
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
package com.worldnet.automerger.commands.distribution;

import com.worldnet.automerger.commands.Command;
import com.worldnet.automerger.commands.CommandExecutor;

import org.apache.commons.lang3.StringUtils;

/**
 * Calculate MD5Sum of the distribution file
 * @author Leonardo Pavone - 13 Jun 2019.
 */
public class Md5SumCalculator extends Command {

    private static final String MD5SUM_COMMAND = "md5sum %s";
    private String branchName;
    private String tempFolder;
    private String distFileLocalPath;

    public Md5SumCalculator(final String branchName, final String tempFolder, final String distFileLocalPath) {
        this.branchName = branchName;
        this.tempFolder = tempFolder;
        this.distFileLocalPath = distFileLocalPath;
    }

    @Override
    public String execute() {
        /*
        Result of this path will be something like:
        /home/lpavone/branches/VERSION_5_9_1_0/build/distribution/merchant_dist.zip
         */
        StringBuilder distFileLocalFullPath = new StringBuilder(tempFolder)
            .append(tempFolder.endsWith("/") ? "" : "/")
            .append(branchName)
            .append("/")
            .append(distFileLocalPath);

        String md5sumCalculatorCommand = String.format(MD5SUM_COMMAND, distFileLocalFullPath);
        output = CommandExecutor.run(md5sumCalculatorCommand, null);
        return output;
    }

    @Override
    public boolean wasSuccessful() {
        return StringUtils.isNotBlank(output) &&
                !StringUtils.contains(output,"No such file or directory");
    }

    /**
     * Returns only the md5sum of the file from the command output
     */
    @Override
    public String getOutput(){
        String md5sum = null;
        if (StringUtils.isNotBlank(output)){
            md5sum = StringUtils.substringBefore(output," ");
        }
        return md5sum;
    }
}
