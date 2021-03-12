package com.example.letsscan.TP_Callables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.example.letsscan.Functions.PreProcess;
import com.example.letsscan.ThreadPoolManager.CustomThreadPoolManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;

public class PDFCallable implements Callable {
    ArrayList<String> abs_path_list;
    private Context context;
    String file_name;
    int key;
    private WeakReference<CustomThreadPoolManager> mCustomThreadPoolManagerWeakReference;
    int quality;

    public PDFCallable(ArrayList<String> arrayList, Context context2, String str, int i, int i2) {
        this.context = context2;
        this.abs_path_list = arrayList;
        this.file_name = str;
        this.key = i;
        this.quality = i2;
    }

    public Object call() throws Exception {
        try {
            if (!Thread.interrupted()) {
                Log.d("MYTAG", "SharePDF Callable" + Thread.currentThread().getName());
                PdfDocument pdfDocument = new PdfDocument();
                int i = this.context.getResources().getDisplayMetrics().widthPixels;
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
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    createScaledBitmap.compress(Bitmap.CompressFormat.JPEG, this.quality, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.flush();
                    byteArrayOutputStream.close();
                    Log.d("SIZE", String.valueOf(byteArray.length / 1024));
                    Bitmap decodeStream = BitmapFactory.decodeStream(new ByteArrayInputStream(byteArray));
                    Log.d("SIZE", String.valueOf(decodeStream.getByteCount() / 1024));
                    PdfDocument.Page startPage = pdfDocument.startPage(new PdfDocument.PageInfo.Builder(width, height, 1).create());
                    Canvas canvas = startPage.getCanvas();
                    Paint paint = new Paint();
                    paint.setColor(-1);
                    canvas.drawPaint(paint);
                    canvas.drawBitmap(decodeStream, 0.0f, 0.0f, (Paint) null);
                    pdfDocument.finishPage(startPage);
                    decodeStream.recycle();
                }
                Log.d("MYTAG", "CP----2");
                Bundle bundle = new Bundle();
                if (Build.VERSION.SDK_INT >= 29) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_display_name", this.file_name);
                    contentValues.put("mime_type", "application/pdf");
                    contentValues.put("relative_path", "Documents/Let's Scan/PDF");
                    ContentResolver contentResolver = this.context.getContentResolver();
                    Uri insert = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues);
                    OutputStream openOutputStream = contentResolver.openOutputStream(insert);
                    pdfDocument.writeTo(openOutputStream);
                    openOutputStream.flush();
                    openOutputStream.close();
                    contentValues.clear();
                    contentResolver.update(insert, contentValues, (String) null, (String[]) null);
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(insert);
                    bundle.putParcelableArrayList("uri", arrayList);
                } else {
                    String str = Environment.getExternalStorageDirectory().toString() + File.separator + "Let's Scan" + File.separator + "PDF";
                    File file = new File(str);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File file2 = new File(str + File.separator + this.file_name + ".pdf");
                    FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    pdfDocument.writeTo(fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.add(Uri.fromFile(file2));
                    bundle.putParcelableArrayList("uri", arrayList2);
                }
                pdfDocument.close();
                bundle.putString("type", "pdf");
                Message message = new Message();
                message.what = this.key;
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
