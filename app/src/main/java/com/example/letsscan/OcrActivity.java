package com.example.letsscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letsscan.Dictionary.DictPagerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;

public class OcrActivity extends AppCompatActivity {

    ImageView ocr_image;
    Button gallery,camera,ocr;
    Bitmap bitmap;
    File photoFile = null;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        ocr_image = findViewById(R.id.ocr_image);
        gallery = findViewById(R.id.gallery_button);
        camera = findViewById(R.id.camera_button);
        ocr = findViewById(R.id.ocr_1);
        Toolbar toolbar = findViewById(R.id.ocr_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("OCR");
        toolbar.setTitleTextColor(Color.WHITE);
        ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();


                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText result) {
                                // Task completed successfully
                                // ...
                                if(!result.getText().equals("")){
                                    Intent intent = new Intent(OcrActivity.this, ResultActivity.class);
                                    intent.putExtra("result", result.getText());
                                    startActivity(intent);
                                }
                                else
                                    Toast.makeText(getApplicationContext(),"Text not found",Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        Toast.makeText(getApplicationContext(),"Text Not Found",Toast.LENGTH_LONG).show();
                                    }
                                });

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OcrActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
                }
                else
                {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        try {

                            photoFile = createImageFile();
                            Log.i("Mayank",photoFile.getAbsolutePath());

                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, 102);
                            }
                        } catch (Exception ex) {
                            // Error occurred while creating the File
                             }

            }
        } }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 101);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri imageUri = data.getData();
            if (resultCode == RESULT_OK && requestCode == 101) {
                ocr_image.setImageURI(imageUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ocr.setVisibility(View.VISIBLE);
            } else if (resultCode == RESULT_OK && requestCode == 102) {
                Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                bitmap = myBitmap;
                ocr_image.setImageBitmap(myBitmap);
                ocr.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "images";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}

