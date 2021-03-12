package com.example.letsscan.ThreadPoolManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import androidx.core.app.NotificationCompat;

import com.example.letsscan.Crop.ImageUtils;
import com.example.letsscan.Crop.MathUtils;
import com.example.letsscan.Crop.ScanUtils;
import com.example.letsscan.Functions.ImageUtil;
import com.example.letsscan.Functions.PreProcess;
import com.example.letsscan.ImageAttrs;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

public class CropSaveCallable implements Callable {
    private Context context;
    private String find_path;
    private ImageAttrs imageattr;
    private WeakReference<CustomThreadPoolManager> mCustomThreadPoolManagerWeakReference;
    private String path;
    private String save_path;
    private Size tvSize;

    public CropSaveCallable(String str, String str2, String str3, Context context2, ImageAttrs imageAttrs, Size size) {
        this.context = context2;
        this.find_path = str;
        this.save_path = str3;
        this.path = str2;
        this.imageattr = imageAttrs;
        this.tvSize = size;
    }

    public Object call() throws Exception {
        if (!Thread.interrupted()) {
            Log.d("MYTAG", Thread.currentThread().getName());
            File file = new File(this.find_path, this.path);
            Bitmap decodeSampledBitmap = ImageUtil.decodeSampledBitmap(file.getPath(), this.tvSize.getWidth(), this.tvSize.getHeight());
            Log.d("BITMAPSize", "Sampled image width:" + decodeSampledBitmap.getWidth() + "  Height:" + decodeSampledBitmap.getHeight());
            int convertX = PreProcess.convertX(this.context, file.getPath());
            Size bitmapSizeCalc = PreProcess.bitmapSizeCalc(file.getPath());
            Matrix matrix = new Matrix();
            matrix.postScale(((float) decodeSampledBitmap.getWidth()) / ((float) bitmapSizeCalc.getWidth()), ((float) decodeSampledBitmap.getHeight()) / ((float) bitmapSizeCalc.getHeight()));
            Bitmap transformedImage = ImageUtils.getTransformedImage(ScanUtils.getCroppedImage(decodeSampledBitmap, MathUtils.applyMatrix(this.imageattr.getCropPoints(), matrix)), this.imageattr.getFilter(), this.imageattr.getRot() + ((float) convertX));
            decodeSampledBitmap.recycle();
            File file2 = new File(this.save_path);
            if (!file2.exists()) {
                file2.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(new File(file2, this.path));
            transformedImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            transformedImage.recycle();
            Bundle bundle = new Bundle();
            bundle.putString(NotificationCompat.CATEGORY_STATUS, "Success");
            Message message = new Message();
            message.setData(bundle);
            WeakReference<CustomThreadPoolManager> weakReference = this.mCustomThreadPoolManagerWeakReference;
            if (weakReference == null || weakReference.get() == null) {
                return null;
            }
            ((CustomThreadPoolManager) this.mCustomThreadPoolManagerWeakReference.get()).sendMessageToUiThread(message);
            return null;
        }
        throw new InterruptedException();
    }

    public void setCustomThreadPoolManager(CustomThreadPoolManager customThreadPoolManager) {
        this.mCustomThreadPoolManagerWeakReference = new WeakReference<>(customThreadPoolManager);
    }
}
