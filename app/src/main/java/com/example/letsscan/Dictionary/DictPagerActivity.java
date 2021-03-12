package com.example.letsscan.Dictionary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.letsscan.AsyncTasks.DictAPITask;
import com.example.letsscan.AsyncTasks.DictAsyncResponse;
import com.example.letsscan.AsyncTasks.TransAPITask;
import com.example.letsscan.AsyncTasks.TranslateAsyncResponse;
import com.example.letsscan.BuildConfig;
import com.example.letsscan.OcrActivity;
import com.example.letsscan.R;
import com.example.letsscan.ResultActivity;
import com.example.letsscan.SpeechSynthesis;
import com.example.letsscan.TP_Callables.VisionCallable;
import com.example.letsscan.ThreadPoolManager.CustomThreadPoolManager;
import com.example.letsscan.ThreadPoolManager.UiThreadCallback;
import com.example.letsscan.recycler_views.L1DictData;
import com.example.letsscan.recycler_views.L1RecyclerView;
import com.example.letsscan.recycler_views.TransData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DictPagerActivity extends AppCompatActivity implements UiThreadCallback, DictAsyncResponse, TranslateAsyncResponse, AdapterView.OnItemSelectedListener {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    static final int TIME_INTERVAL = 2000;
    static Animation bb_b2t;
    static Animation bb_t2b;
    public static DictAPITask dictAPItask;
    static LayoutInflater inflater;
    static BottomSheetBehavior mBottomSheetBehavior;
    static NestedScrollView nestedScrollView;
    static Animation tb_b2t;
    static Animation tb_t2b;
    static Boolean topBarHidden = false;
    public static TransAPITask transAPItask;
    /* access modifiers changed from: private */
    public static ViewPager2 viewPager;
    int CAMERA_REQUEST_CODE = 1;
    int CODE_POSITION = 36;
    /* access modifiers changed from: private */
    public SpeechSynthesis SpeechActivity;
    DictPagerAdapter adapter;
    ImageButton audio;
    ImageButton bookmark;
    FrameLayout bottom_bar_dict;
    public Context context;
    public List<VisionAPIData> dictPackets = new ArrayList();
    ImageButton dict_btn;
    private boolean doubleBackToExitPressedOnce;
    Button google_result;
    Boolean isOpen = false;
    Boolean isSpeaking = false;
    String[] langCode = {"af", "sq", "am", "ar", "hy", "az", "eu", "be", "bn", "bs", "bg", "ca", "ceb", "ny", "zh-CN", "co", "hr", "cs", "da", "nl", "en", "eo", "et", "tl", "fi", "fr", "fy", "gl", "ka", "de", "el", "gu", "ht", "ha", "haw", "iw", "hi", "hmn", "hu", "is", "ig", "id", "ga", "it", "ja", "jw", "kn", "kk", "km", "rw", "ko", "ku", "ky", "lo", "la", "lv", "lt", "lb", "mk", "mg", "ms", "ml", "mt", "mi", "mr", "mn", "my", "ne", "no", "or", "ps", "fa", "pl", "pt", "pa", "ro", "ru", "sm", "gd", "sr", "st", "sn", "sd", "si", "sk", "sl", "so", "es", "su", "sw", "sv", "tg", "ta", "tt", "te", "th", "tr", "tk", "uk", "ur", "ug", "uz", "vi", "cy", "xh", "yi", "yo", "zu"};
    Spinner loadingSpinner;
    private CustomThreadPoolManager mCustomThreadPoolManager;
    private Handler mHandler = new Handler();
    TextView pageCountTV;
    ArrayList<String> path_list = new ArrayList<>();
    TextView phonetic;
    int position_rec;
    LinearLayout progressBar;
    ImageButton text2speech_btn;
    ImageButton ocr,share_img;
    TextView transText;
    ImageButton translate;
    LinearLayout translateBar;
    private UiHandler uiHandler;
    ArrayList<String> visionPushPath = new ArrayList<>();
    TextView word;
    Button wrong_result;

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public DictPagerActivity() {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_dict_pager);
        getWindow().setFlags(1024, 1024);
        this.path_list = getIntent().getStringArrayListExtra("pathlist");
        this.position_rec = getIntent().getIntExtra("position", 0);
        getIntent().removeExtra("pathlist");
        this.context = this;
        Iterator<String> it = this.path_list.iterator();
        while (it.hasNext()) {
            String next = it.next();
            this.dictPackets.add(new VisionAPIData(this.path_list.indexOf(next), next, (ArrayList<String>) null, (ArrayList<RectF>) null, (String) null, false, false));
        }
        this.pageCountTV = (TextView) findViewById(R.id.img_count_dict_pager);
        this.bottom_bar_dict = (FrameLayout) findViewById(R.id.dict_bottom_bar);
        this.dict_btn = (ImageButton) findViewById(R.id.dict_btn_bottom_bar);
        this.text2speech_btn = (ImageButton) findViewById(R.id.text2speech_bottom_bar);
        this.progressBar = (LinearLayout) findViewById(R.id.progress_bar_dict);
        this.ocr = (ImageButton) findViewById(R.id.ocr);
        this.share_img = (ImageButton) findViewById(R.id.share_img);
        ViewPager2 viewPager2 = (ViewPager2) findViewById(R.id.viewPagerDictSlider);
        viewPager = viewPager2;
        viewPager2.setOffscreenPageLimit(3);
        DictPagerAdapter dictPagerAdapter = new DictPagerAdapter(this, false);
        this.adapter = dictPagerAdapter;
        dictPagerAdapter.submitList(new ArrayList(this.dictPackets));
        viewPager.setAdapter(this.adapter);
        viewPager.setCurrentItem(this.position_rec);
        TextView textView = this.pageCountTV;
        textView.setText("" + (this.position_rec + 1) + "/" + this.path_list.size());
        SpeechSynthesis speechSynthesis = new SpeechSynthesis();
        this.SpeechActivity = speechSynthesis;
        speechSynthesis.engineSetup(this.context);
        NestedScrollView nestedScrollView2 = (NestedScrollView) findViewById(R.id.bottom_sheet);
        nestedScrollView = nestedScrollView2;
        BottomSheetBehavior from = BottomSheetBehavior.from(nestedScrollView2);
        mBottomSheetBehavior = from;
        from.setState(BottomSheetBehavior.STATE_COLLAPSED);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater = LayoutInflater.from(this);
        this.dict_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int currentItem = DictPagerActivity.viewPager.getCurrentItem();
                VisionAPIData visionAPIData = DictPagerActivity.this.dictPackets.get(currentItem);
                DictPagerActivity.this.dictPackets.set(currentItem, new VisionAPIData(visionAPIData.ID, visionAPIData.path, visionAPIData.words, visionAPIData.rectangles, visionAPIData.completePage, visionAPIData.status, !visionAPIData.display));
                if (DictPagerActivity.this.dictPackets.get(currentItem).display) {
                    DictPagerActivity dictPagerActivity = DictPagerActivity.this;
                    if (!dictPagerActivity.startVisionAPI(dictPagerActivity.path_list.get(currentItem))) {
                        DictPagerActivity.this.adapter.submitList(new ArrayList(DictPagerActivity.this.dictPackets));
                    }
                    DictPagerActivity.this.dictScrollView(true);
                    return;
                }
                DictPagerActivity.this.stopProgressBar();
                DictPagerActivity.this.adapter.submitList(new ArrayList(DictPagerActivity.this.dictPackets));
                DictPagerActivity.this.dictScrollView(false);
            }
        });
        this.text2speech_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int currentItem = DictPagerActivity.viewPager.getCurrentItem();
                if (DictPagerActivity.this.isSpeaking.booleanValue()) {
                    DictPagerActivity.this.shutSpeak();
                    Toast.makeText(DictPagerActivity.this.getBaseContext(), "Stopped AI Narrator", Toast.LENGTH_LONG).show();
                    if (!DictPagerActivity.this.dictPackets.get(currentItem).display) {
                        DictPagerActivity.this.stopProgressBar();
                        return;
                    }
                    return;
                }
                DictPagerActivity.this.text2speech_btn.setAlpha(1.0f);
                DictPagerActivity.this.text2speech_btn.setImageResource(R.drawable.ic_pause_top_bar);
                Toast.makeText(DictPagerActivity.this.getBaseContext(), "Starting AI Narrator", Toast.LENGTH_LONG).show();
                if (!DictPagerActivity.this.dictPackets.get(currentItem).status) {
                    if (!DictPagerActivity.this.visionPushPath.contains(DictPagerActivity.this.path_list.get(currentItem))) {
                        DictPagerActivity dictPagerActivity = DictPagerActivity.this;
                        boolean unused = dictPagerActivity.startVisionAPI(dictPagerActivity.path_list.get(currentItem));
                    }
                    DictPagerActivity.this.startProgressBar();
                }
                DictPagerActivity.this.SpeechActivity.startSpeech(DictPagerActivity.this.dictPackets.get(DictPagerActivity.viewPager.getCurrentItem()).completePage);
                DictPagerActivity.this.isSpeaking = true;
            }
        });
        this.ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = DictPagerActivity.viewPager.getCurrentItem();
                File file = new File(DictPagerActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.annotated/"),path_list.get(currentItem));
                Uri uri = Uri.fromFile(file);
                Bitmap bitmap=null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(DictPagerActivity.this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();


                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText result) {
                                // Task completed successfully
                                // ...
                                Intent intent = new Intent(DictPagerActivity.this, ResultActivity.class);
                                intent.putExtra("result", result.getText());
                                startActivity(intent);
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
        this.share_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = DictPagerActivity.viewPager.getCurrentItem();
                File file = new File(DictPagerActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.annotated/"),path_list.get(currentItem));
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent, "Share image via"));

            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            public void onPageScrolled(int i, float f, int i2) {
                super.onPageScrolled(i, f, i2);
            }

            public void onPageSelected(int i) {
                super.onPageSelected(i);
                TextView textView = DictPagerActivity.this.pageCountTV;
                textView.setText("" + (i + 1) + "/" + DictPagerActivity.this.path_list.size());
                DictPagerActivity dictPagerActivity = DictPagerActivity.this;
                dictPagerActivity.dictScrollView(dictPagerActivity.dictPackets.get(i).display);
                DictPagerActivity.this.shutSpeak();
                if (!DictPagerActivity.this.visionPushPath.contains(DictPagerActivity.this.dictPackets.get(DictPagerActivity.viewPager.getCurrentItem()).path) || !DictPagerActivity.this.dictPackets.get(DictPagerActivity.viewPager.getCurrentItem()).display) {
                    DictPagerActivity.this.stopProgressBar();
                } else {
                    DictPagerActivity.this.startProgressBar();
                }
            }

            public void onPageScrollStateChanged(int i) {
                super.onPageScrollStateChanged(i);
            }
        });
    }

    public void shutSpeak() {
        this.text2speech_btn.setAlpha(0.7f);
        this.SpeechActivity.stopSpeech();
        this.isSpeaking = false;
        this.text2speech_btn.setImageResource(R.drawable.ic_headset);
    }

    public void dictScrollView(boolean z) {
        if (z) {
            this.dict_btn.setAlpha(1.0f);
            emptyBSD();
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            return;
        }
        emptyBSD();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        this.dict_btn.setAlpha(0.7f);
    }

    public static void disableUserInput() {
        viewPager.setUserInputEnabled(false);
    }

    public static void enableUserInput() {
        viewPager.setUserInputEnabled(true);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.uiHandler = new UiHandler(Looper.getMainLooper(), this);
        CustomThreadPoolManager customThreadPoolManager = CustomThreadPoolManager.getsInstance();
        this.mCustomThreadPoolManager = customThreadPoolManager;
        customThreadPoolManager.setUiThreadCallback(this);
    }

    public void startProgressBar() {
        this.progressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar() {
        this.progressBar.setVisibility(View.GONE);
    }

    /* access modifiers changed from: private */
    public boolean startVisionAPI(String str) {
        if (this.dictPackets.get(this.path_list.indexOf(str)).status) {
            return false;
        }
        this.visionPushPath.add(str);
        startProgressBar();
        Log.d("MYTAG", "Adding callable for image @ " + str);
        int indexOf = this.path_list.indexOf(str);
        VisionCallable visionCallable = new VisionCallable(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.annotated/"), str).getAbsolutePath(), str, indexOf, this);
        visionCallable.setCustomThreadPoolManager((CustomThreadPoolManager) this.mCustomThreadPoolManager);
        this.mCustomThreadPoolManager.addCallable(visionCallable);
        return true;
    }

    /* access modifiers changed from: private */
    public void decodeMessage(Message message) {
        Log.d("TPM", "Received result from thread pool -> callable");
        VisionAPIData visionAPIData = (VisionAPIData) message.getData().getParcelable("dictData");
        visionAPIData.display = this.dictPackets.get(visionAPIData.ID).display;
        this.dictPackets.set(visionAPIData.ID, visionAPIData);
        this.adapter.submitList(new ArrayList(this.dictPackets));
        this.visionPushPath.remove(visionAPIData.path);
        if (viewPager.getCurrentItem() == visionAPIData.ID) {
            stopProgressBar();
            if (this.isSpeaking.booleanValue()) {
                this.SpeechActivity.startSpeech(visionAPIData.completePage);
            }
        }
    }

    public void publishToUiThread(Message message) {
        UiHandler uiHandler2 = this.uiHandler;
        if (uiHandler2 != null) {
            uiHandler2.sendMessage(message);
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        adapterView.getItemAtPosition(i).toString();
        this.CODE_POSITION = i;
        PrintStream printStream = System.out;
        printStream.print(this.CODE_POSITION + IOUtils.LINE_SEPARATOR_UNIX);
        transRun(this.word.getText().toString());
    }

    private static class UiHandler extends Handler {
        private WeakReference<DictPagerActivity> mTarget;

        public UiHandler(Looper looper, DictPagerActivity dictPagerActivity) {
            super(looper);
            this.mTarget = new WeakReference<>(dictPagerActivity);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            DictPagerActivity dictPagerActivity = (DictPagerActivity) this.mTarget.get();
            if (dictPagerActivity != null) {
                dictPagerActivity.decodeMessage(message);
            }
        }
    }

    public void dictProcessFinish(L1DictData l1DictData) {
        Log.i("DICTAPI", "dictProcessFinish() triggered ");
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        createBSD(l1DictData);
    }

    public void translateProcessFinish(TransData transData) {
        Log.d("Translate", transData.translation + IOUtils.LINE_SEPARATOR_UNIX);
        this.transText.setText(transData.translation.toString());
        this.translateBar.setLayoutParams(new LinearLayout.LayoutParams(this.translateBar.getWidth(), 100));
    }

    public class MyJavaScriptInterface {
        private Context ctx;
        public String html;

        MyJavaScriptInterface(Context context) {
            this.ctx = context;
        }

        @JavascriptInterface
        public void showHTML(String str) {
            this.html = str;
        }
    }

    public void transRun(final String str) {
        String str2 = "https://translate.google.com/#view=home&op=translate&sl=" + "en" + "&tl=" + this.langCode[this.CODE_POSITION] + "&text=" + str;
        System.out.print(str2 + IOUtils.LINE_SEPARATOR_UNIX);
        final WebView webView = new WebView(this.context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(this.context), "HtmlViewer");
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView webView, String str) {
                webView.evaluateJavascript("document.getElementsByTagName('table')[0].innerHTML", new ValueCallback<String>() {
                    public void onReceiveValue(String str) {
                        String replace = str.replace("\\u003C", "<").replace("\\u003E", ">").replace("\\\"", "\"");
                        DictPagerActivity.transAPItask = new TransAPITask("<table>" + replace + "</table>");
                        DictPagerActivity.transAPItask.delegate = (TranslateAsyncResponse) DictPagerActivity.this.context;
                        DictPagerActivity.transAPItask.execute(new String[]{str});
                    }
                });
            }
        });
        webView.loadUrl(str2);
    }

    public void emptyBSD() {
        View inflate = inflater.inflate(R.layout.bottom_sheet_empty, (ViewGroup) null);
        Log.d("TAGGY", "1");
        nestedScrollView.removeAllViews();
        nestedScrollView.addView(inflate);
    }

    private void createBSD(L1DictData l1DictData) {
        if (l1DictData != null) {
            View inflate = inflater.inflate(R.layout.bottom_sheet_dialog, (ViewGroup) null);
            this.word = (TextView) inflate.findViewById(R.id.word);
            this.phonetic = (TextView) inflate.findViewById(R.id.phonetic);
            this.audio = (ImageButton) inflate.findViewById(R.id.audio_btn);
            this.translate = (ImageButton) inflate.findViewById(R.id.translateButton);
            this.loadingSpinner = (Spinner) inflate.findViewById(R.id.translate_spinner);
            this.transText = (TextView) inflate.findViewById(R.id.transText);
            this.translateBar = (LinearLayout) inflate.findViewById(R.id.translateBar);
            ArrayAdapter<CharSequence> createFromResource = ArrayAdapter.createFromResource(this, R.array.language_array, android.R.layout.simple_spinner_dropdown_item);
            createFromResource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.loadingSpinner.setAdapter(createFromResource);
            this.loadingSpinner.setSelection(this.CODE_POSITION);
            this.translate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    DictPagerActivity.this.loadingSpinner.setVisibility(View.VISIBLE);
                    DictPagerActivity dictPagerActivity = DictPagerActivity.this;
                    dictPagerActivity.transRun(dictPagerActivity.word.getText().toString());
                    DictPagerActivity.this.loadingSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) DictPagerActivity.this.context);
                }
            });
            this.word.setText(l1DictData.word);
            this.audio.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    PrintStream printStream = System.out;
                    printStream.print(DictPagerActivity.this.word.getText().toString() + IOUtils.LINE_SEPARATOR_UNIX);
                    DictPagerActivity.this.SpeechActivity.startSpeech(DictPagerActivity.this.word.getText().toString());
                }
            });
            if (l1DictData.phonetic_available) {
                this.phonetic.setText(l1DictData.phonetic);
            } else {
                this.phonetic.setText("...");
            }
            RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.recyclerView1);
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(new L1RecyclerView(l1DictData.l2DictData, this));
            } else {
                Toast.makeText(getApplicationContext(), "Error 404", Toast.LENGTH_LONG).show();
            }
            nestedScrollView.removeAllViews();
            nestedScrollView.addView(inflate);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            return;
        }
        View inflate2 = inflater.inflate(R.layout.bottom_sheet_negative, (ViewGroup) null);
        nestedScrollView.removeAllViews();
        nestedScrollView.addView(inflate2);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void dictRun(String str) {
        Log.i("TOUCH", str);
        DictAPITask dictAPITask = new DictAPITask();
        dictAPItask = dictAPITask;
        dictAPITask.delegate = (DictAsyncResponse) this;
        dictAPItask.execute(new String[]{str});
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        shutSpeak();
        if (!this.dictPackets.get(viewPager.getCurrentItem()).display) {
            stopProgressBar();
        }
    }
}
