package com.andoverrobotics.core.drivetrain;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_WITHOUT_ENCODER;

import com.andoverrobotics.core.utilities.Converter;
import com.andoverrobotics.core.utilities.IMotor;
import com.andoverrobotics.core.utilities.MotorAdapter;
import com.andoverrobotics.core.utilities.MotorPair;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

public class TankDrive extends DriveTrain {

  private IMotor motorL;
  private IMotor motorR;
  private final int ticksPerInch;
  private final int ticksPer360;

  // Verify that the motor(s) on one side is reversed if the motors point in opposite directions!

  /**
   * Creates a TankDrive from two IMotors
   *
   * @param motorL The left IMotor
   * @param motorR The right IMotor
   * @param opMode The OpMode to set
   * @param ticksPerInch How many ticks per inch of displacement
   * @param ticksPer360 How many ticks in one full rotation of the motor
   */
  public TankDrive(IMotor motorL, IMotor motorR, OpMode opMode,
      int ticksPerInch, int ticksPer360) {
    super(opMode);

    this.motorL = motorL;
    this.motorR = motorR;
    this.ticksPerInch = ticksPerInch;
    this.ticksPer360 = ticksPer360;
  }

  /**
   * Creates a TankDrive from two DcMotors.
   *
   * @param motorL The left DcMotor
   * @param motorR The right DcMotor
   * @param opMode The OpMode to set
   * @param ticksPerInch How many ticks per inch of displacement
   * @param ticksPer360 How many ticks in one full rotation of the motor
   *
   * @return A TankDrive created with the inputted motors
   */
  public static TankDrive fromMotors(DcMotor motorL, DcMotor motorR, OpMode opMode,
      int ticksPerInch, int ticksPer360) {

    return new TankDrive(new MotorAdapter(motorL),
        new MotorAdapter(motorR), opMode, ticksPerInch, ticksPer360);
  }

  /**
   * Creates a TankDrive from four DcMotors.
   *
   * @param motorL1 One of the left DcMotors
   * @param motorR1 One of the right DcMotors
   * @param motorL2 The other left DcMotor
   * @param motorR2 The other right DcMotor
   * @param opMode The OpMode to set
   * @param ticksPerInch How many ticks per inch of displacement
   * @param ticksPer360 How many ticks in one full rotation of the motor
   *
   * @return A TankDrive created using MotorPairs
   */
  public static TankDrive fromMotors(DcMotor motorL1, DcMotor motorL2, DcMotor motorR1, DcMotor motorR2,
      OpMode opMode, int ticksPerInch, int ticksPer360) {

    return new TankDrive(
        MotorPair.of(motorL1, motorL2),
        MotorPair.of(motorR1, motorR2), opMode, ticksPerInch, ticksPer360);
  }

  /**
   * Drive forward a certain amount with encoders.
   *
   * @param distanceInInches The distance to move forwards
   * @param power The motor power
   */
  @Override
  public void driveForwards(double distanceInInches, double power) {
    driveWithEncoder(Math.abs(distanceInInches), Math.abs(power));
  }

  private void driveWithEncoder(double displacementInInches, double givenPower) {

    if (givenPower == 0) {
      stop();
      return;
    }

    double power = Math.abs(Range.clip(givenPower, -1, 1));

    if (displacementInInches < 0) {
      power *= -1;
    }

    double robotTurn = displacementInInches * ticksPerInch;

    runWithEncoder((int) robotTurn, (int) robotTurn, power, power);
  }

  /**
   * Drive backwards a certain amount with encoders.
   *
   * @param distanceInInches The distance to move backwards
   * @param power The motor power
   */
  @Override
  public void driveBackwards(double distanceInInches, double power) {
    driveWithEncoder(-Math.abs(distanceInInches), -Math.abs(power));
  }

  /**
   * Rotate a certain amount with encoders clockwise.
   *
   * @param degrees The desired clockwise rotation in degrees
   * @param givenPower The motor power
   */
  @Override
  public void rotateClockwise(int degrees, double givenPower) {
    double power = Range.clip(givenPower, -1, 1);
    power = Math.abs(power);
    double normalizedDegrees = Converter.normalizedDegrees(degrees);

    rotateWithEncoder(normalizedDegrees, -normalizedDegrees, power, -power);
  }

  /**
   * Rotate a certain amount with encoders counterclockwise.
   *
   * @param degrees The desired counterclockwise rotation in degrees
   * @param givenPower The motor power
   */
  @Override
  public void rotateCounterClockwise(int degrees, double givenPower) {
    double power = Range.clip(givenPower, -1, 1);
    power = Math.abs(power);
    double normalizedDegrees = Converter.normalizedDegrees(degrees);

    rotateWithEncoder(-normalizedDegrees, normalizedDegrees, -power, power);
  }

  private void rotateWithEncoder(double leftDegrees, double rightDegrees,
      double leftPower, double rightPower) {

    runWithEncoder(
        (int) Math.round(leftDegrees / 360.0 * ticksPer360),
        (int) Math.round(rightDegrees / 360.0 * ticksPer360),
        leftPower, rightPower);
  }

  private void runWithEncoder(int leftTickOffset, int rightTickOffset,
      double leftPower, double rightPower) {

    // Fails unit tests
    /*Log.d("TankDrive Encoder",
        String.format("leftTickOffset=%d rightTickOffset=%d leftPower=%.3f rightPower=%.3f",
        leftTickOffset, rightTickOffset, leftPower, rightPower));*/

    motorL.startRunToPosition(leftTickOffset, leftPower);
    motorR.startRunToPosition(rightTickOffset, rightPower);

    while (isBusy() && opModeIsActive()) {
    }

    stop();
    setMotorMode(RUN_USING_ENCODER);
  }

  // -- TeleOp methods --

  /**
   * Set the power of the motors to move in a straight line without encoders.
   *
   * @param power The motor power to set [-1, 1]
   */
  @Override
  public void setMovementPower(double power) {
    setMotorMode(RUN_WITHOUT_ENCODER);

    motorL.setPower(power);
    motorR.setPower(power);
  }

  /**
   * Set the power of the motors to rotate without encoders.
   *
   * @param power The motor power to set [-1, 1]
   */
  @Override
  public void setRotationPower(double power) { //clockwise if power is positive
    setMotorMode(RUN_WITHOUT_ENCODER);

    motorL.setPower(power);
    motorR.setPower(-power);
  }

  /**
   * Set both the movement power and the rotation power of the the motors.
   *
   * @param movePower The motor power to set for movement [-1, 1]
   * @param rotatePower The motor power to set for rotation [-1, 1]
   */
  @Override
  public void setMovementAndRotation(double movePower, double rotatePower) {
    double leftPower = movePower + rotatePower,
        rightPower = movePower - rotatePower,
        maxAbsPower = Math.max(Math.abs(leftPower), Math.abs(rightPower));

    if (maxAbsPower > 1) {
      leftPower /= maxAbsPower;
      rightPower /= maxAbsPower;
    }

    motorL.setPower(leftPower);
    motorR.setPower(rightPower);
  }

  @Override
  protected IMotor[] getMotors() {
    return new IMotor[]{motorL, motorR};
  }

}
