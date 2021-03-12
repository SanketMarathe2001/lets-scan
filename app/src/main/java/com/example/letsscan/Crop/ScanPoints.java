package com.example.letsscan.Crop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ScanPoints {
    private static final double AREA_LOWER_THRESHOLD = 0.2d;
    private static final double AREA_UPPER_THRESHOLD = 0.98d;
    private static Comparator<MatOfPoint2f> AreaDescendingComparator = new Comparator<MatOfPoint2f>() {
        public int compare(MatOfPoint2f matOfPoint2f, MatOfPoint2f matOfPoint2f2) {
            return (int) Math.ceil(Imgproc.contourArea(matOfPoint2f2) - Imgproc.contourArea(matOfPoint2f));
        }
    };
    private static final double DOWNSCALE_IMAGE_SIZE = 600.0d;
    private static final int THRESHOLD_LEVEL = 2;

    public static MatOfPoint2f getPoint(String str) {
        Bitmap decodeFile = BitmapFactory.decodeFile(str);
        Mat bitmapToMat = ImageUtils.bitmapToMat(decodeFile);
        decodeFile.recycle();
        double max = DOWNSCALE_IMAGE_SIZE / ((double) Math.max(bitmapToMat.width(), bitmapToMat.height()));
        Size size = new Size(((double) bitmapToMat.width()) * max, ((double) bitmapToMat.height()) * max);
        Mat mat = new Mat(size, bitmapToMat.type());
        Imgproc.resize(bitmapToMat, mat, size);
        List<MatOfPoint2f> points = getPoints(mat);
        if (points.size() == 0) {
            return null;
        }
        Collections.sort(points, AreaDescendingComparator);
        return MathUtils.scaleRectangle(points.get(0), 1.0d / max);
    }

    public static List<MatOfPoint2f> getPoints(Mat mat) {
        int i;
        int i2;
        Mat mat2 = new Mat();
        Imgproc.medianBlur(mat, mat2, 9);
        int i3 = 0;
        Mat mat3 = new Mat(mat2.size(), 0);
        Mat mat4 = new Mat();
        ArrayList<MatOfPoint> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        arrayList3.add(mat2);
        ArrayList arrayList4 = new ArrayList();
        arrayList4.add(mat3);
        int rows = mat.rows() * mat.cols();
        int i4 = 0;
        while (i4 < 3) {
            int[] iArr = new int[2];
            iArr[i3] = i4;
            iArr[1] = i3;
            Core.mixChannels(arrayList3, arrayList4, new MatOfInt(iArr));
            int i5 = 0;
            for (int i6 = 2; i5 < i6; i6 = 2) {
                if (i5 == 0) {
                    i = i5;
                    i2 = 2;
                    Imgproc.Canny(mat3, mat4, 10.0d, 20.0d, 3);
                    Imgproc.dilate(mat4, mat4, new Mat(), new Point(-1.0d, -1.0d));
                } else {
                    i = i5;
                    i2 = 2;
                    Imgproc.threshold(mat3, mat4, (double) (((i + 1) * 255) / 2), 255.0d, 0);
                }
                Imgproc.findContours(mat4, arrayList, new Mat(), 1, i2);
                for (MatOfPoint matOfPointFloat : arrayList) {
                    MatOfPoint2f matOfPointFloat2 = MathUtils.toMatOfPointFloat(matOfPointFloat);
                    MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
                    Imgproc.approxPolyDP(matOfPointFloat2, matOfPoint2f, Imgproc.arcLength(matOfPointFloat2, true) * 0.02d, true);
                    if (isRectangle(matOfPoint2f, rows)) {
                        arrayList2.add(matOfPoint2f);
                    }
                }
                i5 = i + 1;
            }
            i4++;
            i3 = 0;
        }
        return arrayList2;
    }

    private static boolean isRectangle(MatOfPoint2f matOfPoint2f, int i) {
        MatOfPoint matOfPointInt = MathUtils.toMatOfPointInt(matOfPoint2f);
        if (matOfPoint2f.rows() != 4 || Math.abs(Imgproc.contourArea(matOfPoint2f)) <= 1000.0d || !Imgproc.isContourConvex(matOfPointInt)) {
            return false;
        }
        double d = 0.0d;
        Point[] array = matOfPoint2f.toArray();
        for (int i2 = 2; i2 < 5; i2++) {
            d = Math.max(Math.abs(MathUtils.angle(array[i2 % 4], array[i2 - 2], array[i2 - 1])), d);
        }
        if (d < 0.3d) {
            return true;
        }
        return false;
    }

    public static Bitmap getScannedBitmap(Bitmap bitmap, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        PerspectiveTransformation perspectiveTransformation = new PerspectiveTransformation();
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
        matOfPoint2f.fromArray(new Point((double) f, (double) f2), new Point((double) f3, (double) f4), new Point((double) f5, (double) f6), new Point((double) f7, (double) f8));
        return ImageUtils.matToBitmap(perspectiveTransformation.transform(ImageUtils.bitmapToMat(bitmap), matOfPoint2f));
    }

    public static Bitmap getGrayScale(Bitmap bitmap) {
        Mat bitmapToMat = ImageUtils.bitmapToMat(bitmap);
        Imgproc.cvtColor(bitmapToMat, bitmapToMat, 6);
        return ImageUtils.matToBitmap(bitmapToMat);
    }

    public static Bitmap getMagicColor(Bitmap bitmap) {
        Mat bitmapToMat = ImageUtils.bitmapToMat(bitmap);
        Mat mat = new Mat();
        bitmapToMat.convertTo(mat, -1, 1.9d, -80.0d);
        return ImageUtils.matToBitmap(mat);
    }

    public static Bitmap getBWImage(Bitmap bitmap) {
        Mat bitmapToMat = ImageUtils.bitmapToMat(bitmap);
        Imgproc.cvtColor(bitmapToMat, bitmapToMat, 6);
        Imgproc.medianBlur(bitmapToMat, bitmapToMat, 3);
        Imgproc.adaptiveThreshold(bitmapToMat, bitmapToMat, 255.0d, 1, 0, 11, 2.0d);
        return ImageUtils.matToBitmap(bitmapToMat);
    }
}
