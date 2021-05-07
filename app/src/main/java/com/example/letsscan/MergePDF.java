package com.example.letsscan;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MergePDF extends AppCompatActivity {

    private EditText txt1,txt2;
    private Button bt1,bt2;
    private Handler handler;
    private final int PICKFILE_RESULT_CODE=10;
    private String btTag="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PDFBoxResourceLoader.init(getApplicationContext());
        setContentView(R.layout.activity_merge_pdf);
        txt1=(EditText)findViewById(R.id.txtfirstpdf);
        txt2=(EditText)findViewById(R.id.txtsecondpdf);
        txt1.setEnabled(false);
        txt2.setEnabled(false);
        bt1=(Button)findViewById(R.id.bt1);
        bt2=(Button)findViewById(R.id.bt2);
        Toolbar toolbar = findViewById(R.id.merge_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("MERGE PDF");
        toolbar.setTitleTextColor(Color.WHITE);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btTag=((Button)v).getTag().toString();
                showFileChooser();

            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btTag=((Button)v).getTag().toString();
                showFileChooser();

            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void mergePdfFiles(View view){
        try {
            if(!(txt1.getText().toString().equals("Pdf file") || txt1.getText().toString().equals("")) && (txt2.getText().toString().equals("Pdf file") || txt2.getText().toString().equals(""))){
            String[] srcs = {txt1.getText().toString(), txt2.getText().toString()};
            mergePdf(srcs);
            }
            else
                Toast.makeText(getApplicationContext(),"You cannot leave it blank.",Toast.LENGTH_SHORT).show();
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    // Save tag of the clicked button
    // It is used to identify the button has been pressed
    public void onSaveInstanceState( Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("savText", btTag);


    }
    @Override
    // Restore the tag
    protected void onRestoreInstanceState( Bundle savedInstanceState) {
        super.onRestoreInstanceState( savedInstanceState);
        btTag=savedInstanceState.getString( "savText");

    }

    public void mergePdf(String[] srcs){
        try{
            // Create document object
            Document document = new Document();
            // Create pdf copy object to copy current document to the output mergedresult file
            PdfCopy copy = new PdfCopy(document, new FileOutputStream((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath() + "/" + currentDateFormat()+".pdf"));
            // Open the document
            document.open();
            PdfReader pr;
            int n;
            for (String src : srcs) {
                // Create pdf reader object to read each input pdf file
                pr = new PdfReader(src.toString());
                // Get the number of pages of the pdf file
                n = pr.getNumberOfPages();
                for (int page = 1; page <= n; page++) {
                    // Import all pages from the file to PdfCopy
                    copy.addPage(copy.getImportedPage(pr, page));
                }
            }
            document.close();
            Toast.makeText(getApplicationContext(),"File Available at "+(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath() + "/" + currentDateFormat()+".pdf",Toast.LENGTH_SHORT).show();// close the document

        }catch(Exception e){e.printStackTrace();}
    }
    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }
    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTime = dateFormat.format(new Date());
        return currentTime;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            try {
                Uri obj = data.getData();
                File file = new File(obj.getPath());//create path from uri
                final String[] split = file.getPath().split(":");//split the path.
                String path = Environment.getExternalStorageDirectory() + "/" + split[1];
                if(path.contains("/storage/emulated/0/Download/")) {
                    path = path.replace("/storage/emulated/0//","/");
                }
                if (requestCode == PICKFILE_RESULT_CODE) {
                    if (resultCode == RESULT_OK) {
                        if(!checkencrypt(new File(path))) {
                            String FilePath = data.getData().getPath();
                            if (bt1.getTag().toString().equals(btTag))
                                txt1.setText(path);
                            else
                                txt2.setText(path);
                        }
                        else{
                            path="";
                            Toast.makeText(getApplicationContext(),"Use cannot select the encrypt file.",Toast.LENGTH_SHORT).show();
                            if (bt1.getTag().toString().equals(btTag))
                                txt1.setText(path);
                            else
                                txt2.setText(path);
                        }
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public boolean checkencrypt(File file){
        try{
            PDDocument pdd = PDDocument.load(file);
            if(pdd.isEncrypted()){
            }
            else
                return false;
            pdd.close();
        }
        catch (InvalidPasswordException e){
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}


