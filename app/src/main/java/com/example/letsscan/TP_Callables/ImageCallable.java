package com.example.letsscan.TP_Callables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.example.letsscan.Functions.PreProcess;
import com.example.letsscan.ThreadPoolManager.CustomThreadPoolManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;

public class ImageCallable implements Callable {
    ArrayList<String> abs_path_list;
    private Context context;
    String file_name;
    int key;
    private WeakReference<CustomThreadPoolManager> mCustomThreadPoolManagerWeakReference;
    int quality;

    public ImageCallable(ArrayList<String> arrayList, Context context2, String str, int i, int i2) {
        this.context = context2;
        this.abs_path_list = arrayList;
        this.file_name = str;
        this.key = i;
        this.quality = i2;
    }

    public Object call() throws Exception {
        try {
            if (!Thread.interrupted()) {
                Log.d("MYTAG", "ShareImage Callable" + Thread.currentThread().getName());
                int i = this.context.getResources().getDisplayMetrics().widthPixels;
                ArrayList arrayList = new ArrayList();
                Iterator<String> it = this.abs_path_list.iterator();
                while (it.hasNext()) {
                    String next = it.next();
                    Bitmap convert = PreProcess.convert(this.context, BitmapFactory.decodeFile(next), next);
                    int width = convert.getWidth();
                    int height = convert.getHeight();
                    if (width >= i) {
                        height = (height * i) / width;
                        width = i;
                    }
                    Bitmap createScaledBitmap = Bitmap.createScaledBitmap(convert, width, height, true);
                    convert.recycle();
                    if (Build.VERSION.SDK_INT >= 29) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("_display_name", this.file_name + "_Img_" + this.abs_path_list.indexOf(next));
                        contentValues.put("mime_type", "image/jpeg");
                        contentValues.put("relative_path", "Documents/Let's Scan/Images");
                        contentValues.put("is_pending", 1);
                        ContentResolver contentResolver = this.context.getContentResolver();
                        Uri insert = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues);
                        OutputStream openOutputStream = contentResolver.openOutputStream(insert);
                        createScaledBitmap.compress(Bitmap.CompressFormat.JPEG, this.quality, openOutputStream);
                        openOutputStream.flush();
                        openOutputStream.close();
                        contentValues.clear();
                        contentValues.put("is_pending", 0);
                        contentResolver.update(insert, contentValues, (String) null, (String[]) null);
                        arrayList.add(insert);
                    } else {
                        String str = Environment.getExternalStorageDirectory().toString() + File.separator + "Let's Scan" + File.separator + "Images";
                        File file = new File(str);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        File file2 = new File(str + File.separator + this.file_name + "_Img_" + this.abs_path_list.indexOf(next) + ".jpg");
                        FileOutputStream fileOutputStream = new FileOutputStream(file2);
                        createScaledBitmap.compress(Bitmap.CompressFormat.JPEG, this.quality, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        arrayList.add(Uri.fromFile(file2));
                    }
                    createScaledBitmap.recycle();
                }
                Message message = new Message();
                message.what = this.key;
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("uri", arrayList);
                bundle.putString("type", "image");
                message.setData(bundle);
                if (!(this.mCustomThreadPoolManagerWeakReference == null || this.mCustomThreadPoolManagerWeakReference.get() == null)) {
                    ((CustomThreadPoolManager) this.mCustomThreadPoolManagerWeakReference.get()).sendMessageToUiThread(message);
                    Log.d("MYTAG", "Message sent to UI thread");
                }
                return null;
            }
            throw new InterruptedException();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCustomThreadPoolManager(CustomThreadPoolManager customThreadPoolManager) {
        this.mCustomThreadPoolManagerWeakReference = new WeakReference<>(customThreadPoolManager);
    }
}

