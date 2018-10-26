package org.firstinspires.ftc.teamcode.detectgold;

import com.andoverrobotics.core.drivetrain.TankDrive;
import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.Dogeforia;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

@Autonomous(name = "Gold Detection Test", group = "DogeCV")
public class GoldDetection extends LinearOpMode {
    //The distance between the front wheels, the back wheels, and the front and the back wheels, in inches. Currently unset because measuring is hard.
    private static final double FRONT_WHEEL_DISTANCE = 14.8, BACK_WHEEL_DISTANCE = 14.8, FRONT_BACK_DISTANCE = 12.25;

    //TICKS_PER_WHEEL_360: how many ticks of a motor to make a wheel turn 360
    //ticksPer360: how many encoder ticks required to cause a full rotation for the robot, when this amount is applied to the left and right motors in opposite directions
    //ticksPer360 is currently calculated by multiplying ticksPerInch by the circumference of the circle with the rear axle as a diameter, as those are the wheels that are moving
    //ticksPerInch and ticksPer360 are rounded to the nearest integer
    private static final int WHEEL_DIAMETER_IN = 4, TICKS_PER_WHEEL = 1440, TICKS_PER_INCH = (int)(TICKS_PER_WHEEL / (Math.PI * WHEEL_DIAMETER_IN) + 0.5), TICKS_PER_360 = (int)(TICKS_PER_INCH * Math.PI * 10.55 + 0.5);
    // KNOWN MOTOR TICKS (TICKS_PER_WHEEL_360):
    //     Tetrix DC Motors: 1440
    //     AndyMark NeveRest Motors: 1120 (Not 100% sure)

    private final double CAM_FOCAL_LENGTH = 751.0, GOLD_WIDTH_IN = 2; // Approximate focal length of a Moto G (2nd gen): 637.5

    // last year's Vuforia key
    private final String VUFORIA_KEY = "AQRacK7/////AAAAGea1bsBsYEJvq6S3KuXK4PYTz4IZmGA7SV88bdM7l26beSEWkZTUb8H352Bo/ZMC6krwmfEuXiK7d7qdFkeBt8BaD0TZAYBMwHoBkb7IBgMuDF4fnx2KiQPOvwBdsIYSIFjiJgGlSj8pKZI+M5qiLb3DG3Ty884EmsqWQY0gjd6RNhtSR+6oiXazLhezm9msyHWZtX5hQFd9XoG5npm4HoGaZNdB3g5YCAQNHipjTm3Vkf71rG/Fffif8UTCI1frmKYtb4RvqiixDSPrD6OG6YmbsPOYUt2RZ6sSTreMzVL76CNfBTzmpo2V0E6KKP2y9N19hAum3GZu3G/1GEB5D+ckL/CXk4JM66sJw3PGucCs";

    private ThunderGoldAlignDetector detector;

    private Dogeforia vuforia;

    private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = VuforiaLocalizer.CameraDirection.FRONT;

    private DcMotor motorL, motorR;
    private TankDrive tankDrive;

    @Override
    public void runOpMode() {
        setup();

        while (opModeIsActive()) {
            double perpendicularDistance = distanceFromGold(detector.getBestRectWidth());
            if (detector.isFound() && !Double.isInfinite(perpendicularDistance) && !detector.bestRectIsNull()) {
                double cubeDistance = cubeDistanceFromCenter(detector.getBestRectWidth());

                if (cubeDistance != Double.MAX_VALUE) {
                    double angle = Math.toDegrees(Math.atan(cubeDistance / perpendicularDistance)); // The angle to turn, in degrees. Negative = clockwise, positive = counterclockwise

                    double distanceToTravel = (int)(Math.sqrt(Math.pow(perpendicularDistance, 2) + Math.pow(cubeDistance, 2)) + 2.5); //Use the pythagorean theorem to calculate the length of the hypotenuse. Always rounds up to an integer to ensure that the robot will reach the gold every time

                    if (Math.abs(angle) <= 2)
                        angle = 0; //Practically head on, no point turning

                    int roundedAngle = (angle >= 0) ? (int) (angle + 0.5) : (int) (angle - 0.5); //Round to the nearest integer

                    telemetry.addData("Distance", perpendicularDistance);
                    telemetry.addData("Cube Dist", cubeDistance);
                    telemetry.addData("Angle", angle);
                    telemetry.addData("Rounded Angle", roundedAngle);
                    telemetry.addData("Hypotenuse (Rounded)", distanceToTravel);
                    telemetry.update();

                    if(roundedAngle < 0){
                        tankDrive.rotateClockwise(-roundedAngle, 0.5); //Positive is counterclockwise, passing in a negative turns clockwise, so this works without any conditionals
                        tankDrive.driveForwards(distanceToTravel, 0.5);

                        //Go back to the stating position
                        tankDrive.driveBackwards(distanceToTravel, 0.5);
                        tankDrive.rotateCounterClockwise(-roundedAngle, 0.5);

                    }
                    else {
                        tankDrive.rotateCounterClockwise(roundedAngle, 0.5); //Positive is counterclockwise, passing in a negative turns clockwise, so this works without any conditionals
                        tankDrive.driveForwards(distanceToTravel, 0.5);

                        //Go back to the stating position
                        tankDrive.driveBackwards(distanceToTravel, 0.5);
                        tankDrive.rotateClockwise(roundedAngle, 0.5);
                    }
                    detector.disable();
                 }

                 if(cubeDistance < 2)
                     break;
            }
        }
    }

    private void setup() {
        telemetry.addData("Status", "Gold Detection Test");

        //int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        Dogeforia.Parameters parameters = new Dogeforia.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;

        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;

        parameters.fillCameraMonitorViewParent = true;

        vuforia = new Dogeforia(parameters);

        vuforia.enableConvertFrameToBitmap();

        detector = new ThunderGoldAlignDetector();
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance(), 1, true);
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

        vuforia.setDogeCVDetector(detector);
        vuforia.enableDogeCV();
        //vuforia.showDebug(); Don't enable this since it causes a crash
        vuforia.start();

        motorR = hardwareMap.dcMotor.get("motorR");
        motorL = hardwareMap.dcMotor.get("motorL");
        motorL.setDirection(Direction.REVERSE);

        motorL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        tankDrive = TankDrive.fromMotors(motorL, motorR, this, TICKS_PER_INCH, TICKS_PER_360);

        waitForStart();
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
