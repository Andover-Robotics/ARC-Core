package com.andoverrobotics.core.examples;

import com.andoverrobotics.core.drivetrain.TankDrive;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

@Autonomous(name = "TankDrive Autonomous Example", group = "ARC")
public class TankDriveAutonomousExample extends LinearOpMode {

  private static final int TICKS_PER_INCH = 1000, TICKS_PER_360 = 1440 * 4;

    @Override
    public void runOpMode() {

        DcMotor motorL = hardwareMap.dcMotor.get("motorL");
        DcMotor motorR = hardwareMap.dcMotor.get("motorR");
        motorL.setDirection(Direction.REVERSE);


        TankDrive tankDrive = TankDrive.fromMotors(motorL, motorR, this, TICKS_PER_INCH, TICKS_PER_360);


        TankDrive tankDrive = TankDrive.fromMotors(motorL, motorR, this, ticksPerInch, ticksPer360);

        waitForStart();

        for (int i = 0; i < 4; i++) {
            tankDrive.driveForwards(36, 0.25);
            telemetry.addLine("Traveled 36 inches");
            telemetry.update();

            tankDrive.rotateClockwise(90, 0.25);
            telemetry.addLine("Turned 90 degrees clockwise");
            telemetry.update();
        }
    }
}
