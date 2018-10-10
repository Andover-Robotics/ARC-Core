package org.firstinspires.ftc.teamcode.detectgold;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Gold Detection Test", group = "DogeCV")
public class GoldDetection extends OpMode {
    // Approximate focal length of a Moto G (2nd gen): 637.5
    private final double CAM_FOCAL_LENGTH = 751.0, GOLD_WIDTH_IN = 2;
    private ThunderGoldAlignDetector detector;

    //TODO: Setup DriveTrain to move the test bot

    @Override
    public void init() {

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
    }

    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {

    }

    @Override
    public void loop() {
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

                //TODO: Turn angle degrees to the right or left
                //TODO: Drive distanceToTravel inches
                //TODO: disable detector using detector.disable()
            }
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        detector.disable();
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
