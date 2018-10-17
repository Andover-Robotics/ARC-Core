package com.andoverrobotics.core.utilities;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import org.junit.After;
import org.junit.Test;

public class MotorAdapterTest {
  private DcMotor targetMotor = mock(DcMotor.class);
  private MotorAdapter testee = new MotorAdapter(targetMotor);

  @After
  public void tearDown() {
    reset(targetMotor);
  }

  @Test
  public void setPower() {
    testee.setPower(0.5);
    verify(targetMotor).setPower(0.5);

    testee.setPower(0);
    verify(targetMotor).setPower(0);

    testee.setPower(2.0);
    verify(targetMotor).setPower(2.0);
  }

  @Test
  public void addTargetPositionToZero() {
    when(targetMotor.getCurrentPosition()).thenReturn(0);
    testee.addTargetPosition(25);
    verify(targetMotor).setTargetPosition(25);
  }

  @Test
  public void addTargetPositionToPositive() {
    when(targetMotor.getCurrentPosition()).thenReturn(120);
    testee.addTargetPosition(50);
    verify(targetMotor).setTargetPosition(120 + 50);
  }

  @Test
  public void addNegativeTargetPositionToZero() {
    when(targetMotor.getCurrentPosition()).thenReturn(0);
    testee.addTargetPosition(-30);
    verify(targetMotor).setTargetPosition(-30);
  }

  @Test
  public void addNegativeTargetPositionToPositive() {
    when(targetMotor.getCurrentPosition()).thenReturn(30);
    testee.addTargetPosition(-30);
    verify(targetMotor).setTargetPosition(0);
  }

  @Test
  public void startRunToPositionWithPositiveOffset() {
    when(targetMotor.getCurrentPosition()).thenReturn(0);

    testee.startRunToPosition(100, 1);

    verify(targetMotor).setMode(RunMode.RUN_TO_POSITION);
    verify(targetMotor).setTargetPosition(100);
    verify(targetMotor).setPower(1);
  }

  @Test
  public void startRunToPositionWithNegativeOffset() {
    when(targetMotor.getCurrentPosition()).thenReturn(20);

    testee.startRunToPosition(-100, -1);

    verify(targetMotor).setMode(RunMode.RUN_TO_POSITION);
    verify(targetMotor).setTargetPosition(20 - 100);
    verify(targetMotor).setPower(-1);
  }

  @Test
  public void startRunToPositionWithSignMismatch() {
    when(targetMotor.getCurrentPosition()).thenReturn(0);

    testee.startRunToPosition(200, -0.7);

    verify(targetMotor).setMode(RunMode.RUN_TO_POSITION);
    verify(targetMotor).setTargetPosition(200);
    verify(targetMotor).setPower(0.7);
  }

  @Test
  public void startRunToPositionWithZeros() {
    when(targetMotor.getCurrentPosition()).thenReturn(0);

    testee.startRunToPosition(0, 1);

    verifyNoMoreInteractions(targetMotor);
  }

  @Test
  public void setRunMode() {
    testee.setMode(RunMode.RUN_WITHOUT_ENCODER);
    verify(targetMotor).setMode(RunMode.RUN_WITHOUT_ENCODER);

    testee.setMode(RunMode.STOP_AND_RESET_ENCODER);
    verify(targetMotor).setMode(RunMode.STOP_AND_RESET_ENCODER);

    testee.setMode(RunMode.RUN_TO_POSITION);
    verify(targetMotor).setMode(RunMode.RUN_TO_POSITION);
  }

  @Test
  public void isBusy() {
    when(targetMotor.isBusy()).thenReturn(false);
    assertFalse(testee.isBusy());

    when(targetMotor.isBusy()).thenReturn(true);
    assertTrue(testee.isBusy());
  }
}
