package com.example.letsscan.Crop;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;
import android.util.Size;

import com.example.letsscan.Functions.PreProcess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

public class ScanUtils {
    public static Map<Integer, PointF> getEdgePoints(String str) {
        return orderedValidEdgePoints(str, getContourEdgePoints(str));
    }

    private static List<PointF> getContourEdgePoints(String str) {
        MatOfPoint2f point = ScanPoints.getPoint(str);
        if (point == null) {
            point = new MatOfPoint2f();
        }
        List asList = Arrays.asList(point.toArray());
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < asList.size(); i++) {
            arrayList.add(new PointF((float) ((Point) asList.get(i)).x, (float) ((Point) asList.get(i)).y));
        }
        return arrayList;
    }

    public static Map<Integer, PointF> getOutlinePoints(String str) {
        Size bitmapSizeCalc = PreProcess.bitmapSizeCalc(str);
        int width = bitmapSizeCalc.getWidth();
        int height = bitmapSizeCalc.getHeight();
        HashMap hashMap = new HashMap();
        hashMap.put(0, new PointF(0.0f, 0.0f));
        float f = (float) width;
        hashMap.put(1, new PointF(f, 0.0f));
        float f2 = (float) height;
        hashMap.put(2, new PointF(0.0f, f2));
        hashMap.put(3, new PointF(f, f2));
        return hashMap;
    }

    private static Map<Integer, PointF> orderedValidEdgePoints(String str, List<PointF> list) {
        Map<Integer, PointF> orderedPoints = getOrderedPoints(list);
        return !isValidShape(orderedPoints) ? getOutlinePoints(str) : orderedPoints;
    }

    public static boolean isValidShape(Map<Integer, PointF> map) {
        return map.size() == 4;
    }

    public static Map<Integer, PointF> getOrderedPoints(List<PointF> list) {
        PointF pointF = new PointF();
        int size = list.size();
        for (PointF next : list) {
            float f = (float) size;
            pointF.x += next.x / f;
            pointF.y += next.y / f;
        }
        HashMap hashMap = new HashMap();
        for (PointF next2 : list) {
            int i = -1;
            if (next2.x < pointF.x && next2.y < pointF.y) {
                i = 0;
            } else if (next2.x > pointF.x && next2.y < pointF.y) {
                i = 1;
            } else if (next2.x < pointF.x && next2.y > pointF.y) {
                i = 2;
            } else if (next2.x > pointF.x && next2.y > pointF.y) {
                i = 3;
            }
            hashMap.put(Integer.valueOf(i), next2);
        }
        return hashMap;
    }

    public static Bitmap getCroppedImage(Bitmap bitmap, Map<Integer, PointF> map) {
        try {
            float f = ((PointF) Objects.requireNonNull(map.get(3))).x * 1.0f;
            float f2 = ((PointF) Objects.requireNonNull(map.get(1))).y * 1.0f;
            return ScanPoints.getScannedBitmap(bitmap, ((PointF) Objects.requireNonNull(map.get(0))).x * 1.0f, ((PointF) Objects.requireNonNull(map.get(0))).y * 1.0f, ((PointF) Objects.requireNonNull(map.get(1))).x * 1.0f, f2, ((PointF) Objects.requireNonNull(map.get(2))).x * 1.0f, ((PointF) Objects.requireNonNull(map.get(2))).y * 1.0f, f, ((PointF) Objects.requireNonNull(map.get(3))).y * 1.0f);
        } catch (Exception unused) {
            Log.d("TAG", "Error in Cropping");
            return null;
        }
    }
}
