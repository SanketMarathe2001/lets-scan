package com.example.letsscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {

    TextView result_text;
    Button share_res_text,copy_res_text,url;

    ClipboardManager myClipboard;
    ClipData myClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle bundle = getIntent().getExtras();
        String result = bundle.getString("result");
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        result_text = findViewById(R.id.result_text);
        share_res_text = findViewById(R.id.share_res_text);
        copy_res_text = findViewById(R.id.copy_res_text);
        url = findViewById(R.id.url);

        result_text.setText(result);

        if(URLUtil.isValidUrl(result)){
         url.setVisibility(View.VISIBLE);
        }

        copy_res_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClip = ClipData.newPlainText("text",result);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(),"Text Save",Toast.LENGTH_LONG).show();

            }
        });

        share_res_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, result);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(result));
                startActivity(i);
            }
        });
    }
}
