package com.andoverrobotics.core.drivetrain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.andoverrobotics.core.utilities.MotorPair;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;

public class MecanumDriveTest {
  private OpMode opMode = mock(OpMode.class);
  private MotorPair
      rightDiagonal = mock(MotorPair.class),
      leftDiagonal = mock(MotorPair.class),
      rightSide = mock(MotorPair.class),
      leftSide = mock(MotorPair.class);

  private StrafingDriveTrain driveTrain = new MecanumDrive(
      leftDiagonal, rightDiagonal, leftSide, rightSide,
      opMode, 5, 10);

  @Before
  public void setUp() {
    reset(rightDiagonal, leftDiagonal, rightSide, leftSide, opMode);
  }

  @Test
  public void driveForwards() {
    driveTrain.driveForwards(5, 1);

    verify(leftDiagonal).addTargetPosition(5 * 5);
    verify(rightDiagonal).addTargetPosition(5 * 5);

    verifyDiagonalPowers(1, 1);
  }

  @Test
  public void driveBackwardsPositivePower() {
    driveTrain.driveBackwards(8, 0.6);

    verify(leftDiagonal).addTargetPosition(-8 * 5);
    verify(rightDiagonal).addTargetPosition(-8 * 5);

    verifyDiagonalPowers(-0.6, -0.6);
  }

  @Test
  public void driveBackwardsNegativePower() {
    driveTrain.driveBackwards(8, -0.4);

    verify(leftDiagonal).addTargetPosition(-8 * 5);
    verify(rightDiagonal).addTargetPosition(-8 * 5);

    verifyDiagonalPowers(-0.4, -0.4);
  }

  @Test
  public void driveBackwardsNegativeDistance() {
    driveTrain.driveBackwards(-4, 0.6);

    verify(leftDiagonal).addTargetPosition(-4 * 5);
    verify(rightDiagonal).addTargetPosition(-4 * 5);

    verifyDiagonalPowers(-0.6, -0.6);
  }

  @Test
  public void rotateClockwise() {
  }

  @Test
  public void rotateCounterClockwise() {
  }

  @Test
  public void strafeRight() {
  }

  @Test
  public void strafeLeft() {
  }

  @Test
  public void setMovementPower() {
  }

  @Test
  public void setRotationPower() {
  }

  private void verifyDiagonalPowers(double powerLeftDiagonal, double powerRightDiagonal) {
    verifyPairPower(leftDiagonal, powerLeftDiagonal);
    verifyPairPower(rightDiagonal, powerRightDiagonal);
  }

  private void verifyPairPower(MotorPair pair, double power) {
    verify(pair).setPower(AdditionalMatchers.eq(power, 1e-5));
  }
}