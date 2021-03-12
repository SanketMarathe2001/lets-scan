package com.example.letsscan.Dictionary;

import android.graphics.RectF;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class VisionAPIData implements Parcelable {
    public static final Creator<VisionAPIData> CREATOR = new Creator<VisionAPIData>() {
        public VisionAPIData createFromParcel(Parcel parcel) {
            return new VisionAPIData(parcel);
        }

        public VisionAPIData[] newArray(int i) {
            return new VisionAPIData[i];
        }
    };
    int ID;
    String completePage;
    public boolean display;
    String path;
    ArrayList<RectF> rectangles;
    boolean status;
    ArrayList<String> words;

    public int describeContents() {
        return 0;
    }

    public VisionAPIData(int i, String str, ArrayList<String> arrayList, ArrayList<RectF> arrayList2, String str2, boolean z, boolean z2) {
        this.status = false;
        this.display = false;
        this.ID = i;
        this.path = str;
        this.words = arrayList;
        this.rectangles = arrayList2;
        this.completePage = str2;
        this.status = z;
        this.display = z2;
    }

    private VisionAPIData(Parcel parcel) {
        this.status = false;
        this.display = false;
        this.ID = parcel.readInt();
        this.path = parcel.readString();
        if (parcel.readByte() == 1) {
            ArrayList<String> arrayList = new ArrayList<>();
            this.words = arrayList;
            parcel.readList(arrayList, String.class.getClassLoader());
        } else {
            this.words = null;
        }
        if (parcel.readByte() == 1) {
            ArrayList<RectF> arrayList2 = new ArrayList<>();
            this.rectangles = arrayList2;
            parcel.readList(arrayList2, RectF.class.getClassLoader());
        } else {
            this.rectangles = null;
        }
        this.completePage = parcel.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.status = parcel.readBoolean();
            this.display = parcel.readBoolean();
        }

    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.ID);
        parcel.writeString(this.path);
        if (this.words == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeList(this.words);
        }
        if (this.rectangles == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeList(this.rectangles);
        }
        parcel.writeString(this.completePage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(this.status);
            parcel.writeBoolean(this.display);
        }
    }
}
