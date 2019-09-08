package com.andoverrobotics.core.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;

/**
 * Adapts a DcMotor to the IMotor interface for the DriveTrains.
 */
public class MotorAdapter extends CachedMotor implements IMotor {

  /**
   * Creates a new MotorAdapter for the given motor.
   *
   * @param motor The motor to send commands to
   */
  public MotorAdapter(DcMotor motor) {
    super(motor);
  }
  
  @Override
  public void addTargetPosition(int tickOffset) {
    setTargetPosition(motor.getCurrentPosition() + tickOffset);
  }

  @Override
  public void startRunToPosition(int tickOffset, double power) {
    double absPower = Math.abs(power);
    if (tickOffset == 0 || absPower < 1e-5) {
      return;
    }

    setMode(RunMode.RUN_TO_POSITION);
    addTargetPosition(tickOffset);
    setPower(absPower);
  }

  @Override
  public boolean isBusy() {
    return motor.isBusy();
  }
}