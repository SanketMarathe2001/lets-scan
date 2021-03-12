package com.example.letsscan.recycler_views;

import java.util.ArrayList;

public class FileData {
    private String date_added;
    private String iconuri;
    private ArrayList<String> images = new ArrayList<>();
    private String name;
    private String time_added;

    public FileData(String str, String str2, String str3, String str4) {
        this.name = str;
        this.date_added = str2;
        this.time_added = str3;
        this.iconuri = str4;
    }

    public String getName() {
        return this.name;
    }

    public String getDate() {
        return this.date_added;
    }

    public String getTime() {
        return this.time_added;
    }

    public String getIconuri() {
        return this.iconuri;
    }

    public void addImages(ArrayList<String> arrayList) {
        this.images.addAll(arrayList);
    }

    public void removeImages(ArrayList<String> arrayList) {
        this.images.removeAll(arrayList);
    }

    public ArrayList<String> getImages() {
        return this.images;
    }
}
