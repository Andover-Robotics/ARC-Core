package com.andoverrobotics.core.utilities;

import com.qualcomm.robotcore.hardware.DcMotor.RunMode;

public interface IMotor {

  /**
   * Changes the power of the motor(s)
   * @param power The new power
   */
  void setPower(double power);

  /**
   * Adds the given offset to the current position, and sets that sum as the target position
   * @param tickOffset The offset that is added to the current position
   */
  void addTargetPosition(int tickOffset);

  /**
   * Starts moving to the specified target position with the given power
   * @param tickOffset The direction to move in, and the new target position
   * @param absPower   The absolute value of the power to assign to the motors
   */
  void startRunToPosition(int tickOffset, double absPower);

  /**
   * Sets the RunMode of the motor(s)
   * @param mode The new RunMode
   */
  void setMode(RunMode mode);

  /**
   * Tells if any motor is busy
   * @return True if any motor is busy
   */
  boolean isBusy();
}
