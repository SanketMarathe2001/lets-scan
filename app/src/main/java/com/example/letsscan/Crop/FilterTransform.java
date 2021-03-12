package com.example.letsscan.Crop;

import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;

public class FilterTransform extends BitmapTransformation {
    private static final String ID = "com.bumptech.glide.transformations.FilterTransform";
    private static final byte[] ID_BYTES = ID.getBytes(Charset.forName("UTF-8"));
    private String filter;
    private float rot;

    public int hashCode() {
        return -441350798;
    }

    public FilterTransform(String str, float f) {
        this.filter = str;
        this.rot = f;
    }

    public Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int i, int i2) {
        Log.d("TYPE", this.filter);
        return ImageUtils.getTransformedImage(bitmap, this.filter, this.rot);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FilterTransform)) {
            return false;
        }
        FilterTransform filterTransform = (FilterTransform) obj;
        if (!this.filter.equals(filterTransform.filter) || this.rot != filterTransform.rot) {
            return false;
        }
        return true;
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        messageDigest.update(this.filter.getBytes());
        messageDigest.update(ByteBuffer.allocate(4).putInt((int) this.rot).array());
    }
}
