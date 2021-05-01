package com.example.letsscan;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letsscan.Functions.Utils;

import java.util.List;

public class SettingActivity extends AppCompatActivity {

    List<Integer> pref_index = SplashActivity.preferences;
    LinearLayout quality;
    TextView quality_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final String[] strArr2 = {getString(R.string.quality_hi_value), getString(R.string.quality_label_sd), getString(R.string.quality_label_lo)};
        this.quality_tv = (TextView) findViewById(R.id.quality_tv);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_setting2_act);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.settings));
        toolbar.setTitleTextColor(-1);
        this.quality = (LinearLayout) findViewById(R.id.quality_ll);
        this.quality.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingActivity settingActivity = SettingActivity.this;
                settingActivity.create_dialogue(1, strArr2, settingActivity.getString(R.string.select_quality), SplashActivity.preferences.get(1).intValue());
            }
        });
    }

    public void create_dialogue(final int i, final String[] strArr, String str, int i2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) str);
        builder.setSingleChoiceItems((CharSequence[]) strArr, i2, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SettingActivity.this.setDialogAction(i, i, strArr);
            }
        });
        builder.setPositiveButton((CharSequence) getString(R.string.enter), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Utils.writePrefGSON(SettingActivity.this.pref_index, SettingActivity.this);
                SplashActivity.preferences = SettingActivity.this.pref_index;
                if (i == 0) {
                    Toast.makeText(SettingActivity.this.getBaseContext(), "Please restart the app", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton((CharSequence) getString(R.string.cancel), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SettingActivity.this.cancelDialogAction(i, strArr);
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void cancelDialogAction(int i, String[] strArr) {
        if (i == 1) {
            this.quality_tv.setText(strArr[SplashActivity.preferences.get(i).intValue()]);
            this.pref_index.set(i, SplashActivity.preferences.get(i));
        }
    }

    public void setDialogAction(int i, int i2, String[] strArr) {
        if (i == 1) {
            this.quality_tv.setText(strArr[i2]);
            this.pref_index.set(i, Integer.valueOf(i2));
            if (i2 == 0) {
                Toast.makeText(getBaseContext(), R.string.hq_low_end, Toast.LENGTH_LONG).show();
            }
        }
    }
}
