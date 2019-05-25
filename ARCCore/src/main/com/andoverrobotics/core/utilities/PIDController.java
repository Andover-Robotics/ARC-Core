package com.andoverrobotics.core.utilities;

public class PIDController {
  private final double kP, kI, kD;

  private double errorSum = 0, prevError;
  private long prevTime;
  private boolean isInitialized = false;

  private double outputMin = Double.NEGATIVE_INFINITY, outputMax = Double.POSITIVE_INFINITY;
  private double maxErrorSum = Double.MAX_VALUE;

  public PIDController(double kP, double kI, double kD) {
    this.kP = kP;
    this.kI = kI;
    this.kD = kD;
  }

  public void reset() {
    errorSum = 0;
    prevError = 0;
    prevTime = 0;
    isInitialized = false;
  }

  public void boundOutput(double min, double max) {
    outputMin = min;
    outputMax = max;
  }

  public void boundIntegral(double max) {
    maxErrorSum = max;
  }

  public double runIteration(double error, long time) {
    if (!isInitialized)
      return initialIteration(error, time);

    double output = error * kP;
    errorSum += (prevError + error) * (time - prevTime) / 2;
    boundErrorSum();
    output += errorSum * kI;
    output += (error - prevError) / (time - prevTime) * kD;

    prevError = error;
    prevTime = time;

    return bounded(output);
  }

  private void boundErrorSum() {
    errorSum = Math.min(Math.abs(errorSum), Math.abs(maxErrorSum)) * Math.signum(errorSum);
  }

  private double initialIteration(double error, long time) {
    isInitialized = true;

    prevError = error;
    prevTime = time;
    return bounded(error * kP);
  }

  private double bounded(double output) {
    return Math.max(outputMin, Math.min(outputMax, output));
  }
}
