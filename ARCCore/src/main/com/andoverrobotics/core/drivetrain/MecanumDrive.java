package com.andoverrobotics.core.drivetrain;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_TO_POSITION;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_WITHOUT_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER;

import com.andoverrobotics.core.utilities.Converter;
import com.andoverrobotics.core.utilities.Coordinate;
import com.andoverrobotics.core.utilities.IMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.Range;

public class MecanumDrive extends StrafingDriveTrain {

  private final IMotor leftDiagonal;
  private final IMotor rightDiagonal;
  private final IMotor leftSide;
  private final IMotor rightSide;

  private final int ticksPerInch;
  private final int ticksPer360;

  public MecanumDrive(IMotor leftDiagonal, IMotor rightDiagonal, IMotor leftSide, IMotor rightSide,
      OpMode opMode, int ticksPerInch, int ticksPer360) {

    super(opMode);

    this.leftDiagonal = leftDiagonal;
    this.rightDiagonal = rightDiagonal;
    this.leftSide = leftSide;
    this.rightSide = rightSide;

    this.ticksPerInch = ticksPerInch;
    this.ticksPer360 = ticksPer360;
  }

  @Override
  public void driveForwards(double distanceInInches, double power) {
    driveWithEncoder(
        Coordinate.fromXY(0, Math.abs(distanceInInches)), power);
  }

  private void driveWithEncoder(Coordinate displacement, double power) {
    double absPower = Math.abs(Range.clip(power, -1, 1));

    if (displacement.getPolarDistance() < 1e-5) {
      return;
    }

    setMotorMode(STOP_AND_RESET_ENCODER);
    setMotorMode(RUN_TO_POSITION);

    Coordinate diagonalOffsets = displacement.rotate(-45);

    leftDiagonal.addTargetPosition((int) (diagonalOffsets.getY() * ticksPerInch));
    rightDiagonal.addTargetPosition((int) (diagonalOffsets.getX() * ticksPerInch));

    double maxOffset = Math.max(diagonalOffsets.getX(), diagonalOffsets.getY());

    leftDiagonal.setPower(absPower * diagonalOffsets.getY() / maxOffset);
    rightDiagonal.setPower(absPower * diagonalOffsets.getX() / maxOffset);

    while (isBusy() && opModeIsActive()) {
    }

    stop();
    setMotorMode(RUN_USING_ENCODER);
  }

  @Override
  public void driveBackwards(double distanceInInches, double power) {
    driveWithEncoder(
        Coordinate.fromXY(0, -Math.abs(distanceInInches)), power);
  }

  @Override
  public void rotateClockwise(int degrees, double power) {
    rotateWithEncoder(-Converter.normalizedDegrees(degrees), -Math.abs(power));
  }

  @Override
  public void rotateCounterClockwise(int degrees, double power) {
    rotateWithEncoder(Converter.normalizedDegrees(degrees), Math.abs(power));
  }

  // Positive input means counter-clockwise
  private void rotateWithEncoder(int degrees, double power) {
    double clippedPower = Range.clip(power, -1, 1);

    setMotorMode(STOP_AND_RESET_ENCODER);
    setMotorMode(RUN_TO_POSITION);

    leftSide.addTargetPosition((int) (-degrees / 360.0 * ticksPer360));
    rightSide.addTargetPosition((int) (degrees / 360.0 * ticksPer360));

    leftSide.setPower(-clippedPower);
    rightSide.setPower(clippedPower);

    while (isBusy() && opModeIsActive()) {
    }

    stop();
    setMotorMode(RUN_USING_ENCODER);
  }

  @Override
  public void strafeRight(double distanceInInches, double power) {

  }

  @Override
  public void strafeLeft(double distanceInInches, double power) {

  }

  @Override
  public void strafeToCoordinate(double xInInches, double yInInches, double power) {

  }

  @Override
  public void strafeDegrees(int degrees, double distanceInInches, double power) {

  }

  // -- TeleOp methods --

  @Override
  public void setMovementPower(double power) {
    setMotorMode(RUN_WITHOUT_ENCODER);

    leftSide.setPower(power);
    rightSide.setPower(power);
  }

  @Override
  public void setRotationPower(double power) { //clockwise if power is positive
    setMotorMode(RUN_WITHOUT_ENCODER);

    leftSide.setPower(power);
    rightSide.setPower(-power);
  }

  @Override
  public void setStrafe(int degrees, double power) {

  }

  @Override
  public void setMovementAndRotation(double movePower, double rotatePower) {

  }

  @Override
  protected IMotor[] getMotors() {
    return new IMotor[]{leftDiagonal, rightDiagonal, leftSide, rightSide};
  }
}