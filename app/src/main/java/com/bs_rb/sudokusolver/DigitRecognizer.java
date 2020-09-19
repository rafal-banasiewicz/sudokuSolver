package com.bs_rb.sudokusolver;


import android.content.Context;
import android.util.Pair;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import org.opencv.core.CvType;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class DigitRecognizer {

    private ArrayList<Pair<Mat, MatOfPoint>> digits;

    public DigitRecognizer() {
        digits = new ArrayList<>();
    }

    public int classify( Mat img) {
        Mat cellImg = preprocessImage(img);
        img.copyTo((cellImg));

        Mat cellImg_float = new Mat();
        Mat digit_float = new Mat();

        cellImg.convertTo(cellImg_float, CvType.CV_32F);

        Mat resized = new Mat();

        MatOfDouble digit_mean = new MatOfDouble();
        MatOfDouble digit_std = new MatOfDouble();
        MatOfDouble cellImg_mean  = new MatOfDouble();
        MatOfDouble cellImg_std = new MatOfDouble();

        Double covar;
        Double correl;

        Integer pixels = cellImg_float.rows() * cellImg_float.cols();

        ArrayList<Pair<Double, Integer>> vecCorrel = new ArrayList<>();
        ArrayList<MatOfPoint> cnts = new ArrayList<>();

        Integer number = 1;
        Mat hierarchy = new Mat();

        Mat sub1 = new Mat();
        Mat sub2 = new Mat();

        Imgproc.findContours(cellImg, cnts, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for(Pair<Mat, MatOfPoint> digit : digits){
            Imgproc.resize(digit.first, resized, cellImg.size());
            resized.convertTo(digit_float, CvType.CV_32F);

            Core.meanStdDev(cellImg_float, cellImg_mean, cellImg_std);
            Core.meanStdDev(digit_float, digit_mean, digit_std);

            Core.subtract(cellImg_float, cellImg_mean, sub1);
            Core.subtract(digit_float, digit_mean, sub2);
            covar = sub1.dot(sub2) / pixels;

            correl = covar / (cellImg_std.toList().get(0) * digit_std.toList().get(0)); //possible wrong

            vecCorrel.add(new Pair<>(correl, number));
            number++;
        }

        Collections.sort(vecCorrel, (o1, o2) -> Double.compare(o1.first, o2.first));
        Collections.reverse(vecCorrel);

        Double c = vecCorrel.get(0).first;
        number = vecCorrel.get(0).second;

        if(c > 0.5f) {
            return number;
        }
        else return 0;
    }

    public boolean loadDigits(Context context) throws IOException {

        Mat img;
        MatOfPoint cnt_empty = new MatOfPoint();

        img = Utils.loadResource(context, R.drawable.one);
        Imgproc.cvtColor(img,img, Imgproc.COLOR_RGBA2GRAY);
        digits.add(new Pair<>(img, cnt_empty));

        img = Utils.loadResource(context, R.drawable.two);
        Imgproc.cvtColor(img,img, Imgproc.COLOR_RGBA2GRAY);
        digits.add(new Pair<>(img, cnt_empty));

        img = Utils.loadResource(context, R.drawable.three);
        Imgproc.cvtColor(img,img, Imgproc.COLOR_RGBA2GRAY);
        digits.add(new Pair<>(img, cnt_empty));

        img = Utils.loadResource(context, R.drawable.four);
        Imgproc.cvtColor(img,img, Imgproc.COLOR_RGBA2GRAY);
        digits.add(new Pair<>(img, cnt_empty));

        img = Utils.loadResource(context, R.drawable.five);
        Imgproc.cvtColor(img,img, Imgproc.COLOR_RGBA2GRAY);
        digits.add(new Pair<>(img, cnt_empty));

        img = Utils.loadResource(context, R.drawable.six);
        Imgproc.cvtColor(img,img, Imgproc.COLOR_RGBA2GRAY);
        digits.add(new Pair<>(img, cnt_empty));

        img = Utils.loadResource(context, R.drawable.seven);
        Imgproc.cvtColor(img,img, Imgproc.COLOR_RGBA2GRAY);
        digits.add(new Pair<>(img, cnt_empty));

        img = Utils.loadResource(context, R.drawable.eight);
        Imgproc.cvtColor(img,img, Imgproc.COLOR_RGBA2GRAY);
        digits.add(new Pair<>(img, cnt_empty));

        img = Utils.loadResource(context, R.drawable.nine);
        Imgproc.cvtColor(img,img, Imgproc.COLOR_RGBA2GRAY);
        digits.add(new Pair<>(img, cnt_empty));

        return true;
    }

    public boolean preapareDigits() {
        Rect rect = new Rect();
        ArrayList<MatOfPoint> vecContour = new ArrayList<>();
        ArrayList<Pair<Mat, MatOfPoint>> prepared = new ArrayList<>();
        Mat hierarchy = new Mat(); //not used
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));

        for (Pair<Mat, MatOfPoint> digit : digits) {
            vecContour.clear();
            Imgproc.GaussianBlur(digit.first, digit.first, new Size(3,3),0);
            Imgproc.threshold(digit.first, digit.first, 200, 255, Imgproc.THRESH_BINARY_INV);
            Imgproc.dilate(digit.first, digit.first, kernel);
            Imgproc.findContours(digit.first, vecContour, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            rect = Imgproc.boundingRect(vecContour.get(0));

            prepared.add(new Pair<>(digit.first.submat(rect), vecContour.get(0)));
        }

        digits = prepared;
        return  true;
    }

    private Mat preprocessImage(Mat img) {
        Rect rect;
        ArrayList<MatOfPoint> vecContour = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(img, vecContour, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        rect = Imgproc.boundingRect(vecContour.get(0));

        Mat retImg = img.submat(rect);
        return retImg;
    }

}

