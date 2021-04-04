package com.example.letsscan;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.multidex.BuildConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letsscan.Functions.Utils;
import com.example.letsscan.TP_Callables.ImageImportCallable;
import com.example.letsscan.TP_Callables.PDFImportCallable;
import com.example.letsscan.ThreadPoolManager.CustomThreadPoolManager;
import com.example.letsscan.ThreadPoolManager.UiThreadCallback;
import com.example.letsscan.Views.FileDialog;
import com.example.letsscan.pdfTest.TestActivity;
import com.example.letsscan.recycler_views.FileData;
import com.example.letsscan.recycler_views.FilesRecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.apache.http.protocol.HTTP;

public class FileActivity extends AppCompatActivity implements FilesRecyclerView.OnClickThumbListener, FileDialog.FileDialogListener, ActivityCompat.OnRequestPermissionsResultCallback, NavigationView.OnNavigationItemSelectedListener, UiThreadCallback, ScrollBottomSheet.BottomSheetListener {
    public static int clickedFile;
    public static Size dispSize;
    public static ArrayList<FileData> listItems;
    public static Size tvSize;
    int CAMERA_REQUEST_CODE = 1;
    private int PICK_IMAGES = 103;
    /* access modifiers changed from: private */
    public int PICK_PDF_FILE = 102;
    int SAVE_PDF_KEY = 2;
    int SHARE_PDF_KEY = 3;
    /* access modifiers changed from: private */
    public FilesRecyclerView adapter;
    LinearLayout addLoadingBar;
    Boolean batch_mode = true;
    Animation cam_btn_b2t;
    Animation cam_btn_t2b;
    FrameLayout cameraBottomBar;
    TextView centerText;
    DrawerLayout drawer;
    EditText editText;
    ArrayList<Integer> filterPos;
    boolean filterStatus = false;
    ArrayList<ImageAttrs> imageAttrs = new ArrayList<>();
    private CustomThreadPoolManager mCustomThreadPoolManager;
    String new_file;
    ArrayList<String> pathlist = new ArrayList<>();
    Boolean scroll_down = true;
    ImageButton search_btn;
    boolean search_pressed = false;
    private UiHandler uiHandler;

    public void onButtonClicked(String str, int i) {
    }

    static {
        System.loadLibrary("opencv_java3");
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.files_view);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.all_files);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(-1);
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.addLoadingBar = (LinearLayout) findViewById(R.id.add_bar_file);
        ImageButton imageButton = (ImageButton) findViewById(R.id.scan_btn_file_act);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.pdf_btn_file_act);
        ImageButton imageButton3 = (ImageButton) findViewById(R.id.gallery_btn_file_act);
        this.cam_btn_b2t = AnimationUtils.loadAnimation(this, R.anim.camerabtn_translate_b2t);
        this.cam_btn_t2b = AnimationUtils.loadAnimation(this, R.anim.camerabtn_translate_t2b);
        this.cameraBottomBar = (FrameLayout) findViewById(R.id.cameraBar);
        this.editText = (EditText) findViewById(R.id.searchFile);
        ImageButton imageButton4 = (ImageButton) findViewById(R.id.search_file_btn);
        this.search_btn = imageButton4;
        imageButton4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (FileActivity.this.search_pressed) {
                    FileActivity fileActivity = FileActivity.this;
                    fileActivity.setTitle(fileActivity.getString(R.string.app_name));
                    FileActivity.this.search_pressed = false;
                    FileActivity.this.search_btn.setImageResource(R.drawable.ic_file_search);
                    FileActivity.this.editText.setVisibility(View.INVISIBLE);
                    FileActivity.this.editText.setHint("");
                    FileActivity.this.editText.getText().clear();
                    FileActivity.this.filterStatus = false;
                    FileActivity.this.adapter.listItems = FileActivity.listItems;
                    FileActivity.this.adapter.notifyDataSetChanged();
                    return;
                }
                FileActivity.this.setTitle("");
                FileActivity.this.search_pressed = true;
                FileActivity.this.search_btn.setImageResource(R.drawable.ic_close_search);
                FileActivity.this.editText.setVisibility(View.VISIBLE);
                FileActivity.this.editText.setHint(FileActivity.this.getString(R.string.enter_search_keyword));
            }
        });
        this.editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                FileActivity.this.filterStatus = true;
                FileActivity.this.filter(editable.toString());
            }
        });
        ((NavigationView) findViewById(R.id.navigation_drawer)).setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, this.drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        listItems = new ArrayList<>();
        loadViewData();
        this.centerText = (TextView) findViewById(R.id.textCenter);
        if (listItems.size() == 0) {
            this.centerText.setVisibility(View.VISIBLE);
        } else {
            this.centerText.setVisibility(View.INVISIBLE);
        }
        Calendar instance = Calendar.getInstance();
        int i = instance.get(11);
        Log.d("MYTAG", "" + instance.get(11) + instance.get(9));
        FilesRecyclerView filesRecyclerView = new FilesRecyclerView(listItems, getApplicationContext(), this, i <= 13 ? "Good Morning" : i <= 17 ? "Good Afternoon" : "Good Evening");
        this.adapter = filesRecyclerView;
        recyclerView.setAdapter(filesRecyclerView);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new FileDialog("Add", "", FileActivity.this).show(FileActivity.this.getSupportFragmentManager(), "file dialog");
            }
        });
        imageButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
                intent.addCategory("android.intent.category.OPENABLE");
                intent.setType("application/pdf");
                FileActivity fileActivity = FileActivity.this;
                fileActivity.startActivityForResult(intent, fileActivity.PICK_PDF_FILE);
            }
        });
        imageButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FileActivity.this.pickImage();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                if (i2 > 0) {
                    if (!FileActivity.this.scroll_down.booleanValue()) {
                        FileActivity.this.cameraBottomBar.startAnimation(FileActivity.this.cam_btn_t2b);
                        FileActivity.this.scroll_down = true;
                    }
                } else if (FileActivity.this.scroll_down.booleanValue()) {
                    FileActivity.this.cameraBottomBar.startAnimation(FileActivity.this.cam_btn_b2t);
                    FileActivity.this.scroll_down = false;
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
            }
        });
        dispSize = Utils.getDisplaySize(getWindowManager());
        tvSize = Utils.calculateTvSize(dispSize, new Rational(3, 4));
    }


    public void publishToUiThread(Message message) {
        UiHandler uiHandler2 = this.uiHandler;
        if (uiHandler2 != null) {
            uiHandler2.sendMessage(message);
        }
    }

    private static class UiHandler extends Handler {
        private WeakReference<FileActivity> mTarget;

        public UiHandler(Looper looper, FileActivity fileActivity) {
            super(looper);
            this.mTarget = new WeakReference<>(fileActivity);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            FileActivity fileActivity = (FileActivity) this.mTarget.get();
            if (fileActivity != null) {
                fileActivity.addFiles(message);
            }
        }
    }

    /* access modifiers changed from: private */
    public void addFiles(Message message) {
        Log.d("TPM", "Received result from thread pool -> callable");
        Bundle data = message.getData();
        ArrayList<String> stringArrayList = data.getStringArrayList("path_list");
        ArrayList<ImageAttrs> parcelableArrayList = data.getParcelableArrayList("image_attrs_list");
        this.pathlist = stringArrayList;
        this.imageAttrs = parcelableArrayList;
        this.addLoadingBar.setVisibility(View.INVISIBLE);
        startImagePager();
    }

    /* access modifiers changed from: package-private */
    public void startImagePager() {
        Log.d("PDFCheck", "" + this.pathlist.size());
        this.new_file = "New Doc " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        if (this.pathlist.size() > 1) {
            this.batch_mode = true;
        }
        Intent intent = new Intent(this, ImagePagerActivity.class);
        intent.putExtra("pathlist", this.pathlist);
        intent.putParcelableArrayListExtra("imageattrs", this.imageAttrs);
        intent.putExtra("batch_mode", this.batch_mode);
        this.pathlist = new ArrayList<>();
        this.imageAttrs = new ArrayList<>();
        startActivityForResult(intent, this.CAMERA_REQUEST_CODE);
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_images)), this.PICK_IMAGES);
    }

    /* access modifiers changed from: private */
    public void filter(String str) {
        ArrayList arrayList = new ArrayList();
        this.filterPos = new ArrayList<>();
        for (int i = 0; i < listItems.size(); i++) {
            if (listItems.get(i).getName().toLowerCase().contains(str.toLowerCase())) {
                arrayList.add(listItems.get(i));
                this.filterPos.add(Integer.valueOf(i));
            }
        }
        this.adapter.filterList(arrayList);
    }

    public void onStart() {
        super.onStart();
        this.adapter.notifyDataSetChanged();
    }

    public void loadViewData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        try {
            listItems = (ArrayList) new Gson().fromJson((Reader) new BufferedReader(new FileReader(new File(getExternalFilesDir("hidden"), "file.json"))), new TypeToken<ArrayList<FileData>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();
    }

    public void applyName(String str) {
        this.new_file = str;
        startActivityForResult(new Intent(this, CameraActivity.class), this.CAMERA_REQUEST_CODE);
    }

    public void applyRename(String str) {
        listItems.add(0, new FileData(str, new SimpleDateFormat("yyyy.MM.dd").format(new Date()), new SimpleDateFormat("HH:mm:ss").format(new Date()), listItems.get(clickedFile).getIconuri()));
        listItems.get(0).addImages(listItems.get(clickedFile + 1).getImages());
        listItems.remove(clickedFile + 1);
        Utils.writeGSON(listItems, this);
        if (this.filterStatus) {
            filter(this.editText.getText().toString());
        } else {
            this.adapter.notifyDataSetChanged();
        }
    }

    public void onfileSelect(int i) {
        int[] iArr = {R.drawable.ic_dialog_share, R.drawable.ic_dialog_save, R.drawable.ic_dialog_rename, R.drawable.ic_dialog_delete};
        String[] strArr = {getString(R.string.share_as_pdf), getString(R.string.save_as_pdf), getString(R.string.rename_file), getString(R.string.delete_file)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < 4; i2++) {
            HashMap hashMap = new HashMap();
            hashMap.put("image", Integer.valueOf(iArr[i2]));
            hashMap.put("text", strArr[i2]);
            arrayList.add(hashMap);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.more_options_item, new String[]{"image", "text"}, new int[]{R.id.alertDialogItemImageView, R.id.alertDialogItemTextView});
        if (this.filterStatus) {
            clickedFile = this.filterPos.get(i).intValue();
        } else {
            clickedFile = i;
        }
        builder.setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    ScrollBottomSheet scrollBottomSheet = new ScrollBottomSheet(FileActivity.listItems.get(FileActivity.clickedFile).getImages(), FileActivity.listItems.get(FileActivity.clickedFile).getName(), 3, FileActivity.this.getApplicationContext());
                    scrollBottomSheet.setCancelable(false);
                    scrollBottomSheet.show(FileActivity.this.getSupportFragmentManager(), "ScrollBottomSheet");
                } else if (i == 2) {
                    new FileDialog("Rename", FileActivity.listItems.get(FileActivity.clickedFile).getName(), FileActivity.this).show(FileActivity.this.getSupportFragmentManager(), "file dialog");
                } else if (i == 3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FileActivity.this);
                    builder.setTitle((CharSequence) "Delete");
                    builder.setMessage((CharSequence) "Are you sure?");
                    builder.setPositiveButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setNegativeButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FileActivity.listItems.remove(FileActivity.clickedFile);
                            Utils.writeGSON(FileActivity.listItems, FileActivity.this.getApplicationContext());
                            if (FileActivity.this.filterStatus) {
                                FileActivity.this.filter(FileActivity.this.editText.getText().toString());
                            } else {
                                FileActivity.this.adapter.notifyDataSetChanged();
                            }
                            if (FileActivity.listItems.size() == 0) {
                                FileActivity.this.centerText.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    builder.show();
                } else if (i == 1) {
                    ScrollBottomSheet scrollBottomSheet2 = new ScrollBottomSheet(FileActivity.listItems.get(FileActivity.clickedFile).getImages(), FileActivity.listItems.get(FileActivity.clickedFile).getName(), 2, FileActivity.this.getApplicationContext());
                    scrollBottomSheet2.setCancelable(false);
                    scrollBottomSheet2.show(FileActivity.this.getSupportFragmentManager(), "ScrollBottomSheet");
                }
            }
        });
        builder.create();
        builder.show();
    }

    public void onClickFile(int i) {
        if (this.filterStatus) {
            clickedFile = this.filterPos.get(i).intValue();
            this.search_btn.performClick();
        } else {
            clickedFile = i;
        }
        if (this.search_pressed) {
            this.search_btn.performClick();
        }
        this.pathlist = listItems.get(clickedFile).getImages();
        String name = listItems.get(clickedFile).getName();
        Intent intent = new Intent(this, ScrollActivity.class);
        intent.putExtra("pathlist", this.pathlist);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    public void updateImages(ArrayList<String> arrayList) {
        listItems.get(clickedFile).addImages(arrayList);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        try {
            if (i == this.CAMERA_REQUEST_CODE && i2 == -1 && intent != null) {
                ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("pathlist");
                this.pathlist = stringArrayListExtra;
                if (stringArrayListExtra.size() != 0) {
                    listItems.add(0, new FileData(this.new_file, new SimpleDateFormat("yyyy.MM.dd").format(new Date()), new SimpleDateFormat("HH:mm:ss").format(new Date()), this.pathlist.get(0)));
                    listItems.get(0).addImages(this.pathlist);
                    Utils.writeGSON(listItems, this);
                    this.adapter.notifyDataSetChanged();
                    this.centerText.setVisibility(View.INVISIBLE);
                    Intent intent2 = new Intent(this, ScrollActivity.class);
                    intent2.putExtra("pathlist", this.pathlist);
                    intent2.putExtra("name", listItems.get(clickedFile).getName());
                    startActivity(intent2);
                }
                super.onActivityResult(i, i2, intent);
            } else if (i == this.PICK_PDF_FILE && i2 == -1) {
                if (intent != null) {
                    Uri data = intent.getData();
                    Log.i("MYTAG", "PDF Uri :" + data);
                    try {
                        getPdfFromUri(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                super.onActivityResult(i, i2, intent);
            } else {
                if (i == this.PICK_IMAGES && i2 == -1) {
                    try {
                        getImagesFromData(intent);
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                super.onActivityResult(i, i2, intent);
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    private void getImagesFromData(Intent intent) throws IOException {
        this.addLoadingBar.setVisibility(View.VISIBLE);
        this.uiHandler = new UiHandler(Looper.getMainLooper(), this);
        CustomThreadPoolManager customThreadPoolManager = CustomThreadPoolManager.getsInstance();
        this.mCustomThreadPoolManager = customThreadPoolManager;
        customThreadPoolManager.setUiThreadCallback(this);
        ImageImportCallable imageImportCallable = new ImageImportCallable(this, intent);
        imageImportCallable.setCustomThreadPoolManager(this.mCustomThreadPoolManager);
        this.mCustomThreadPoolManager.addCallable(imageImportCallable);
    }

    private void getPdfFromUri(Uri uri) throws IOException {
        this.addLoadingBar.setVisibility(View.VISIBLE);
        this.uiHandler = new UiHandler(Looper.getMainLooper(), this);
        CustomThreadPoolManager customThreadPoolManager = CustomThreadPoolManager.getsInstance();
        this.mCustomThreadPoolManager = customThreadPoolManager;
        customThreadPoolManager.setUiThreadCallback(this);
        PDFImportCallable pDFImportCallable = new PDFImportCallable(this, uri);
        pDFImportCallable.setCustomThreadPoolManager(this.mCustomThreadPoolManager);
        this.mCustomThreadPoolManager.addCallable(pDFImportCallable);
    }

    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_refer :
                startActivity(new Intent(FileActivity.this,AboutUsActivity.class));
                break;

            case R.id.nav_qr_code:
                    startActivity(new Intent(this, QrCodeActivity.class));
                break;

            case R.id.pdf_tool:
                startActivity(new Intent(this, TestActivity.class));
                break;
            case R.id.nav_settings :
                //startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.nav_tutorial :
                Intent intent3 = new Intent("android.intent.action.VIEW");
                intent3.setData(Uri.parse("http://tiny.cc/air_app_scanner_htu"));
                startActivity(intent3);
                break;

            case R.id.ocr_option:
                startActivity(new Intent(this,OcrActivity.class));
                break;
        }
        this.drawer.closeDrawer((int) GravityCompat.START);
        return true;
    }

    public void onBackPressed() {
        if (this.drawer.isDrawerOpen((int) GravityCompat.START)) {
            this.drawer.closeDrawer((int) GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
