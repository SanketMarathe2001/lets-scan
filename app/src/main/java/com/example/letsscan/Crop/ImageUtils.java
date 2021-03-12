package com.example.letsscan.Crop;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class ImageUtils {
    public static Mat bitmapToMat(Bitmap bitmap) {
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), 0, new Scalar(4.0d));
        Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(copy, mat);
        copy.recycle();
        return mat;
    }

    public static Bitmap matToBitmap(Mat mat) {
        Bitmap createBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, createBitmap);
        return createBitmap;
    }

    public static Bitmap getTransformedImage(Bitmap bitmap, final String s, final float n) {
        final int hashCode = s.hashCode();
        int n2 = 0;
        Label_0070: {
            if (hashCode != -1650363957) {
                if (hashCode != 95475) {
                    if (hashCode == 1574013398) {
                        if (s.equals("magiccolor")) {
                            n2 = 1;
                            break Label_0070;
                        }
                    }
                }
                else if (s.equals("b&w")) {
                    n2 = 2;
                    break Label_0070;
                }
            }
            else if (s.equals("greyscale")) {
                n2 = 0;
                break Label_0070;
            }
            n2 = -1;
        }
        if (n2 != 0) {
            if (n2 != 1) {
                if (n2 == 2) {
                    bitmap = ScanPoints.getBWImage(bitmap);
                }
            }
            else {
                bitmap = ScanPoints.getMagicColor(bitmap);
            }
        }
        else {
            bitmap = ScanPoints.getGrayScale(bitmap);
        }
        final Matrix matrix = new Matrix();
        matrix.postRotate(n);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        Log.d("BItmap", String.valueOf(bitmap.getWidth()));
        Log.d("BItmap", String.valueOf(bitmap.getHeight()));
        return bitmap;
    }
}
