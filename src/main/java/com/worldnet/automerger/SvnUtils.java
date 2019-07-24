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

import com.worldnet.automerger.commands.CommandExecutor;
import com.worldnet.automerger.commands.merge.CheckoutBranch;
import com.worldnet.automerger.commands.merge.RevertChanges;
import com.worldnet.automerger.commands.merge.UpdateBranch;

import org.apache.commons.lang3.BooleanUtils;

import java.io.File;

/**
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
     */
    public static String createSvnCredentials() {
        boolean isSvnUsingCredentials =
            BooleanUtils.toBoolean(PropertiesUtil.getString("svn.enable.password.auth"));
        String credentials;
        if (isSvnUsingCredentials) {
            String user = PropertiesUtil.getString("svn.username");
            String password = PropertiesUtil.getString("svn.password");
            credentials = String.format(SvnOperationsEnum.SVN_CREDENTIALS, user, password);
        } else {
            credentials = "";
        }
        return credentials;
    }


    /**
     * Checkout the working copy of target branch. If already exits will do: - Revert: to remove any
     * possible unwanted changes - Update: to update latest changes from repository
     */
    public static void checkoutOrUpdateBranch(String branchName) throws Exception {
        boolean branchDirExists = new File(SvnUtils.TEMP_FOLDER + "/" + branchName).exists();
        if (branchDirExists) {
            RevertChanges revertChangesCmd = new RevertChanges(branchName);
            revertChangesCmd.execute();
            UpdateBranch updateBranchCmd = new UpdateBranch(branchName);
            updateBranchCmd.execute();
            if (!updateBranchCmd.wasSuccessful()) {
                throw new Exception("Error updating working copy");
            }
        } else {
            CheckoutBranch checkoutBranchCmd = new CheckoutBranch(branchName);
            checkoutBranchCmd.execute();
            if (!checkoutBranchCmd.wasSuccessful()) {
                throw new Exception("Error checking out working copy");
            }
            createLocalConfigFile(branchName);
        }
    }

    /**
     * Create localconf folder and properties file necessary to run build task in order to check
     * merge integrity.
     */
    private static void createLocalConfigFile(String branchName) throws Exception {
        String branchPath = SvnUtils.TEMP_FOLDER + "/" + branchName;
        //run script to set up project
        String scriptSetupPath = PropertiesUtil.getString("script.setup.path");
        String scriptCommand = String.format("%s %s", scriptSetupPath, branchName);
        CommandExecutor.run(scriptCommand, branchPath);
    }
}
