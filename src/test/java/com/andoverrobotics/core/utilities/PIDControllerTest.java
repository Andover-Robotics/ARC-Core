package com.andoverrobotics.core.utilities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PIDControllerTest {
  private static final double kP = 1, kI = 0.2, kD = 0.6;
  private PIDController controller = new PIDController(kP, kI, kD);

  @Before
  public void setUp() {
    controller.reset();
  }

  @Test
  public void proportionalOnlyForFirstIteration() {
    assertEquals(10*kP, controller.runIteration(10, 0), 1e-4);
  }

  @Test
  public void secondIteration() {
    controller.runIteration(100, 0);
    assertEquals(90*kP + 1900*kI - 10/20.0*kD, controller.runIteration(90, 20), 1e-4);
  }

  @Test
  public void thirdIteration() {
    controller.runIteration(50, 0);
    controller.runIteration(45, 5);
    double out = controller.runIteration(38, 10);

    assertEquals(38*kP + 445*kI + (38-45)/5.0*kD, out, 1e-4);
  }

  @Test
  public void boundOutput() {
    controller.boundOutput(-10, 10);
    assertEquals(Math.min(20*kP, 10), controller.runIteration(20, 0), 1e-4);
    assertEquals(Math.max(-50*kP + 15*5*kI + -70/5.0*kD, -10), controller.runIteration(-50, 5), 1e-4);
  }

  @Test
  public void boundIntegralPositive() {
    controller.boundIntegral(80);
    assertEquals(20, controller.runIteration(20, 0), 1e-4);
    assertEquals(10*kP + 30*kI - 5*kD, controller.runIteration(10, 2), 1e-4);
    assertEquals(30*kP + 80*kI + 20/3.0*kD, controller.runIteration(30, 5), 1e-4);
  }

  @Test
  public void boundIntegralNegative() {
    controller.boundIntegral(80);
    assertEquals(-20, controller.runIteration(-20, 0), 1e-4);
    assertEquals(-10*kP - 30*kI + 5*kD, controller.runIteration(-10, 2), 1e-4);
    assertEquals(-30*kP - 80*kI - 20/3.0*kD, controller.runIteration(-30, 5), 1e-4);
  }
}