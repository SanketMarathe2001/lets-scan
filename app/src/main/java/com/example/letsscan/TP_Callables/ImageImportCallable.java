package com.example.letsscan.TP_Callables;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.example.letsscan.Crop.ScanUtils;
import com.example.letsscan.Functions.PreProcess;
import com.example.letsscan.ImageAttrs;
import com.example.letsscan.ThreadPoolManager.CustomThreadPoolManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

public class ImageImportCallable implements Callable {
    private Context context;
    ArrayList<ImageAttrs> imageAttrs = new ArrayList<>();
    private WeakReference<CustomThreadPoolManager> mCustomThreadPoolManagerWeakReference;
    ArrayList<String> pathlist = new ArrayList<>();
    Intent resultData;

    public ImageImportCallable(Context context2, Intent intent) {
        this.context = context2;
        this.resultData = intent;
    }

    private void saveBitmaps(ArrayList<Bitmap> arrayList) {
        File file;
        FileOutputStream fileOutputStream = null;
        IOException e;
        this.pathlist = new ArrayList<>();
        File externalFilesDir = this.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.original/");
        Log.d("MYTAG", externalFilesDir.getAbsolutePath());
        Iterator<Bitmap> it = arrayList.iterator();
        while (it.hasNext()) {
            Bitmap next = it.next();
            FileOutputStream fileOutputStream2 = null;
            try {
                file = File.createTempFile("Image", ".jpg", externalFilesDir);
                try {
                    this.pathlist.add(file.getName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (IOException e2) {
                e = e2;
                file = null;
                e2.printStackTrace();
                try {
                    fileOutputStream = new FileOutputStream(file);
                } catch (FileNotFoundException e4) {
                    e=e4;
                    e.printStackTrace();
                }
                next.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                Map<Integer, PointF> edgePoints = ScanUtils.getEdgePoints(file.getPath());
                this.imageAttrs.add(new ImageAttrs(edgePoints, 0.0f, "original"));
                Log.d("CROPIMG", String.valueOf(edgePoints));
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                catch (IOException e3) {
                    e=e3;
                    e.printStackTrace();
                }

            }
            try {
                fileOutputStream = new FileOutputStream(file);
                try {
                    next.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    Map<Integer, PointF> edgePoints2 = ScanUtils.getEdgePoints(file.getPath());
                    this.imageAttrs.add(new ImageAttrs(edgePoints2, 0.0f, "original"));
                    Log.d("CROPIMG", String.valueOf(edgePoints2));
                } catch (Exception e3) {
                    e = (IOException) e3;
                    fileOutputStream2 = fileOutputStream;
                }
            } catch (FileNotFoundException e4) {
                e = e4;
                e.printStackTrace();
                fileOutputStream = fileOutputStream2;
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e5) {
                    e=e5;
                    e.printStackTrace();
                }
            }
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e5) {
                e5.printStackTrace();
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor openFileDescriptor = this.context.getContentResolver().openFileDescriptor(uri, "r");
        Bitmap decodeFileDescriptor = BitmapFactory.decodeFileDescriptor(openFileDescriptor.getFileDescriptor());
        openFileDescriptor.close();
        return decodeFileDescriptor;
    }

    public Object call() throws Exception {
        ClipData clipData = this.resultData.getClipData();
        ArrayList arrayList = new ArrayList();
        if (clipData != null) {
            for (int i = 0; i < clipData.getItemCount(); i++) {
                Uri uri = clipData.getItemAt(i).getUri();
                try {
                    arrayList.add(PreProcess.convert(this.context, getBitmapFromUri(uri), uri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (arrayList.size() > 0) {
                saveBitmaps(arrayList);
            }
        } else {
            Uri data = this.resultData.getData();
            try {
                arrayList.add(PreProcess.convert(this.context, getBitmapFromUri(data), data));
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            if (arrayList.size() > 0) {
                saveBitmaps(arrayList);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("path_list", this.pathlist);
        bundle.putParcelableArrayList("image_attrs_list", this.imageAttrs);
        Message message = new Message();
        message.setData(bundle);
        WeakReference<CustomThreadPoolManager> weakReference = this.mCustomThreadPoolManagerWeakReference;
        if (weakReference == null || weakReference.get() == null) {
            return null;
        }
        ((CustomThreadPoolManager) this.mCustomThreadPoolManagerWeakReference.get()).sendMessageToUiThread(message);
        Log.d("MYTAG", "Message sent to UI thread");
        return null;
    }

    public void setCustomThreadPoolManager(CustomThreadPoolManager customThreadPoolManager) {
        this.mCustomThreadPoolManagerWeakReference = new WeakReference<>(customThreadPoolManager);
    }
}
