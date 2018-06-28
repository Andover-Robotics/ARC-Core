package com.andoverrobotics.core.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;

public class MotorPair implements IMotor {

  private DcMotor first;
  private DcMotor second;

  private MotorPair(DcMotor first, DcMotor second) {
    this.first = first;
    this.second = second;
  }

  /**
   * Creates a MotorPair
   *
   * @param one The first motor in a pair
   * @param two The second motor in a pair
   * @return The new MotorPair
   */
  public static MotorPair of(DcMotor one, DcMotor two) {
    return new MotorPair(one, two);
  }

  /**
   * Sets power for both motors
   *
   * @param power The power to set the motors
   */
  @Override
  public void setPower(double power) {
    first.setPower(power);
    second.setPower(power);
  }

  /**
   * Moves a pair of motors a certain amount based on their current positions
   *
   * @param position The amount to modify the current positions
   */
  @Override
  public void addTargetPosition(int position) {
    first.setTargetPosition(first.getCurrentPosition() + position);
    second.setTargetPosition(second.getCurrentPosition() + position);
  }

  /**
   * Moves a certain amount based on ticks and the absolute value of the power
   *
   * @param tickOffset The amount to move based on ticks
   * @param absPower The power value to move with
   */
  @Override
  public void startRunToPosition(int tickOffset, double absPower) {
    if (tickOffset == 0 || absPower < 1e-5) {
      return;
    }

    setMode(RunMode.RUN_TO_POSITION);
    addTargetPosition(tickOffset);
    setPower(tickOffset > 0 ? Math.abs(absPower) : -Math.abs(absPower));
  }

  /**
   * Sets the RunMode of both of the motors
   *
   * @param mode The RunMode to run the motors with (RUN_WITHOUT_ENCODER, RUN_USING_ENCODER,
   *             RUN_TO_POSITION, STOP_AND_RESET_ENCODER)
   */
  @Override
  public void setMode(RunMode mode) {
    first.setMode(mode);
    second.setMode(mode);
  }

  /**
   * Checks to see if the motors are running
   *
   * @return True if any of the motors in the pair are running
   */
  @Override
  public boolean isBusy() {
    return first.isBusy() || second.isBusy();
  }
}
