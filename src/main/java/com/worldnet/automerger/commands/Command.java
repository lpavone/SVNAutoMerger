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

package com.worldnet.automerger.commands;

/**
 *
 * @author Leonardo Pavone - 26/07/17.
 */
public abstract class Command {

  protected String output;

  /**
   * Execute a command.
   * @return command's output
   */
  public abstract String execute();

  /**
   * Returns if a command was successful or not. If the command to implement does not need to check
   * result there is no need to override this method, it returns true by default.
   * @return true if command was successfully executed, false otherwise
   */
  public boolean wasSuccessful(){
    return true;
  }

  /**
   * Returns output of the command execution.
   */
  public String getOutput(){
    return output;
  }

}
