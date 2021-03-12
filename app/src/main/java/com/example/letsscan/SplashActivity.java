package com.example.letsscan;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.letsscan.Functions.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 500;
    public static List<Integer> preferences = new ArrayList();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        if (new File(getFilesDir(), "preferences.json").exists()) {
            Log.d("MYTAGE", "Pref File Exists");
            preferences = Utils.loadPreferences(this);
        } else {
            Log.d("MYTAGE", "Pref File Don't Exists");
            List<Integer> asList = Arrays.asList(new Integer[]{0, 1, 2, 2});
            preferences = asList;
            Utils.writePrefGSON(asList, this);
        }
        String[] strArr = {"English", "Hindi"};
        updateLanguage(this, strArr[preferences.get(0).intValue()]);
        Log.d("MYTAGE", "Language" + strArr[preferences.get(0).intValue()]);
        setContentView((int) R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                SplashActivity.this.startActivity(new Intent(SplashActivity.this, FileActivity.class));
                SplashActivity.this.finish();
            }
        }, (long) SPLASH_TIME_OUT);
    }

    public void updateLanguage(Context context, String str) {
        if ("Hindi".equals(str)) {
            str = "hi";
        } else if ("English".equals(str)) {
            str = "en";
        }
        Locale locale = new Locale(str);
        if (!getResources().getConfiguration().locale.equals(locale)) {
            Log.d("MYTAGE", "CP-1 language");
            Locale.setDefault(locale);
            Configuration configuration = new Configuration();
            configuration.locale = locale;
            context.getResources().updateConfiguration(configuration, (DisplayMetrics) null);
        }
    }
}

