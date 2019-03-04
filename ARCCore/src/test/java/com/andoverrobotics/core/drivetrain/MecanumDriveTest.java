package com.andoverrobotics.core.drivetrain;

import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.andoverrobotics.core.utilities.Coordinate;
import com.andoverrobotics.core.utilities.IMotor;
import com.andoverrobotics.core.utilities.MotorAdapter;
import com.andoverrobotics.core.utilities.MotorPair;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;

public class MecanumDriveTest {

  private OpMode opMode = mock(OpMode.class);
  private MotorAdapter motorFL = mock(MotorAdapter.class),
      motorFR = mock(MotorAdapter.class),
      motorBL = mock(MotorAdapter.class),
      motorBR = mock(MotorAdapter.class);
  // This is an octagonal config
  private MotorAdapter[] leftSide = {motorFL, motorBL},
      rightSide = {motorFR, motorBR},
      leftDiagonal = {motorBL, motorFR},
      rightDiagonal = {motorBR, motorFL},
      allMotors = {motorFL, motorFR, motorBL, motorBR};

  private StrafingDriveTrain driveTrain = new MecanumDrive(motorFL, motorFR, motorBL, motorBR,
      new MotorAdapter[]{
          motorFR,
          motorBL
      }, new MotorAdapter[]{
      motorFL,
      motorBR
  }, opMode, 5, 100);

  @Before
  public void setUp() {
    reset(motorFL, motorFR, motorBL, motorBR, opMode);
  }

  @Test
  public void driveForwards() {
    driveTrain.driveForwards(5, 1);

    int tickOffset = (int) (5 * 5 / Math.sqrt(2));

    verifyAllMotorsRunToPosition(tickOffset, 1);
  }

  @Test
  public void driveBackwardsPositivePower() {
    driveTrain.driveBackwards(8, 0.6);

    int tickOffset = (int) (-8 * 5 / Math.sqrt(2));

    verifyAllMotorsRunToPosition(tickOffset, 0.6);
  }

  @Test
  public void driveBackwardsNegativePower() {
    driveTrain.driveBackwards(8, -0.4);

    int tickOffset = (int) (-8 * 5 / Math.sqrt(2));

    verifyAllMotorsRunToPosition(tickOffset, 0.4);
  }

  @Test
  public void driveBackwardsNegativeDistance() {
    driveTrain.driveBackwards(-4, 0.6);

    int tickOffset = (int) (-4 * 5 / Math.sqrt(2));

    verifyAllMotorsRunToPosition(tickOffset, 0.6);
  }

  @Test
  public void rotateClockwise() {
    driveTrain.rotateClockwise(50, 0.6);

    verifyRunToPosition((int) (50 / 360.0 * 100), 0.6, motorBL, motorFL);
    verifyRunToPosition((int) (-50 / 360.0 * 100), 0.6, motorBR, motorFR);
  }

  @Test
  public void rotateClockwiseNegativeDegree() {
    driveTrain.rotateClockwise(-36, 0.2);

    verifyRunToPosition((int) ((360 - 36) / 360.0 * 100), 0.2, leftSide);
    verifyRunToPosition((int) ((36 - 360) / 360.0 * 100), 0.2, rightSide);
  }

  @Test
  public void rotateCounterClockwise() {
    driveTrain.rotateCounterClockwise(50, -0.5);

    verifyRunToPosition((int) (-50 / 360.0 * 100), 0.5, leftSide);
    verifyRunToPosition((int) (50 / 360.0 * 100), 0.5, rightSide);
  }

  @Test
  public void rotateCounterClockwiseNegativeDegree() {
    driveTrain.rotateCounterClockwise(-70, -0.8);

    verifyRunToPosition((int) ((70 - 360) / 360.0 * 100), 0.8, leftSide);
    verifyRunToPosition((int) ((360 - 70) / 360.0 * 100), 0.8, rightSide);
  }

  @Test
  public void strafeRight() {
    driveTrain.strafeRight(20.5, 0.6);

    int tickOffset = (int) (20.5 * 5 / Math.sqrt(2));

    verifyRunToPosition(-tickOffset, -0.6, leftDiagonal);
    verifyRunToPosition(tickOffset, 0.6, rightDiagonal);
  }

  @Test
  public void strafeRightNegativeDistance() {
    driveTrain.strafeRight(-14.8, -0.7);

    int tickOffset = (int) (14.8 * 5 / Math.sqrt(2));

    verifyRunToPosition(-tickOffset, -0.7, leftDiagonal);
    verifyRunToPosition(tickOffset, 0.7, rightDiagonal);
  }

  @Test
  public void strafeLeft() {
    driveTrain.strafeLeft(23.1, 0.4);

    int tickOffset = (int) (23.1 * 5 / Math.sqrt(2));

    verifyRunToPosition(tickOffset, 0.4, leftDiagonal);
    verifyRunToPosition(-tickOffset, -0.4, rightDiagonal);
  }

  @Test
  public void strafeLeftNegativeDistance() {
    driveTrain.strafeLeft(-24.5, -0.64);

    int tickOffset = (int) (24.5 * 5 / Math.sqrt(2));

    verifyRunToPosition(tickOffset, 0.64, leftDiagonal);
    verifyRunToPosition(-tickOffset, -0.64, rightDiagonal);
  }

  @Test
  public void strafeInches() {
    driveTrain.strafeInches(10, 15, 0.4);

    verifyRunToPosition((int) (3.5355339 * 5), 0.08, leftDiagonal);
    verifyRunToPosition((int) (17.67766953 * 5), 0.4, rightDiagonal);
  }

  @Test
  public void strafeInchesNullVector() {
    driveTrain.strafeInches(0, 0, 0.4);

    verifyNoMoreInteractions((Object[]) allMotors);
  }

  @Test
  public void setMovementPower() {
    driveTrain.setMovementPower(0.7);
    verifyPowersWithoutEncoder(0.7, 0.7);
  }

  @Test
  public void setRotationPower() {
    driveTrain.setRotationPower(0.4);
    verifyPowersWithoutEncoder(0.4, -0.4);
  }

  @Test
  public void setRotationPowerNegative() {
    driveTrain.setRotationPower(-1.2);
    verifyPowersWithoutEncoder(-1, 1);
  }

  @Test
  public void setMovementAndRotation() {
    driveTrain.setMovementAndRotation(0.5, 0.2);
    verifyPowersWithoutEncoder(0.7, 0.3);
  }

  @Test
  public void setMovementAndRotationWithSingleOverflow() {
    driveTrain.setMovementAndRotation(0.8, -0.6);
    verifyPowersWithoutEncoder(0.2 / 1.4, 1);
  }

  @Test
  public void setMovementAndRotationWithMultipleOverflows() {
    driveTrain.setMovementAndRotation(0.8, 5);
    verifyPowersWithoutEncoder(1, -4.2 / 5.8);
  }

  @Test
  public void setStrafeWithinUnitCircle() {
    driveTrain.setStrafe(Coordinate.fromXY(0.5, 0.7), 1);

    verifyPairPower(0.141421356, leftDiagonal);
    verifyPairPower(0.8485281374, rightDiagonal);
  }

  @Test
  public void setStrafeOutsideUnitCircle() {
    driveTrain.setStrafe(Coordinate.fromXY(525, -1441), 0.5);

    verifyPairPower(-0.45322105262, leftDiagonal);
    verifyPairPower(-0.211165047915, rightDiagonal);
  }

  @Test
  public void setStrafeWithOrigin() {
    driveTrain.setStrafe(0, 0, Double.MAX_VALUE);

    verifyPairPower(0, leftDiagonal);
    verifyPairPower(0, rightDiagonal);
  }

  private void verifyPowersWithoutEncoder(double leftPower, double rightPower) {

    for (IMotor motor : allMotors) {
      verify(motor).setMode(RunMode.RUN_WITHOUT_ENCODER);
    }

    verifyPairPower(leftPower, leftSide);
    verifyPairPower(rightPower, rightSide);
  }

  private void verifyAllMotorsRunToPosition(int tickOffset, double power) {
    verifyRunToPosition(tickOffset, power, motorFL, motorFR, motorBL, motorBR);
  }

  private void verifyRunToPosition(int tickOffset, double power, MotorAdapter... mocks) {
    for (MotorAdapter mock : mocks) {
      verify(mock).startRunToPosition(eq(tickOffset), AdditionalMatchers.eq(Math.abs(power), 1e-4));
    }
  }

  private void verifyPairPower(double power, MotorAdapter... pair) {
    for (MotorAdapter motor : pair) {
      verify(motor).setPower(AdditionalMatchers.eq(power, 1e-3));
    }
  }
}