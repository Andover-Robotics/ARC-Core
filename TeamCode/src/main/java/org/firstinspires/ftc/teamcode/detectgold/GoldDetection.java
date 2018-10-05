package org.firstinspires.ftc.teamcode.detectgold;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Gold Detection Test", group = "DogeCV")
public class GoldDetection extends OpMode {
    private final double CAM_FOCAL_LENGTH = 1.0, GOLD_WIDTH_IN = 2;
    private ThunderGoldAlignDetector detector;


    @Override
    public void init() {
        telemetry.addData("Status", "Gold Detection Test");

        detector = new ThunderGoldAlignDetector();
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
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
        telemetry.addData("IsAligned", detector.getAligned()); // Is the bot aligned with the gold mineral
        telemetry.addData("X Pos", detector.getXPosition()); // Gold X pos.
        telemetry.addData("Focal Length", calculateFocalLength(detector.getBestRectWidth(), 10));
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        detector.disable();
    }

    private double calculateFocalLength(int goldWithPX, int distanceFromObjIn){
        return goldWithPX * distanceFromObjIn / GOLD_WIDTH_IN;
    }

    private double distanceFromGold(int goldWidthPX){
        return GOLD_WIDTH_IN * CAM_FOCAL_LENGTH / goldWidthPX;
    }
}
