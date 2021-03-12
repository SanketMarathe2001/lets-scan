package com.example.letsscan.TP_Callables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.example.letsscan.Crop.ScanUtils;
import com.example.letsscan.ImageAttrs;
import com.example.letsscan.ThreadPoolManager.CustomThreadPoolManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class PDFImportCallable implements Callable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private Context context;
    private WeakReference<CustomThreadPoolManager> mCustomThreadPoolManagerWeakReference;
    Uri uri;

    public PDFImportCallable(Context context2, Uri uri2) {
        this.context = context2;
        this.uri = uri2;
    }

    public Object call() throws Exception {
        File file;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ParcelFileDescriptor openFileDescriptor = this.context.getContentResolver().openFileDescriptor(this.uri, "r");
        PdfRenderer pdfRenderer = new PdfRenderer(openFileDescriptor);
        int pageCount = pdfRenderer.getPageCount();
        Log.i("MYTAG", "Page Count :" + pageCount);
        int i = this.context.getResources().getDisplayMetrics().densityDpi;
        for (int i2 = 0; i2 < pageCount; i2++) {
            PdfRenderer.Page openPage = pdfRenderer.openPage(i2);
            Log.d("CameraD", String.valueOf(pdfRenderer.shouldScaleForPrinting()));
            Bitmap createBitmap = Bitmap.createBitmap((openPage.getWidth() * i) / 72, (openPage.getHeight() * i) / 72, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.drawColor(-1);
            canvas.drawBitmap(createBitmap, 0.0f, 0.0f, (Paint) null);
            openPage.render(createBitmap, (Rect) null, (Matrix) null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            Context context2 = this.context;
            File externalFilesDir = context2.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.original/");
            Log.d("CameraD", externalFilesDir.getAbsolutePath());
            try {
                file = File.createTempFile("Image", ".jpg", externalFilesDir);
                arrayList.add(file.getName());
            } catch (IOException e2) {
                file = null;
                e2.printStackTrace();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                arrayList2.add(new ImageAttrs(ScanUtils.getOutlinePoints(file.getPath()), 0.0f, "original"));
                fileOutputStream.flush();
                fileOutputStream.close();
                openPage.close();
            }
            FileOutputStream fileOutputStream2 = new FileOutputStream(file);
            createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream2);
            arrayList2.add(new ImageAttrs(ScanUtils.getOutlinePoints(file.getPath()), 0.0f, "original"));
            fileOutputStream2.flush();
            fileOutputStream2.close();
            openPage.close();
        }
        pdfRenderer.close();
        openFileDescriptor.close();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("path_list", arrayList);
        bundle.putParcelableArrayList("image_attrs_list", arrayList2);
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
