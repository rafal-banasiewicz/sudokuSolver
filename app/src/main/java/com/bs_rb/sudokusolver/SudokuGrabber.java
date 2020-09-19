package com.bs_rb.sudokusolver;

import android.graphics.Bitmap;
import android.telephony.CarrierConfigManager;
import android.util.Pair;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.lang.Math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class SudokuGrabber {

    SudokuGrabber() {
    }

    Mat cell = new Mat();
    Mat digit = new Mat();
    Mat puzzleImage = new Mat();
    Mat warped = new Mat();
    Mat warped_rgb = new Mat();

    public Mat grab(Mat image) {


        Pair<Mat, Mat> pair = find_puzzle(image);
        puzzleImage = pair.first;
        warped = pair.second;

        return puzzleImage;

    }

    public ArrayList<Integer> read_digits(DigitRecognizer dr) {
        Imgproc.resize(puzzleImage, puzzleImage, new Size(1000, 1000));
        Imgproc.resize(warped, warped, new Size(1000, 1000));

        Imgproc.cvtColor(warped, warped_rgb, Imgproc.COLOR_GRAY2BGR);

        ArrayList<Integer> numbers = new ArrayList<>(9*9);
        for(int i = 0; i < 9*9; i++)
            numbers.add(0);



        ArrayList<Rect> rect_cells;
        rect_cells = extract_cells(warped);

        Collections.sort(rect_cells, (r1, r2) -> {

            int c = Double.compare(r1.tl().x, r2.tl().x);
            if (c == 0)
                c = Double.compare(r1.tl().y, r1.tl().y);
            return c;
        });


        if (rect_cells.size() == 82) {
            for (int i = 1; i < rect_cells.size(); i++) {
                cell = warped.submat(rect_cells.get(i));
                digit = extract_digit(cell);

                double stepX = warped.size().width / 10;
                double stepY = warped.size().height / 10;

                int x = (int)(rect_cells.get(i).x / stepX);
                int y = (int)(rect_cells.get(i).y / stepY);
                int idx = y*9 + x;

                if (digit.size() != new Size(0, 0) && digit.size().height >= 20 && digit.size().width >= 20) {
                    int number = dr.classify(digit);

                    numbers.set(idx,number);
                }
                //else numbers.set(idx, 0);
            }
        } else {
            double stepX = warped.size().height / 9;
            double stepY = warped.size().width / 9;

            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    double startX = x * stepX;
                    double startY = y * stepY;

                    double endX = (x + 1) * stepX;
                    double endY = (y + 1) * stepY;

                    Rect rect = new Rect(new Point(startX, startY), new Point(endX, endY));

                    cell = warped.submat(rect);
                    digit = extract_digit(cell);

                    if (digit.size() != new Size(0, 0)) {
                        int number = dr.classify(digit);
                        numbers.add(number);
                    }

                }
            }

        }

        return numbers;

    }

    public ArrayList<Rect> extract_cells(Mat src) {
        Mat sudoku = src.clone();
        Mat grid = new Mat(sudoku.size(), CvType.CV_8UC1);

        Imgproc.GaussianBlur(sudoku, sudoku, new Size(11, 11), 0);
        Imgproc.adaptiveThreshold(sudoku, grid, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 5, 2);
        Core.bitwise_not(grid, grid);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3, 3));
        Imgproc.dilate(grid, grid, kernel);

        Integer count = 0;
        Integer max = -1;

        Point maxPt = new Point();

        for (int y = 0; y < grid.size().height; y++) {
            for (int x = 0; x < grid.size().width; x++) {
                double[] px = grid.get(y, x);
                if (px[0] >= 128) {
                    int area = Imgproc.floodFill(grid, new Mat(), new Point(x, y), new Scalar(64, 0, 0));

                    if (area > max) {
                        maxPt = new Point(x, y);
                        max = area;
                    }
                }
            }
        }

        Imgproc.floodFill(grid, new Mat(), maxPt, new Scalar(255, 255, 255));


        for (int y = 0; y < grid.size().height; y++) {
            for (int x = 0; x < grid.size().width; x++) {
                double[] px = grid.get(y, x);
                if (px[0] == 64 && x != maxPt.x && y != maxPt.y) {
                    int area = Imgproc.floodFill(grid, new Mat(), new Point(x, y), new Scalar(0, 0, 0));
                }
            }
        }

        Imgproc.erode(grid, grid, kernel);
        ////////////////////////////

        Mat dst = new Mat();
        Imgproc.Canny(grid, dst, 50, 200, 3, false);

        Mat kernel_d = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(25, 25));
        Imgproc.dilate(dst, dst, kernel_d);
        Imgproc.erode(dst, dst, kernel_d);

        ArrayList<MatOfPoint> cnts = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dst, cnts, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        Collections.sort(cnts, (c1, c2) -> {
            if (Imgproc.contourArea(c1) > Imgproc.contourArea(c2)) return 1;
            else return 0;
        });
        Collections.reverse(cnts);

        Rect rect = new Rect();
        ArrayList<Rect> ret = new ArrayList<>();

        for (MatOfPoint c : cnts) {
            rect = Imgproc.boundingRect(c);
            ret.add(rect);
        }

        return ret;
    }

    public ArrayList<Point> order_points(List<Point> pts) {
        Point tl = new Point();
        Point tr = new Point();
        Point bl = new Point();
        Point br = new Point();

        ArrayList<Point> ptsTemp = new ArrayList<Point>(pts);

        Collections.sort(ptsTemp, (o1, o2) -> Double.compare(o1.x, o2.x));
        Collections.reverse(ptsTemp);

        ArrayList<Point> leftMost = new ArrayList<>();
        leftMost.add(ptsTemp.get(0));
        leftMost.add(ptsTemp.get(1));

        ArrayList<Point> rightMost = new ArrayList<>();
        rightMost.add(ptsTemp.get(2));
        rightMost.add(ptsTemp.get(3));

        Collections.sort(leftMost, (o1, o2) -> Double.compare(o1.y, o2.y));
        Collections.reverse(leftMost);

        tl = leftMost.get(0);
        bl = leftMost.get(1);

        Point finalTl = tl;

        Collections.sort(rightMost, (o1, o2) -> Double.compare(dist(o1, finalTl), dist(o2, finalTl)));
        //Collections.reverse(rightMost);

        tr = rightMost.get(0);
        br = rightMost.get(1);

        ptsTemp = new ArrayList<>();
        ptsTemp.add(tl);
        ptsTemp.add(tr);
        ptsTemp.add(br);
        ptsTemp.add(bl);

        return ptsTemp;
    }

    private double dist(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public Mat four_point_transform(Mat image, List<Point> pts) {

        ArrayList<Point> vecRect = order_points(pts);

        Point tl, tr, br, bl;
        tl = vecRect.get(0);
        tr = vecRect.get(1);
        br = vecRect.get(2);
        bl = vecRect.get(3);

        double widthA = Math.sqrt(Math.pow(br.x - bl.x, 2) + Math.pow(br.y - bl.y, 2));
        double widthB = Math.sqrt(Math.pow(tr.x - tl.x, 2) + Math.pow(tr.y - tl.y, 2));
        int maxWidth = Math.max((int) widthA, (int) widthB);

        double heightA = Math.sqrt(Math.pow(tr.x - br.x, 2) + Math.pow(tr.y - br.y, 2));
        double heightB = Math.sqrt(Math.pow(tl.x - bl.x, 2) + Math.pow(tl.y - bl.y, 2));
        int maxHeight = Math.max((int) heightA, (int) heightB);

        Point[] points = new Point[4];
        points[0] = new Point(maxWidth, 0);
        points[1] = new Point(0, 0);
        points[2] = new Point(0, maxHeight);
        points[3] = new Point(maxWidth, maxHeight);

        MatOfPoint2f dst = new MatOfPoint2f();
        dst.fromArray(points);

        for (int i = 0; i < 4; i++) {
            points[i] = new Point(pts.get(i).x, pts.get(i).y);
        }
        MatOfPoint2f rect = new MatOfPoint2f(points);

        Mat M = Imgproc.getPerspectiveTransform(rect, dst);
        Mat warped = new Mat();

        Imgproc.warpPerspective(image, warped, M, new Size(maxWidth, maxHeight));

        return warped;
    }

    public Mat extract_digit(Mat cell) {
        Mat thresh = new Mat();
        Imgproc.threshold(cell, thresh, 128, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        ArrayList<MatOfPoint> cnts = new ArrayList<>();
        Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        if (cnts.size() == 0)
            return Mat.zeros(new Size(0, 0), CvType.CV_8UC1);

        MatOfInt hull = new MatOfInt();
        ArrayList<Pair<MatOfInt, MatOfPoint>> hull_contour = new ArrayList<>();

        for (MatOfPoint c : cnts) {
            Imgproc.convexHull(c, hull);
            hull_contour.add(new Pair<>(hull, c));
        }

        Collections.sort(hull_contour, (o1, o2) -> Double.compare(Imgproc.contourArea(o1.second), Imgproc.contourArea(o2.second)));
        //Collections.sort(hull_contour, (o1, o2) -> Integer.compare(o1.first.toList().size(), o2.first.toList().size()));
        Collections.reverse(hull_contour);


        MatOfPoint cnt = hull_contour.get(0).second;
        Rect border = Imgproc.boundingRect(cnt);
        Mat digit = thresh.submat(border);

        return digit;
    }

    public Pair<Mat, Mat> find_puzzle(Mat image) {


        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(7, 7), 3);

        Mat thresh = new Mat();
        Imgproc.adaptiveThreshold(blurred, thresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
        Core.bitwise_not(thresh, thresh);

        Mat hierarchy = new Mat();
        ArrayList<MatOfPoint> cnts = new ArrayList<>();
        Imgproc.findContours(thresh, cnts, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Collections.sort(cnts, (o1, o2) -> Double.compare(Imgproc.contourArea(o1), Imgproc.contourArea(o2)));

        //cut largest contour
        Rect contourRect = Imgproc.boundingRect(cnts.get(cnts.size() - 1));
        Mat temp = new Mat();
        temp = thresh.submat(contourRect);
        cnts.clear();
        Imgproc.findContours(thresh, cnts, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Collections.sort(cnts, (o1, o2) -> Double.compare(Imgproc.contourArea(o1), Imgproc.contourArea(o2)));
        Collections.reverse(cnts);

        MatOfPoint2f approx = new MatOfPoint2f();
        MatOfPoint2f puzzleCnt = new MatOfPoint2f();

        MatOfPoint2f c = new MatOfPoint2f();

        Mat sizee = Mat.zeros(new Size(1, 4), CvType.CV_32FC2);

        for (MatOfPoint cnt : cnts) {
            cnt.convertTo(c, CvType.CV_32F);
            double peri = Imgproc.arcLength(c, true);
            Imgproc.approxPolyDP(c, approx, 0.02 * peri, true);

            if (approx.size().height == sizee.size().height)
            {
                puzzleCnt = approx;
                break;
            }
        }

        Mat puzzle = four_point_transform(image, puzzleCnt.toList());
        Mat warped = four_point_transform(gray, puzzleCnt.toList());

        return new Pair<>(puzzle, warped);
    }
}




