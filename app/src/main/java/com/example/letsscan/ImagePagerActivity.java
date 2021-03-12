package com.example.letsscan;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.letsscan.Crop.ScanUtils;
import com.example.letsscan.Dictionary.DictPagerActivity;
import com.example.letsscan.Functions.Utils;
import com.example.letsscan.ThreadPoolManager.CropSaveCallable;
import com.example.letsscan.ThreadPoolManager.CustomThreadPoolManager;
import com.example.letsscan.ThreadPoolManager.UiThreadCallback;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ImagePagerActivity extends AppCompatActivity implements UiThreadCallback {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    Button all_filter_btn;
    Button bw_filter_btn;
    Button delete_btn;
    Size dispSize = null;
    ImageButton done_btn;
    LinearLayout filterBar;
    Boolean filterBarOn = false;
    Button grey_filter_btn;
    /* access modifiers changed from: private */
    public ArrayList<ImageAttrs> imageAttrs;
    private CustomThreadPoolManager mCustomThreadPoolManager;
    Button magic_filter_btn;
    boolean modify_images = false;
    int no_ann = 0;
    Button original_filter_btn;
    TextView pageCount;
    ArrayList<String> pathlist;
    LinearLayout progress_bar_num;
    Button rotate_btn;
    Button select_all_btn;
    Size tvSize = null;
    private UiHandler uiHandler;
    /* access modifiers changed from: private */
    public ViewPager2 viewPager;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_image_pager);
        getWindow().setFlags(1024, 1024);
        this.pathlist = getIntent().getStringArrayListExtra("pathlist");
        this.imageAttrs = getIntent().getParcelableArrayListExtra("imageattrs");
        Log.d("CROPRECEIVED", String.valueOf(this.pathlist));
        Log.d("CROPRECEIVED", String.valueOf(this.imageAttrs.get(0).getCropPoints()));
        getIntent().removeExtra("imageattrs");
        getIntent().removeExtra("pathlist");
        final boolean booleanExtra = getIntent().getBooleanExtra("batch_mode", false);
        this.modify_images = getIntent().getBooleanExtra("modify_images", false);
        this.viewPager = (ViewPager2) findViewById(R.id.viewPagerImageSlider);
        this.delete_btn = (Button) findViewById(R.id.delete_btn);
        this.done_btn = (ImageButton) findViewById(R.id.doneBtnIP);
        this.rotate_btn = (Button) findViewById(R.id.rotate_btn);
        this.all_filter_btn = (Button) findViewById(R.id.all_filter_btn);
        this.bw_filter_btn = (Button) findViewById(R.id.bw_filter_btn);
        this.grey_filter_btn = (Button) findViewById(R.id.grey_filter_btn);
        this.magic_filter_btn = (Button) findViewById(R.id.magic_filter_btn);
        this.select_all_btn = (Button) findViewById(R.id.select_all_btn);
        this.original_filter_btn = (Button) findViewById(R.id.original_filter_btn);
        this.filterBar = (LinearLayout) findViewById(R.id.filterBar);
        this.pageCount = (TextView) findViewById(R.id.img_count_img_pager);
        this.progress_bar_num = (LinearLayout) findViewById(R.id.bar_image_pager);
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, this.pathlist, this.imageAttrs);
        Size displaySize = Utils.getDisplaySize(getWindowManager());
        this.dispSize = displaySize;
        this.tvSize = Utils.calculateTvSize(displaySize, new Rational(3, 4));
        this.viewPager.setLayoutParams(new FrameLayout.LayoutParams(this.tvSize.getWidth(), this.tvSize.getHeight(), 16));
        this.viewPager.setAdapter(viewPagerAdapter);
        this.viewPager.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ImagePagerActivity.this.hideFilterBar();
                Toast.makeText(ImagePagerActivity.this.getBaseContext(), "Tap on view pager", Toast.LENGTH_LONG).show();
            }
        });
        this.delete_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (booleanExtra) {
                    int currentItem = ImagePagerActivity.this.viewPager.getCurrentItem();
                    if (!ImagePagerActivity.this.modify_images) {
                        ImagePagerActivity imagePagerActivity = ImagePagerActivity.this;
                        Utils.delete_image(new File(imagePagerActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.original/"), ImagePagerActivity.this.pathlist.get(currentItem)).getAbsolutePath(), ImagePagerActivity.this.getBaseContext());
                    }
                    ImagePagerActivity.this.pathlist.remove(currentItem);
                    ImagePagerActivity.this.imageAttrs.remove(currentItem);
                    if (ImagePagerActivity.this.pathlist.size() == 0) {
                        ImagePagerActivity.this.finish();
                    }
                    viewPagerAdapter.notifyDataSetChanged();
                    ImagePagerActivity.this.viewPager.setCurrentItem(currentItem);
                    return;
                }
                if (!ImagePagerActivity.this.modify_images) {
                    ImagePagerActivity imagePagerActivity2 = ImagePagerActivity.this;
                    Utils.delete_image(new File(imagePagerActivity2.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.original/"), ImagePagerActivity.this.pathlist.get(0)).getAbsolutePath(), ImagePagerActivity.this.getBaseContext());
                }
                ImagePagerActivity.this.pathlist.clear();
                ImagePagerActivity.this.imageAttrs.clear();
                ImagePagerActivity.this.finish();
            }
        });
        this.done_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ImagePagerActivity.this.done_btn.setClickable(false);
                ImagePagerActivity.this.progress_bar_num.setVisibility(View.VISIBLE);
                ImagePagerActivity.this.saveAnnotatedImage();
            }
        });
        this.rotate_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int currentItem = ImagePagerActivity.this.viewPager.getCurrentItem();
                ((ImageAttrs) ImagePagerActivity.this.imageAttrs.get(currentItem)).setRot((((ImageAttrs) ImagePagerActivity.this.imageAttrs.get(currentItem)).getRot() + 90.0f) % 360.0f);
                viewPagerAdapter.notifyItemChanged(currentItem);
            }
        });
        this.all_filter_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!ImagePagerActivity.this.filterBarOn.booleanValue()) {
                    ImagePagerActivity.this.filterBar.setVisibility(View.VISIBLE);
                    ImagePagerActivity.this.filterBar.setClickable(true);
                    ImagePagerActivity.this.filterBarOn = true;
                    return;
                }
                ImagePagerActivity.this.hideFilterBar();
                ImagePagerActivity.this.filterBarOn = false;
            }
        });
        this.bw_filter_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                for (int i = 0; i < ImagePagerActivity.this.pathlist.size(); i++) {
                    ((ImageAttrs) ImagePagerActivity.this.imageAttrs.get(i)).setFilter("b&w");
                }
                viewPagerAdapter.notifyDataSetChanged();
                ImagePagerActivity.this.hideFilterBar();
            }
        });
        this.grey_filter_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                for (int i = 0; i < ImagePagerActivity.this.pathlist.size(); i++) {
                    ((ImageAttrs) ImagePagerActivity.this.imageAttrs.get(i)).setFilter("greyscale");
                }
                viewPagerAdapter.notifyDataSetChanged();
                ImagePagerActivity.this.hideFilterBar();
            }
        });
        this.magic_filter_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                for (int i = 0; i < ImagePagerActivity.this.pathlist.size(); i++) {
                    ((ImageAttrs) ImagePagerActivity.this.imageAttrs.get(i)).setFilter("magiccolor");
                }
                viewPagerAdapter.notifyDataSetChanged();
                ImagePagerActivity.this.hideFilterBar();
            }
        });
        this.original_filter_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                for (int i = 0; i < ImagePagerActivity.this.pathlist.size(); i++) {
                    ((ImageAttrs) ImagePagerActivity.this.imageAttrs.get(i)).setFilter("original");
                }
                viewPagerAdapter.notifyDataSetChanged();
                ImagePagerActivity.this.hideFilterBar();
            }
        });
        this.select_all_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int currentItem = ImagePagerActivity.this.viewPager.getCurrentItem();
                ImagePagerActivity imagePagerActivity = ImagePagerActivity.this;
                ((ImageAttrs) ImagePagerActivity.this.imageAttrs.get(currentItem)).setCropPoints(ScanUtils.getOutlinePoints(new File(imagePagerActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.original/"), ImagePagerActivity.this.pathlist.get(currentItem)).getPath()));
                viewPagerAdapter.notifyItemChanged(currentItem);
            }
        });
        this.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            public void onPageSelected(int i) {
                super.onPageSelected(i);
                TextView textView = ImagePagerActivity.this.pageCount;
                textView.setText("" + (i + 1) + "/" + ImagePagerActivity.this.pathlist.size());
            }
        });
    }

    /* access modifiers changed from: private */
    public void hideFilterBar() {
        this.filterBar.setVisibility(View.GONE);
        this.filterBar.setClickable(false);
    }

    public void saveAnnotatedImage() {
        Iterator<String> it = this.pathlist.iterator();
        while (it.hasNext()) {
            String next = it.next();
            Log.d("MYTAG", "Adding ImagePager callable for image @ " + next);
            CropSaveCallable cropSaveCallable = new CropSaveCallable(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/.original/", next, getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/.annotated/", this, this.imageAttrs.get(this.pathlist.indexOf(next)), this.tvSize);
            cropSaveCallable.setCustomThreadPoolManager(this.mCustomThreadPoolManager);
            this.mCustomThreadPoolManager.addCallable(cropSaveCallable);
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.uiHandler = new UiHandler(Looper.getMainLooper(), this);
        CustomThreadPoolManager customThreadPoolManager = CustomThreadPoolManager.getsInstance();
        this.mCustomThreadPoolManager = customThreadPoolManager;
        customThreadPoolManager.setUiThreadCallback(this);
    }

    public void disableUserInput() {
        this.viewPager.setUserInputEnabled(false);
    }

    public void enableUserInput() {
        this.viewPager.setUserInputEnabled(true);
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) "Discard ?");
        builder.setMessage((CharSequence) "Do you want to discard the images? ");
        builder.setPositiveButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton((CharSequence) "Discard", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!ImagePagerActivity.this.modify_images) {
                    Iterator<String> it = ImagePagerActivity.this.pathlist.iterator();
                    while (it.hasNext()) {
                        ImagePagerActivity imagePagerActivity = ImagePagerActivity.this;
                        Utils.delete_image(new File(imagePagerActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.original/"), it.next()).getAbsolutePath(), ImagePagerActivity.this.getBaseContext());
                    }
                }
                ImagePagerActivity.this.pathlist.clear();
                ImagePagerActivity.super.onBackPressed();
            }
        });
        builder.show();
    }

    public void polygonShift(int i, Map<Integer, PointF> map) {
        this.imageAttrs.get(i).setCropPoints(map);
    }

    public void barLimits(Message message) {
        if (message.getData().getString(NotificationCompat.CATEGORY_STATUS).equals("Success")) {
            int i = this.no_ann + 1;
            this.no_ann = i;
            Log.d("Ann", String.valueOf(i));
        }
        if (this.no_ann == this.pathlist.size()) {
            this.progress_bar_num.setVisibility(View.INVISIBLE);

            Intent intent = new Intent(this, DictPagerActivity.class);
            intent.putExtra("pathlist", this.pathlist);
            setResult(-1, intent);
            finish();
        }
    }

    public void publishToUiThread(Message message) {
        UiHandler uiHandler2 = this.uiHandler;
        if (uiHandler2 != null) {
            uiHandler2.sendMessage(message);
        }
    }

    private static class UiHandler extends Handler {
        private WeakReference<ImagePagerActivity> mTarget;

        public UiHandler(Looper looper, ImagePagerActivity imagePagerActivity) {
            super(looper);
            this.mTarget = new WeakReference<>(imagePagerActivity);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            ImagePagerActivity imagePagerActivity = (ImagePagerActivity) this.mTarget.get();
            if (imagePagerActivity != null) {
                imagePagerActivity.barLimits(message);
            }
        }
    }
}
