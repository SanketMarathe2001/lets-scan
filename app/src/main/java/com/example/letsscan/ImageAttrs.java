package com.example.letsscan;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Map;

public class ImageAttrs implements Parcelable {
    public static final Creator<ImageAttrs> CREATOR = new Creator<ImageAttrs>() {
        public ImageAttrs createFromParcel(Parcel parcel) {
            return new ImageAttrs(parcel);
        }

        public ImageAttrs[] newArray(int i) {
            return new ImageAttrs[i];
        }
    };
    public Map<Integer, PointF> cropPoints;
    public String filter;
    public float rot;

    public int describeContents() {
        return 0;
    }

    public ImageAttrs(Map<Integer, PointF> map, float f, String str) {
        this.cropPoints = new HashMap();
        this.rot = 0.0f;
        this.filter = "original";
        this.cropPoints = map;
        this.rot = f;
        this.filter = str;
    }

    public ImageAttrs(Parcel parcel) {
        HashMap hashMap = new HashMap();
        this.cropPoints = hashMap;
        this.rot = 0.0f;
        this.filter = "original";
        parcel.readMap(hashMap, ClassLoader.getSystemClassLoader());
        this.rot = parcel.readFloat();
        this.filter = parcel.readString();
    }

    public Map<Integer, PointF> getCropPoints() {
        return this.cropPoints;
    }

    public float getRot() {
        return this.rot;
    }

    public String getFilter() {
        return this.filter;
    }

    public void setCropPoints(Map<Integer, PointF> map) {
        this.cropPoints = map;
    }

    public void setRot(float f) {
        this.rot = f;
    }

    public void setFilter(String str) {
        this.filter = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeMap(this.cropPoints);
        parcel.writeFloat(this.rot);
        parcel.writeString(this.filter);
    }
}
