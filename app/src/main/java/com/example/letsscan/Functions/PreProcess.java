package com.example.letsscan.Functions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import android.util.Size;
import androidx.exifinterface.media.ExifInterface;
import java.io.InputStream;
import java.util.Objects;

public class PreProcess {
    private static Bitmap Obitmap;

    public static Bitmap convert(Context context, Bitmap bitmap, String str) {
        try {
            int attributeInt = new ExifInterface(str).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (attributeInt == 6) {
                matrix.postRotate(90.0f);
            } else if (attributeInt == 3) {
                matrix.postRotate(180.0f);
            } else if (attributeInt == 8) {
                matrix.postRotate(270.0f);
            }
            Obitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MYTAG", "Error in Preprocess->Convert");
        }
        return Obitmap;
    }

    public static Bitmap convert(Context context, Bitmap bitmap, Uri uri) {
        try {
            InputStream openInputStream = context.getContentResolver().openInputStream(uri);
            ExifInterface exifInterface = new ExifInterface((InputStream) Objects.requireNonNull(openInputStream));
            openInputStream.close();
            int attributeInt = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (attributeInt == 6) {
                matrix.postRotate(90.0f);
            } else if (attributeInt == 3) {
                matrix.postRotate(180.0f);
            } else if (attributeInt == 8) {
                matrix.postRotate(270.0f);
            }
            Obitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MYTAG", "Error in Preprocess->Convert");
        }
        return Obitmap;
    }

    public static int convertX(Context context, String str) {
        try {
            int attributeInt = new ExifInterface(str).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            new Matrix();
            if (attributeInt == 6) {
                return 90;
            }
            if (attributeInt == 3) {
                return 180;
            }
            if (attributeInt == 8) {
                return 270;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MYTAG", "Error in Preprocess->ConvertX");
            return 0;
        }
    }

    public static Size bitmapSizeCalc(String str) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inScaled = false;
        BitmapFactory.decodeFile(str, options);
        return new Size(options.outWidth, options.outHeight);
    }
}
