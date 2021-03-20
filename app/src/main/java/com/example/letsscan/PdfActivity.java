package com.example.letsscan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tom_roush.pdfbox.multipdf.Splitter;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission;
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PdfActivity extends AppCompatActivity {

    int encrypted = 1, split = 2, pdfToImage=3, decrypt = 4, merge = 5;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        txt = findViewById(R.id.path);
        Toolbar toolbar = findViewById(R.id.pdfactionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("PDF TOOLS");
        toolbar.setTitleTextColor(Color.WHITE);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        setup();
    }

    private void setup() {
        // Enable Android-style asset loading (highly recommended)
        PDFBoxResourceLoader.init(getApplicationContext());

        // Need to ask for write permissions on SDK 23 and up, this is ignored on older versions
        if (ContextCompat.checkSelfPermission(PdfActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(PdfActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(PdfActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(PdfActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
    }

    public void splitPdf(File file) {

        File folder = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath() + "/" + currentDateFormat());
        if (!folder.exists()) {
            folder.mkdir();
        }
        try{
            PDDocument document = PDDocument.load(file);

            //Instantiating Splitter class
            Splitter splitter = new Splitter();

            //splitting the pages of a PDF document
            List<PDDocument> Pages = splitter.split(document);

            //Creating an iterator
            Iterator<PDDocument> iterator = Pages.listIterator();

            //Saving each page as an individual document
            int i = 1;
            while(iterator.hasNext()) {
                PDDocument pd = iterator.next();
                pd.save(folder.getAbsolutePath()+"/sample"+ i++ +".pdf");
            }
            System.out.println("Multiple PDF’s created");
            document.close();
            Toast.makeText(getApplicationContext(),"Collection of pdfs available at "+folder.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.d("PdfBox-Android-Sample", "Exception thrown while splitting file", e);
        }
    }

    public void EncryptPdf(File file,String password) {

        try
        {
            PDDocument document = PDDocument.load(file);

            //Creating access permission object
            AccessPermission ap = new AccessPermission();

            //Creating StandardProtectionPolicy object
            StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, ap);

            //Setting the length of the encryption key
            spp.setEncryptionKeyLength(128);

            //Setting the access permissions
            spp.setPermissions(ap);

            //Protecting the document
            document.protect(spp);

            System.out.println("Document encrypted");

            //Saving the document
            document.save(file.getAbsolutePath());
            //Closing the document
            document.close();
            Toast.makeText(getApplicationContext(),"Pdf Encrypted",Toast.LENGTH_SHORT).show();

        }
        catch (Exception e){
            Log.d("PdfBox-Android-Sample", "Exception thrown while encrypting file", e);
        }
    }

    public void renderFile(File file) {

        File folder = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath() + "/" + currentDateFormat());
        if (!folder.exists()) {
            folder.mkdir();
        }
        try {
            // Load in an already created PDF
            PDDocument document = PDDocument.load(file);
            // Create a renderer for the document
            PDFRenderer renderer = new PDFRenderer(document);
            // Render the image to an RGB Bitmap
            Bitmap pageImage;

            for (int i = 0; i < document.getNumberOfPages(); i++){

                pageImage = renderer.renderImage(i, 1, Bitmap.Config.RGB_565);
                // Save the render result to an image
                String path = folder.getAbsolutePath() +"/render"+i+".jpg";
                File renderFile = new File(path);
                FileOutputStream fileOut = new FileOutputStream(renderFile);
                pageImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);
                fileOut.close();
            }
            Toast.makeText(getApplicationContext(),"Images available at "+folder.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        }
        catch (IOException  e)
        {
            Log.e("PdfBox-Android-Sample", "Exception thrown while rendering file", e);
        }
    }

    public void getPDF(int requestcode){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, requestcode);
    }

    public void Options(View view) {
        if(view == findViewById(R.id.buttonEncrypted)){
            getPDF(encrypted);
        }
        else if(view == findViewById(R.id.buttonRender)){
            getPDF(pdfToImage);
        }
        else if(view == findViewById(R.id.buttonSplitPdf)){
            getPDF(split);
        }
        else if(view == findViewById(R.id.buttonDecryptPdf)){
            getPDF(decrypt);
        }
        else
        {
            startActivity(new Intent(PdfActivity.this,MergePDF.class));
        }
    }

    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTime = dateFormat.format(new Date());
        return currentTime;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri obj = data.getData();

            File file = new File(obj.getPath());//create path from uri
            final String[] split = file.getPath().split(":");//split the path.
            String path = Environment.getExternalStorageDirectory() + "/" + split[1];
            if(path.contains("/storage/emulated/0/Download/")) {
                path = path.replace("/storage/emulated/0//","/");
                file = new File(path);
                txt.setText(path);
            }
            else {
                file = new File(path);
                txt.setText(path);
            }

            if (requestCode == 1) {
                AlertDialog.Builder alert = new AlertDialog.Builder(PdfActivity.this);

                alert.setTitle("Encryption");
                alert.setMessage("Write Password");

                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter.LengthFilter(10); //Filter to 10 characters
                input.setFilters(filters);
                alert.setView(input);

                final File finalFile = file;
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        // Do something with value!
                        EncryptPdf(finalFile, value);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            } else if (requestCode == 2) {
                splitPdf(file);
            } else if (requestCode == 3) {
                renderFile(file);
            }
            else if(requestCode == 4){
                try{
                    String en = "";
                    PDDocument pdd = PDDocument.load(file);
                    if (pdd.isEncrypted()){
                        en = "encrypted";
                        AlertDialog.Builder alert = new AlertDialog.Builder(PdfActivity.this);

                        alert.setTitle("Decryption");
                        alert.setMessage("Write Password");

                        // Set an EditText view to get user input
                        final EditText input = new EditText(this);
                        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        InputFilter[] filters = new InputFilter[1];
                        filters[0] = new InputFilter.LengthFilter(10); //Filter to 10 characters
                        input.setFilters(filters);
                        alert.setView(input);

                        final File finalFile = file;
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String value = input.getText().toString();
                                // Do something with value!
                                DecryptPdf(finalFile, value);
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"PDF is not encrypted",Toast.LENGTH_SHORT).show();
                    }
                    if (en.equals("encrypted"))
                        Toast.makeText(getApplicationContext(),"Pdf is encrypted",Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void DecryptPdf(File finalFile, String value) {
        try {
            PDDocument pdd = PDDocument.load(finalFile, value);

            // removing all security from PDF file
            pdd.setAllSecurityToBeRemoved(true);

            // Save the PDF file
            pdd.save(finalFile);

            // Close the PDF file
            pdd.close();
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Some Error occur while decrypting, Please check the password",Toast.LENGTH_SHORT).show();
        }
    }
}

