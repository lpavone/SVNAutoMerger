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

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the google Drive API library
 * @author Leonardo Pavone - 15 Nov 2018.
 */
public class DriveTest {

    @Test
    public void readBranches() throws Exception{
        String branches = App.readBranches();
        String[] lines = branches.split(System.getProperty("line.separator"));
        for (String line : lines){
            System.out.println(line);
        }
        Assert.assertNotNull(branches);
    }

}
