package com.andoverrobotics.core.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CachedMotorTest {
  private DcMotor dcMotor = mock(DcMotor.class);
  private CachedMotor motor = new CachedMotor(dcMotor);

  @Before
  public void setUp() {
    reset(dcMotor);
  }

  @Test
  public void cachedSetPower() {
    motor.setPower(1.0);
    verify(dcMotor).setPower(1.0);

    motor.setPower(1.0);
    verifyNoMoreInteractions(dcMotor);

    motor.setPower(-0.4);
    verify(dcMotor).setPower(-0.4);
  }

  @Test
  public void cachedGetPower() {
    motor.setPower(0.5);
    verify(dcMotor).setPower(0.5);

    assertEquals(0.5, motor.getPower(), 1e-5);
    verifyNoMoreInteractions(dcMotor);
  }

  @Test
  public void cachedSetTargetPosition() {
    motor.setTargetPosition(50);
    verify(dcMotor).setTargetPosition(50);

    motor.setTargetPosition(50);
    verifyNoMoreInteractions(dcMotor);

    motor.setTargetPosition(20);
    verify(dcMotor).setTargetPosition(20);
  }

  // The SDK requires us to set a target position before setting the mode to RUN_TO_POSITION.
  // Therefore, the initial target position is UNDEFINED, not 0.
  // If we set the target position to 0 in our first call to setTargetPosition, it should probably go through.
  @Test
  public void initialSetTargetPositionToZeroPassesThrough() {
    motor.setTargetPosition(0);
    verify(dcMotor).setTargetPosition(0);

    motor.setTargetPosition(0);
    verifyNoMoreInteractions(dcMotor);
  }

  @Test
  public void cachedGetTargetPosition() {
    motor.setTargetPosition(100);
    verify(dcMotor).setTargetPosition(100);

    assertEquals(100, motor.getTargetPosition());
    verifyNoMoreInteractions(dcMotor);
  }

  @Test
  public void cachedSetMode() {
    motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    verify(dcMotor).setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    verifyNoMoreInteractions(dcMotor);

    motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    verify(dcMotor).setMode(DcMotor.RunMode.RUN_TO_POSITION);
  }

  @Test
  public void cachedGetMode() {
    motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    verify(dcMotor).setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    assertEquals(DcMotor.RunMode.RUN_USING_ENCODER, motor.getMode());
    verifyNoMoreInteractions(dcMotor);
  }

  @Test
  public void permitAccess() {
    assertEquals(dcMotor, motor.getMotor());
  }

  @Test
  public void limitSetPowerRange() {
    motor.setPower(1.0);
    verify(dcMotor).setPower(1.0);

    motor.setPower(1.2);
    verifyNoMoreInteractions(dcMotor);
    assertEquals(1.0, motor.getPower(), 1e-5);

    motor.setPower(-1.2);
    verify(dcMotor).setPower(-1.0);
    assertEquals(-1.0, motor.getPower(), 1e-5);
  }
}