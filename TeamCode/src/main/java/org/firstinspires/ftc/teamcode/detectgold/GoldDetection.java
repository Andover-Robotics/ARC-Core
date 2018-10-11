package org.firstinspires.ftc.teamcode.detectgold;

import com.andoverrobotics.core.drivetrain.TankDrive;
import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

@Autonomous(name = "Gold Detection Test", group = "DogeCV")
public class GoldDetection extends LinearOpMode {
    private static final int ticksPerInch = 20, ticksPer360 = 200;
    private final double CAM_FOCAL_LENGTH = 751.0, GOLD_WIDTH_IN = 2; // Approximate focal length of a Moto G (2nd gen): 637.5
    private ThunderGoldAlignDetector detector;
    private DcMotor motorL, motorR;
    private TankDrive tankDrive;

    @Override
    public void runOpMode() {
        setup();

        double perpendicularDistance = distanceFromGold(detector.getBestRectWidth());
        if (detector.isFound() && !Double.isInfinite(perpendicularDistance) && !detector.bestRectIsNull()) {
            double cubeDistance = cubeDistanceFromCenter(detector.getBestRectWidth());

            if (cubeDistance != Double.MAX_VALUE) {
                double angle = Math.toDegrees(Math.atan(cubeDistance / perpendicularDistance)); // The angle to turn, in degrees. Negative = clockwise, positive = counterclockwise

                double distanceToTravel = (int) (Math.sqrt(Math.pow(perpendicularDistance, 2) + Math.pow(cubeDistance, 2)) + 0.99); //Use the pythagorean theorem to calculate the length of the hypotenuse. Always rounds up to an integer to ensure that the robot will reach the gold every time


                if (Math.abs(angle) <= 2)
                    angle = 0; //Practically head on, no point turning

                telemetry.addData("Distance", perpendicularDistance);
                telemetry.addData("Cube Dist", cubeDistance);
                telemetry.addData("Angle", angle);
                telemetry.addData("Hypotenuse (Rounded)", distanceToTravel);

                int roundedAngle = (angle >= 0) ? (int) (angle + 0.5) : (int) (angle - 0.5); //Round to the nearest integer
                tankDrive.rotateCounterClockwise(roundedAngle, 0.5); //Positive is counterclockwise, passing in a negative turns clockwise, so this works without any conditionals
                tankDrive.driveForwards(distanceToTravel, 0.5);

                //Go back to the stating position
                tankDrive.driveBackwards(distanceToTravel, 0.5);
                tankDrive.rotateClockwise(roundedAngle, 0.5);
                detector.disable();
            }
        }
    }

    private void setup(){
        telemetry.addData("Status", "Gold Detection Test");

        detector = new ThunderGoldAlignDetector();
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance(), 0, false);
        detector.useDefaults();

        // Optional Tuning
        detector.alignSize = 100; // How wide (in pixels) is the range in which the gold object will be aligned. (Represented by green bars in the preview)
        detector.alignPosOffset = 0; // How far from center frame to offset this alignment zone.
        detector.downscale = 0.4; // How much to downscale the input frames

        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; // Can also be PERFECT_AREA
        //detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
        detector.maxAreaScorer.weight = 0.005;

        detector.ratioScorer.weight = 5;
        detector.ratioScorer.perfectRatio = 1.0;

        detector.enable();

        motorR = hardwareMap.dcMotor.get("motorR");
        motorL = hardwareMap.dcMotor.get("motorL");
        motorL.setDirection(Direction.REVERSE);

        tankDrive = TankDrive.fromMotors(motorL, motorR, this, ticksPerInch, ticksPer360);
    }

    private double calculateFocalLength(int goldWithPX, int distanceFromObjIn) {
        return goldWithPX * distanceFromObjIn / GOLD_WIDTH_IN;
    }

    private double distanceFromGold(int goldWidthPX) {
        return GOLD_WIDTH_IN * CAM_FOCAL_LENGTH / goldWidthPX;
    }

    private double cubeDistanceFromCenter(double goldWidthPX) {
        return GOLD_WIDTH_IN * detector.distanceToVerticalCenter() / goldWidthPX; //Solve the ratio
    }
}
