package com.example.letsscan.Crop;

import android.graphics.Matrix;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

public class MathUtils {
    public static MatOfPoint toMatOfPointInt(MatOfPoint2f matOfPoint2f) {
        MatOfPoint matOfPoint = new MatOfPoint();
        matOfPoint2f.convertTo(matOfPoint, 4);
        return matOfPoint;
    }

    public static MatOfPoint2f toMatOfPointFloat(MatOfPoint matOfPoint) {
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
        matOfPoint.convertTo(matOfPoint2f, CvType.CV_32FC2);
        return matOfPoint2f;
    }

    public static double angle(Point point, Point point2, Point point3) {
        double d = point.x - point3.x;
        double d2 = point.y - point3.y;
        double d3 = point2.x - point3.x;
        double d4 = point2.y - point3.y;
        return ((d * d3) + (d2 * d4)) / Math.sqrt((((d * d) + (d2 * d2)) * ((d3 * d3) + (d4 * d4))) + 1.0E-10d);
    }

    public static MatOfPoint2f scaleRectangle(MatOfPoint2f matOfPoint2f, double d) {
        List<Point> list = matOfPoint2f.toList();
        ArrayList arrayList = new ArrayList();
        for (Point next : list) {
            arrayList.add(new Point(next.x * d, next.y * d));
        }
        MatOfPoint2f matOfPoint2f2 = new MatOfPoint2f();
        matOfPoint2f2.fromList(arrayList);
        return matOfPoint2f2;
    }

    public static Map<Integer, PointF> applyMatrix(Map<Integer, PointF> map, Matrix matrix) {
        HashMap hashMap = new HashMap();
        int i = 0;
        for (PointF next : map.values()) {
            float[] fArr = {next.x, next.y};
            float[] fArr2 = new float[2];
            matrix.mapPoints(fArr2, fArr);
            hashMap.put(Integer.valueOf(i), new PointF(fArr2[0], fArr2[1]));
            i++;
        }
        return hashMap;
    }

    public static Map<Integer, PointF> applyInvertMatrix(Map<Integer, PointF> map, Matrix matrix) {
        HashMap hashMap = new HashMap();
        int i = 0;
        for (PointF next : map.values()) {
            float[] fArr = {next.x, next.y};
            float[] fArr2 = new float[2];
            matrix.mapPoints(fArr2, fArr);
            hashMap.put(Integer.valueOf(i), new PointF(fArr2[0], fArr2[1]));
            i++;
        }
        return hashMap;
    }
}
