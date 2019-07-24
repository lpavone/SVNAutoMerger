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

import com.worldnet.automerger.commands.merge.BuildCheck;
import com.worldnet.automerger.commands.merge.Commit;
import com.worldnet.automerger.commands.merge.ConflictSolver;
import com.worldnet.automerger.commands.merge.CssCompilation;
import com.worldnet.automerger.commands.merge.LastRevisionLog;
import com.worldnet.automerger.commands.merge.Merge;
import com.worldnet.automerger.commands.merge.MergeInfoRevisions;
import com.worldnet.automerger.commands.merge.StatusCheck;
import com.worldnet.automerger.notification.Notifier;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Leonardo Pavone - 11/07/17.
 */
public class Merger {

    static final Logger logger = LogManager.getLogger();

    public MergeResult performMerge(String sourceBranch, String targetBranch,
        String redmineTaskNumber) throws Exception {
        logger.info("Attempting automatic merge of changes from {} to {}", sourceBranch,
            targetBranch);

        String eligibleRevisions =
            new MergeInfoRevisions(sourceBranch, targetBranch, SvnOperationsEnum.MERGEINFO_ELIGIBLE)
                .execute();
        if (StringUtils.isBlank(eligibleRevisions)) {
            logger.info("No eligible revisions from {} to {}, merge aborted.", sourceBranch,
                targetBranch);
            Notifier.notifyNoEligibleVersions(sourceBranch, targetBranch);
            return MergeResult.NO_ELIGIBLE_REVISIONS;

        } else if (StringUtils.contains(eligibleRevisions, SvnUtils.SVN_ERROR_PREFIX) &&
            StringUtils.contains(eligibleRevisions, SvnUtils.SVN_ERROR_MSG_BRANCH_NOT_FOUND)) {
            logger.error("Branch not found.");
            return MergeResult.BRANCH_NOT_FOUND;
        }

        SvnUtils.checkoutOrUpdateBranch(sourceBranch);
        SvnUtils.checkoutOrUpdateBranch(targetBranch);

        int fromRevision = getFromRevision(eligibleRevisions);
        int toRevision = getToRevision(eligibleRevisions);

        Merge mergeCmd = new Merge(sourceBranch, targetBranch, fromRevision, toRevision);
        mergeCmd.execute();
        String resolveConflictOutput = StringUtils.EMPTY;
        if (!mergeCmd.wasSuccessful()) {
            //resolve automatically CSS conflicts to proceed
            logger.info(
                "Conflicts have been found during merge. Will attempt to resolve CSS conflicts and re-check status.");
            resolveConflictOutput = new ConflictSolver(targetBranch)
                .execute();
            StatusCheck statusCheckCmd = new StatusCheck(targetBranch);
            String statusOutput = statusCheckCmd.execute();
            //check if after resolution there are still conflicts
            if (!statusCheckCmd.wasSuccessful()) {
                logger
                    .info("Conflicts have been found during merge, no changes will be committed.");
                Notifier
                    .notifyMergeWithConflicts(sourceBranch, targetBranch, fromRevision, toRevision,
                        statusOutput);
                return MergeResult.CONFLICTS;
            } else {
                logger
                    .info("CSS conflicts have been resolved, will attempt to recompile CSS files");
                //run and check result of CSS compilation
                CssCompilation cssCompilationCmd = new CssCompilation(targetBranch);
                String cssCompilationOutput = cssCompilationCmd.execute();
                if (!cssCompilationCmd.wasSuccessful()) {
                    logger.info("CSS compilation has failed, merge aborted.");
                    Notifier.notifyCssCompilationFail(sourceBranch, targetBranch, fromRevision,
                        toRevision,
                        cssCompilationOutput);
                    return MergeResult.CSS_COMPILATION_FAILED;
                }
            }
        }
        //check if build is ok after merge
        BuildCheck buildCheckCmd = new BuildCheck(targetBranch);
        String buildOutput = buildCheckCmd.execute();
        if (!buildCheckCmd.wasSuccessful()) {
            logger.info("Build has failed after merge. Changes will not be committed.");
            Notifier.notifyFailedBuild(sourceBranch, targetBranch, fromRevision, toRevision,
                buildOutput);
            return MergeResult.BUILD_FAILED;
        }
        //commit is only done if mode is enabled from config
        boolean isCommitModeEnabled = BooleanUtils.toBoolean(
            PropertiesUtil.getString("enable.commit.mode"));
        if (isCommitModeEnabled) {
            logger.info("Successful merge, changes will be committed in Redmine task #{}",
                redmineTaskNumber);
            String commitMessageFilePath = PropertiesUtil.getString("tmp.commit.message.file");
            createCommitMessageFile(commitMessageFilePath, sourceBranch, targetBranch,
                fromRevision, toRevision, redmineTaskNumber);
            //commit changes
            Commit commitCmd = new Commit(targetBranch, commitMessageFilePath);
            String commitOutput = commitCmd.execute();
            MergeResult result;
            if (commitCmd.wasSuccessful()) {
                String mergedRevisions =
                    new MergeInfoRevisions(sourceBranch, targetBranch,
                        SvnOperationsEnum.MERGEINFO_MERGED)
                        .execute();
                String lastRevisionLog = new LastRevisionLog(targetBranch).execute();

                logger.info("Changes have been successfully committed.");
                logger.info("Last revision:\n%s\n", lastRevisionLog);
                logger.info("Merged revisions:\n%s", mergedRevisions);

                Notifier.notifySuccessfulMerge(sourceBranch, targetBranch, fromRevision, toRevision,
                    mergedRevisions, resolveConflictOutput, false, lastRevisionLog);
                result = MergeResult.MERGED_OK;

            } else {
                logger.info("Commit failed! No changes have been committed into repository");
                Notifier.notifyCommitFailure(sourceBranch, targetBranch, fromRevision, toRevision,
                    commitOutput);
                result = MergeResult.COMMIT_FAILED;

            }
            Utils.removeTempFile(commitMessageFilePath);
            logger.info("Finished automatic merge of changes from {} to {}", sourceBranch,
                targetBranch);
            return result;

        } else {
            logger.info("Commit mode is disabled, no commit will be done.");
            Notifier.notifySuccessfulMerge(sourceBranch, targetBranch, fromRevision, toRevision,
                "[commit disabled]", resolveConflictOutput, true, null);
            return MergeResult.MERGED_SIMULATION_OK;
        }
    }

    /**
     * Creates a temp file with commit message content.
     */
    public void createCommitMessageFile(String commitMessageFilePath, String sourceBranch,
        String targetBranch, int fromRevision, int toRevision, String redmineTicketNumber)
        throws IOException {

        String msgContent = String.format(PropertiesUtil.getString("commit.message.template"),
            redmineTicketNumber,
            sourceBranch,
            targetBranch,
            fromRevision,
            toRevision,
            redmineTicketNumber);

        Files.write(Paths.get(commitMessageFilePath), msgContent.getBytes());
    }

    /**
     * Return last revision to merge
     */
    public int getToRevision(String eligibleRevisions) {
        String[] revisions = StringUtils
            .split(eligibleRevisions, System.getProperty("line.separator"));
        return Integer.parseInt(StringUtils.remove(revisions[revisions.length - 1], "r"));
    }

    /**
     * Return initial revision to merge
     */
    public int getFromRevision(String eligibleRevisions) {
        String revision = StringUtils
            .split(eligibleRevisions, System.getProperty("line.separator"))[0];
        return Integer.parseInt(StringUtils.remove(revision, "r"));
    }

}
