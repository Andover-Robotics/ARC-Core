package com.andoverrobotics.core.drivetrain;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_TO_POSITION;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_WITHOUT_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER;

import com.andoverrobotics.core.utilities.Converter;
import com.andoverrobotics.core.utilities.IMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
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
    driveWithEncoder(Math.abs(distanceInInches), Math.abs(power));
  }

  private void driveWithEncoder(double displacementInInches, double power) {
    power = Range.clip(power, -1, 1);
    power = Math.abs(power);

    if (displacementInInches < 0) {
      power *= -1;
    }

    setMotorMode(STOP_AND_RESET_ENCODER);
    setMotorMode(RUN_TO_POSITION);

    double robotTurn = displacementInInches * ticksPerInch;

    leftDiagonal.addTargetPosition((int) (robotTurn));
    rightDiagonal.addTargetPosition((int) (robotTurn));
    leftSide.addTargetPosition((int) (robotTurn));
    rightSide.addTargetPosition((int) (robotTurn));

    leftDiagonal.setPower(power);
    rightDiagonal.setPower(power);
    leftSide.setPower(power);
    rightSide.setPower(power);

    while (leftDiagonal.isBusy() && rightDiagonal.isBusy() && leftSide.isBusy() && rightSide.isBusy() && opModeIsActive()) {
      reportMotorPositions();
    }

    stop();
    setMotorMode(RUN_USING_ENCODER);
  }

  @Override
  public void driveBackwards(double distanceInInches, double power) {
    driveWithEncoder(-Math.abs(distanceInInches), -Math.abs(power));
  }

  @Override
  public void rotateClockwise(int degrees, double power) {
    power = Range.clip(power, -1, 1);
    power = Math.abs(power);
    degrees = Converter.normalizedDegrees(degrees);

    rotateWithEncoder(degrees, -degrees, power, -power);
  }

  @Override
  public void rotateCounterClockwise(int degrees, double power) {
    power = Range.clip(power, -1, 1);
    power = Math.abs(power);
    degrees = Converter.normalizedDegrees(degrees);

    rotateWithEncoder(-degrees, degrees, -power, power);
  }

  private void rotateWithEncoder(int leftDegrees, int rightDegrees,
      double leftPower, double rightPower) {

    setMotorMode(STOP_AND_RESET_ENCODER);
    setMotorMode(RUN_TO_POSITION);

    leftDiagonal.addTargetPosition((int) (leftDegrees / 360.0 * ticksPer360));
    rightDiagonal.addTargetPosition((int) (rightDegrees / 360.0 * ticksPer360));
    leftSide.addTargetPosition((int) (leftDegrees / 360.0 * ticksPer360));
    rightSide.addTargetPosition((int) (rightDegrees / 360.0 * ticksPer360));

    leftDiagonal.setPower(leftPower);
    rightDiagonal.setPower(rightPower);
    leftSide.setPower(leftPower);
    rightSide.setPower(rightPower);

    while (leftDiagonal.isBusy() && rightDiagonal.isBusy() && leftSide.isBusy() && rightSide.isBusy() && opModeIsActive()) {
      reportMotorPositions();
    }

    stop();
    setMotorMode(RUN_USING_ENCODER);
  }

  public void strafeRight(double power, double distanceInInches)
  {

    // Set the encoder mode to 3 (STOP_AND_RESET_ENCODERS)
    setMotorMode(STOP_AND_RESET_ENCODER);

    // Sets the power range
    power = Range.clip(power, -1, 1);

    // Setting the target positions
    leftDiagonal.addTargetPosition((int)(distanceInInches * -ticksPerInch));
    leftSide.addTargetPosition((int)(distanceInInches * ticksPerInch));
    rightDiagonal.addTargetPosition((int)(distanceInInches * ticksPerInch));
    rightSide.addTargetPosition((int)(distanceInInches * -ticksPerInch));

    // Set encoder mode to RUN_TO_POSITION
    setMotorMode(RUN_TO_POSITION);

    rightDiagonal.setPower(power);
    leftSide.setPower(power);
    leftDiagonal.setPower(power);
    rightSide.setPower(power);

    // While loop for updating telemetry
    while(leftDiagonal.isBusy() && rightDiagonal.isBusy() && opModeIsActive()){

      // Updates the position of the motors
      double LPos = leftDiagonal.getCurrentPosition();
      double RPos = rightDiagonal.getCurrentPosition();

      while (leftDiagonal.isBusy() && rightDiagonal.isBusy() && leftSide.isBusy() && rightSide.isBusy() && opModeIsActive()) {
        reportMotorPositions();
      }

    }

    // Stops the motors
    stop();

    // Resets to run using encoders mode
    setMotorMode(RUN_USING_ENCODER);
  }

  public void strafeLeft(double power, double distanceInInches){
    strafeRight(power, -distanceInInches);
  }

  @Override
  public void strafeToCoordinate(double xInInches, double yInInches, double power) {

  }

  @Override
  public void strafeDegrees(int degrees, double distanceInInches, double power) {

  }

  @Override
  public void setDegreeOfStrafe(int degrees, double power) {

  }

  // -- TeleOp methods --

  @Override
  public void setMovementPower(double power) {
    setMotorMode(RUN_WITHOUT_ENCODER);

    leftDiagonal.setPower(power);
    rightDiagonal.setPower(power);
    leftSide.setPower(power);
    rightSide.setPower(power);
  }

  @Override
  public void setRotationPower(double power) { //clockwise if power is positive
    setMotorMode(RUN_WITHOUT_ENCODER);

    leftDiagonal.setPower(power);
    rightDiagonal.setPower(-power);
    leftSide.setPower(power);
    rightSide.setPower(-power);
  }

  @Override
  public void setMovementAndRotation(double movePower, double rotatePower) {

  }

  @Override
  protected DcMotor[] getMotors() {
    return new DcMotor[]{leftDiagonal, rightSide, leftSide, rightSide};
  }

  private void reportMotorPositions() {
    double FLPos = leftDiagonal.getCurrentPosition();
    double FRPos = rightDiagonal.getCurrentPosition();
    double BLPos = leftSide.getCurrentPosition();
    double BRPos = rightSide.getCurrentPosition();

    opMode.telemetry.addData("leftDiagonal Pos:", FLPos);
    opMode.telemetry.addData("rightDiagonal Pos:", FRPos);
    opMode.telemetry.addData("leftSide Pos:", BLPos);
    opMode.telemetry.addData("rightSide Pos:", BRPos);

    opMode.telemetry.update();
  }
}