package com.andoverrobotics.core.examples;

import com.andoverrobotics.core.drivetrain.MecanumDrive;
import com.andoverrobotics.core.utilities.Coordinate;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

@TeleOp(name = "Mecanum TeleOp Example", group = "ARC")
public class MecanumTeleOpExample extends OpMode {

  private static final int TICKS_PER_INCH = (int) (1120 / (4 * Math.PI)), TICKS_PER_360 = 4000;

  private MecanumDrive mecanumDrive;

  @Override
  public void init() {
    DcMotor motorFL = hardwareMap.dcMotor.get("motorFL");
    DcMotor motorFR = hardwareMap.dcMotor.get("motorFR");
    DcMotor motorBL = hardwareMap.dcMotor.get("motorBL");
    DcMotor motorBR = hardwareMap.dcMotor.get("motorBR");

    motorFL.setDirection(Direction.REVERSE);
    motorBL.setDirection(Direction.REVERSE);

    mecanumDrive = MecanumDrive.fromOctagonalMotors(
        motorFL, motorFR, motorBL, motorBR, this, TICKS_PER_INCH, TICKS_PER_360);
  }

  @Override
  public void loop() {

    mecanumDrive.setStrafeRotation(Coordinate.fromXY(gamepad1.left_stick_x, -gamepad1.left_stick_y),
        Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y), gamepad1.right_stick_x);

    telemetry.addData("Left stick X", gamepad1.left_stick_x);
    telemetry.addData("Left stick Y", -gamepad1.left_stick_y);
    telemetry.addData("Right stick X", gamepad1.right_stick_x);
    telemetry.update();
  }
}
