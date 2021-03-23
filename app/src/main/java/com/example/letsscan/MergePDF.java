package com.example.letsscan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
                if(mergefilename.getText()!=null && file1.getText()!=null && file2.getText()!=null) {
                    try
                    {
                        List<InputStream> list = new ArrayList<InputStream>();

                        InputStream inputStreamOne = new FileInputStream(new File(file1.getText().toString()));
                        list.add(inputStreamOne);
                        InputStream inputStreamTwo = new FileInputStream(new File(file2.getText().toString()));
                        list.add(inputStreamTwo);

                        OutputStream outputStream = new FileOutputStream(new File("/storage/emulated/0/Download/"+mergefilename+".pdf"));
                        mergePdf(list, outputStream);
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (DocumentException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
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

    private static void mergePdf(List<InputStream> list, OutputStream outputStream) throws DocumentException, IOException
    {
            Document document = new Document();

        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte pdfContentByte = pdfWriter.getDirectContent();

        for (InputStream inputStream : list)
        {
            PdfReader pdfReader = new PdfReader(inputStream);
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++)
            {
                document.newPage();
                PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, i);
                pdfContentByte.addTemplate(page, 0, 0);
            }
        }

        outputStream.flush();
        document.close();
        outputStream.close();
    }
}
