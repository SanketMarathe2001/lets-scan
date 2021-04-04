package com.example.letsscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QrGeneratorActivity extends AppCompatActivity {

    EditText code;
    ImageView qr_code;
    Button btn,share,save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generator);
        Toolbar toolbar = findViewById(R.id.qr_gen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("QR GENERATOR");
        toolbar.setTitleTextColor(Color.WHITE);
        code = findViewById(R.id.code);
        qr_code = findViewById(R.id.qr_code);
        btn = findViewById(R.id.gen);
        share = findViewById(R.id.shareqr);
        save = findViewById(R.id.saveqr);
        final MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!code.getText().toString().isEmpty()) {
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(code.getText().toString(), BarcodeFormat.QR_CODE, 700, 700);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        qr_code.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please write text in EditText",Toast.LENGTH_SHORT).show();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //share image

                Drawable drawable = qr_code.getDrawable();
                if(drawable != null) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                    try {
                        File file = new File(getApplicationContext().getExternalCacheDir(), File.separator + "image.jpg");
                        FileOutputStream fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        file.setReadable(true, false);
                        final Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);

                        intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setType("image/jpeg");

                        startActivity(Intent.createChooser(intent, "Share image via"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Generate the image first",Toast.LENGTH_SHORT).show();
                }
            }
        });
         save.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //save image
                 Drawable drawable = qr_code.getDrawable();
                 if(drawable != null) {
                     Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                     try {
                         File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), File.separator + currentDateFormat() + ".jpg");
                         FileOutputStream fOut = new FileOutputStream(file);
                         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                         fOut.flush();
                         fOut.close();
                         Toast.makeText(getApplicationContext(), "Download/"+currentDateFormat() + ".jpg", Toast.LENGTH_SHORT).show();
                     } catch (Exception e) {
                         Toast.makeText(getApplicationContext(), "Error occur while saving", Toast.LENGTH_SHORT).show();
                         e.printStackTrace();
                     }
                 }
                 else{
                     Toast.makeText(getApplicationContext(),"Please Generate the image first",Toast.LENGTH_SHORT).show();
                 }

             }
         });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTime = dateFormat.format(new Date());
        return currentTime;
    }
}
