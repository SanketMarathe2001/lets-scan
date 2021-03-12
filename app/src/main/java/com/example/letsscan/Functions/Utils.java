package com.example.letsscan.Functions;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.WindowManager;

import com.example.letsscan.recycler_views.FileData;
import com.google.api.services.vision.v1.model.BoundingPoly;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Utils extends Activity {
    public static ArrayList<RectF> Poly2Rect(List<BoundingPoly> list) {
        ArrayList<RectF> arrayList = new ArrayList<>();
        if (list != null) {
            Log.d("MYTAG", "boxarray is not null & size: " + Iterables.size(list));
            for (BoundingPoly next : list) {
                arrayList.add(new RectF((float) (next.getVertices().get(0).getX().intValue() - 4), (float) next.getVertices().get(0).getY().intValue(), (float) (next.getVertices().get(2).getX().intValue() + 4), (float) next.getVertices().get(2).getY().intValue()));
            }
            Log.d("MYTAG", "post-invalidating");
        } else {
            Log.d("MYTAG", "boxarray is null");
        }
        return arrayList;
    }

    public static void delete_image(String str, Context context) {
        new File(str).delete();
    }

    public static void writeGSON(ArrayList<FileData> arrayList, Context context) {
        Gson gson = new Gson();
        try {
            FileWriter fileWriter = new FileWriter(context.getExternalFilesDir("hidden") + "/file.json");
            gson.toJson((Object) arrayList, (Appendable) fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writePrefGSON(List<Integer> list, Context context) {
        Gson gson = new Gson();
        try {
            FileWriter fileWriter = new FileWriter(context.getFilesDir() + "/preferences.json");
            gson.toJson((Object) list, (Appendable) fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> loadPreferences(Context context) {
        try {
            List<Integer> list = (List) new Gson().fromJson((Reader) new BufferedReader(new FileReader(new File(context.getFilesDir(), "preferences.json"))), new TypeToken<List<Integer>>() {
            }.getType());
            Log.d("MYTAGE", "pref_index size : " + list.size());
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MYTAGE", "pref_index size : FAILED");
            return null;
        }
    }

    public static int convertDpToPx(Context context, float f) {
        return (int) (f * context.getResources().getDisplayMetrics().density);
    }

    public static Size getDisplaySize(WindowManager windowManager) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int i = displayMetrics.heightPixels;
        int i2 = displayMetrics.widthPixels;
        Log.i("MYTAG", "Screen Height : " + i);
        Log.i("MYTAG", "Screen Width : " + i2);
        return new Size(i2, i);
    }

    public static Size calculateTvSize(Size size, Rational rational) {
        int width = size.getWidth();
        int width2 = (int) (((double) size.getWidth()) / rational.doubleValue());
        Log.i("MYTAG", "Texture view-> height->" + width2);
        Log.i("MYTAG", "Texture view-> width ->" + width);
        return new Size(width, width2);
    }
}
