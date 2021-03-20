package com.example.letsscan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.MemoryFile;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tom_roush.pdfbox.io.MemoryUsageSetting;
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;

public class MergePDF extends AppCompatActivity {

    TextView file1,file2;
    Button btn_file1,btn_file2,btn_save;
    EditText  mergefilename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_pdf);
        file1 = findViewById(R.id.file1);
        file2 = findViewById(R.id.file2);
        mergefilename = findViewById(R.id.mergefilename);
        btn_file1 = findViewById(R.id.btn_file1);
        btn_file2 = findViewById(R.id.btn_file2);
        btn_save = findViewById(R.id.btn_save);

        btn_file1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 1);
            }
        });
        btn_file2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 2);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mergefilename!=null && file1!=null && file2!=null) {
                    try {
                        File fileI, fileII;
                        fileI = new File(file1.getText().toString());
                        fileII = new File(file2.getText().toString());
                        //Instantiating PDFMergerUtility class
                        PDFMergerUtility PDFmerger = new PDFMergerUtility();

                        //Setting the destination file
                        PDFmerger.setDestinationFileName((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath() + "/" +mergefilename.getText().toString()+".pdf");

                        //adding the source files
                        PDFmerger.addSource(fileI);
                        PDFmerger.addSource(fileII);

                        //Merging the two documents
                        PDFmerger.mergeDocuments(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Enter Filename",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        PDFBoxResourceLoader.init(getApplicationContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            Uri obj = data.getData();

            File file = new File(obj.getPath());//create path from uri
            final String[] split = file.getPath().split(":");//split the path.
            String path = Environment.getExternalStorageDirectory() + "/" + split[1];
            if(path.contains("/storage/emulated/0/Download/")) {
                path = path.replace("/storage/emulated/0//","/");
            }

            if(requestCode == 1){
                file1.setText(path);
                btn_file1.setClickable(false);
            }
            if(requestCode == 2){
                file2.setText(path);
                btn_file2.setClickable(false);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
