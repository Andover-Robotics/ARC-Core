package org.firstinspires.ftc.teamcode.detectgold;

import com.disnodeteam.dogecv.detectors.roverruckus.GoldAlignDetector;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ThunderGoldAlignDetector extends GoldAlignDetector {

    private Mat workingMat = new Mat();
    private Mat maskYellow = new Mat();
    private Mat hierarchy = new Mat();

    private boolean found = false;
    private double goldXPos = 0;

    private Rect bestRect;

    public ThunderGoldAlignDetector() {
        super();
        detectorName = "Thunder's Home-made All-natural Gold Align Detector";
    }

    @Override
    public Mat process(Mat input) {
        bestRect = null;
        if (input.channels() < 0 || input.cols() <= 0) {
            Log.e("DogeCV", "Bad INPUT MAT!");

        }
        input.copyTo(workingMat);
        input.release();

        Imgproc.GaussianBlur(workingMat, workingMat, new Size(5, 5), 0);
        yellowFilter.process(workingMat.clone(), maskYellow);

        List<MatOfPoint> contoursYellow = new ArrayList<>();

        Imgproc.findContours(maskYellow, contoursYellow, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(workingMat, contoursYellow, -1, new Scalar(230, 70, 70), 2);


        double bestDiffrence = Double.MAX_VALUE;

        for (MatOfPoint cont : contoursYellow) {
            double score = calculateScore(cont);


            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(cont);
            Imgproc.rectangle(workingMat, rect.tl(), rect.br(), new Scalar(0, 0, 255), 2);

            if (score < bestDiffrence) {
                bestDiffrence = score;
                bestRect = rect;
            }
        }


        double alignX = (getAdjustedSize().width / 2) + alignPosOffset;
        double alignXMin = alignX - (alignSize / 2);
        double alignXMax = alignX + (alignSize / 2);
        double xPos = 0;


        boolean aligned = false;
        if (bestRect != null) {
            Imgproc.rectangle(workingMat, bestRect.tl(), bestRect.br(), new Scalar(255, 0, 0), 4);
            Imgproc.putText(workingMat, "Chosen", bestRect.tl(), 0, 1, new Scalar(255, 255, 255));

            xPos = bestRect.x + (bestRect.width / 2);
            goldXPos = xPos;
            Imgproc.circle(workingMat, new Point(xPos, bestRect.y + (bestRect.height / 2)), 5, new Scalar(0, 255, 0), 2);
            if (xPos < alignXMax && xPos > alignXMin) {
                aligned = true;
            } else {
                aligned = false;
            }
            Imgproc.line(workingMat, new Point(xPos, getAdjustedSize().height), new Point(xPos, getAdjustedSize().height - 30), new Scalar(255, 255, 0), 2);
            found = true;
        } else {
            found = false;
            aligned = false;
        }
        if (debugAlignment) {

            Imgproc.line(workingMat, new Point(alignXMin, getAdjustedSize().height), new Point(alignXMin, getAdjustedSize().height - 40), new Scalar(0, 255, 0), 2);
            Imgproc.line(workingMat, new Point(alignXMax, getAdjustedSize().height), new Point(alignXMax, getAdjustedSize().height - 40), new Scalar(0, 255, 0), 2);
        }


        Imgproc.putText(workingMat, "Result: " + aligned, new Point(10, getAdjustedSize().height - 30), 0, 1, new Scalar(255, 255, 0), 1);


        return workingMat;
    }

    public int getBestRectWidth() {
        return (bestRect == null) ? 0 : bestRect.width;
    }

    public int getYPosition() {
        return (bestRect == null) ? 0 : bestRect.y;
    }

    @Override
    public boolean isFound() {
        return found;
    }

    public double getGoldXPos() {
        return goldXPos;
    }
}
