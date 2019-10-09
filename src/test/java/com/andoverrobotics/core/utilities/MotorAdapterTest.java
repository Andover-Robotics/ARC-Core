package com.andoverrobotics.core.utilities;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Matchers;

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
    verify(targetMotor).setPower(1.0);
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

    testee.addTargetPosition(-40);
    verify(targetMotor).setTargetPosition(-10);
  }

  @Test
  public void startRunToPositionWithPositiveOffset() {
    when(targetMotor.getCurrentPosition()).thenReturn(0);

    testee.startRunToPosition(100, 1);

    verifyStartRunToPosition(100, 1);
  }

  @Test
  public void startRunToPositionWithNegativeOffset() {
    when(targetMotor.getCurrentPosition()).thenReturn(20);

    testee.startRunToPosition(-100, -1);

    verifyStartRunToPosition(20 - 100, 1);
  }

  @Test
  public void startRunToPositionWithSignMismatch() {
    when(targetMotor.getCurrentPosition()).thenReturn(0);

    testee.startRunToPosition(200, -0.7);

    verifyStartRunToPosition(200, 0.7);
  }

  @Test
  public void startRunToPositionWithZeros() {
    when(targetMotor.getCurrentPosition()).thenReturn(0);

    testee.startRunToPosition(0, 1);

    verify(targetMotor, never()).setMode(RunMode.RUN_TO_POSITION);
    verify(targetMotor, never()).setPower(ArgumentMatchers.anyDouble());
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

  private void verifyStartRunToPosition(int target, double power) {
    InOrder order = inOrder(targetMotor);
    order.verify(targetMotor).setTargetPosition(target);
    order.verify(targetMotor).setMode(RunMode.RUN_TO_POSITION);
    order.verify(targetMotor).setPower(power);
  }
}
