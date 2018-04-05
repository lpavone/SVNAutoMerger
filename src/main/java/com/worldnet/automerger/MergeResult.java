/* All materials herein: Copyright (c) 2018 Worldnet TPS Ltd. All Rights Reserved.
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

/**
 * @author Leonardo Pavone - 05 Apr 2018.
 */
public enum MergeResult {

    MERGED_OK,
    NO_ELIGIBLE_REVISIONS,
    BRANCH_NOT_FOUND,
    CONFLICTS,
    CSS_COMPILATION_FAILED,
    MERGED_SIMULATION_OK,
    COMMIT_FAILED,
    BUILD_FAILED
}
