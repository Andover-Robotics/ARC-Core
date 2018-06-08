package com.andoverrobotics.core.utilities;

import com.qualcomm.robotcore.hardware.DcMotor.RunMode;

public interface IMotor {
  void setPower(double power);

  void addTargetPosition(int tickOffset);

  void setMode(RunMode mode);

  boolean isBusy();
}
