package com.example.letsscan;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letsscan.Crop.ScanUtils;
import com.example.letsscan.Dictionary.DictPagerActivity;
import com.example.letsscan.Functions.Utils;
import com.example.letsscan.Views.FileDialog;
import com.example.letsscan.recycler_views.FileData;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ScrollActivity extends AppCompatActivity implements FileDialog.FileDialogListener, ScrollBottomSheet.BottomSheetListener {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static int CAMERA_REQUEST_CODE = 1;
    public static int MODIFY_REQUEST_CODE = 2;
    LinearLayout btnSetTopRightLL;
    ScrollAdapter customAdapter;
    Button del_btn;
    String file_name = "Unknown";
    TextView file_name_tv;
    Button modify_btn;
    ArrayList<String> path_list = new ArrayList<>();
    ImageButton rename_btn;
    Button save_btn;
    Button select_all_btn;
    Button share_btn;
    ImageButton start_dict_btn;

    public void applyName(String str) {
    }

    public void onButtonClicked(String str, int i) {
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_scroll);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_scroll_act));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.path_list = getIntent().getStringArrayListExtra("pathlist");
        this.file_name = getIntent().getStringExtra("name");
        getIntent().removeExtra("pathlist");
        getIntent().removeExtra("name");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.adapterRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        ScrollAdapter scrollAdapter = new ScrollAdapter(this.path_list, this);
        this.customAdapter = scrollAdapter;
        recyclerView.setAdapter(scrollAdapter);
        this.file_name_tv = (TextView) findViewById(R.id.file_name_scroll_act);
        this.del_btn = (Button) findViewById(R.id.deleteBtnBBarScroll);
        this.share_btn = (Button) findViewById(R.id.share_toolbar_scroll);
        this.save_btn = (Button) findViewById(R.id.save_btn_scroll);
        this.rename_btn = (ImageButton) findViewById(R.id.rename_toolbar_scroll);
        this.btnSetTopRightLL = (LinearLayout) findViewById(R.id.LL_rightTopBarScroll);
        this.select_all_btn = (Button) findViewById(R.id.select_all_top_scroll);
        this.start_dict_btn = (ImageButton) findViewById(R.id.dictBtnScroll);
        this.modify_btn = (Button) findViewById(R.id.modifyScroll);
        this.file_name_tv.setText(this.file_name);
        this.rename_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new FileDialog("Rename", FileActivity.listItems.get(FileActivity.clickedFile).getName(), ScrollActivity.this.getApplicationContext()).show(ScrollActivity.this.getSupportFragmentManager(), "file dialog");
            }
        });
        this.del_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ScrollActivity.this.customAdapter.selectedPos.size() != 0) {
                    FileActivity.listItems.get(FileActivity.clickedFile).removeImages(ScrollActivity.this.customAdapter.selectedPos);
                    Utils.writeGSON(FileActivity.listItems, ScrollActivity.this.getApplicationContext());
                    Iterator<String> it = ScrollActivity.this.customAdapter.selectedPos.iterator();
                    while (it.hasNext()) {
                        String next = it.next();
                        Utils.delete_image(next, ScrollActivity.this.getApplicationContext());
                        ScrollActivity.this.customAdapter.path_lists.remove(next);
                    }
                    if (ScrollActivity.this.customAdapter.path_lists.size() == 0) {
                        FileActivity.listItems.remove(FileActivity.clickedFile);
                        Utils.writeGSON(FileActivity.listItems, ScrollActivity.this.getApplicationContext());
                        ScrollActivity.this.finish();
                    }
                    ScrollActivity.this.customAdapter.list_init();
                    ScrollActivity.this.customAdapter.notifyDataSetChanged();
                    ScrollActivity.this.hideBtn();
                    return;
                }
                Toast.makeText(ScrollActivity.this, R.string.select_one_image, Toast.LENGTH_LONG).show();
            }
        });
        this.share_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ScrollActivity.this.customAdapter.selectedPos.size() == 0) {
                    ScrollBottomSheet scrollBottomSheet = new ScrollBottomSheet(ScrollActivity.this.path_list, ScrollActivity.this.file_name, 3, ScrollActivity.this.getApplicationContext());
                    scrollBottomSheet.setCancelable(false);
                    scrollBottomSheet.show(ScrollActivity.this.getSupportFragmentManager(), "ScrollBottomSheet");
                } else {
                    ScrollBottomSheet scrollBottomSheet2 = new ScrollBottomSheet(ScrollActivity.this.customAdapter.selectedPos, ScrollActivity.this.file_name, 3, ScrollActivity.this.getApplicationContext());
                    scrollBottomSheet2.setCancelable(false);
                    scrollBottomSheet2.show(ScrollActivity.this.getSupportFragmentManager(), "ScrollBottomSheet");
                }
                ScrollActivity.this.hideBtn();
                ScrollActivity.this.customAdapter.selectedPos.clear();
                ScrollActivity.this.customAdapter.clear = true;
                ScrollActivity.this.customAdapter.notifyDataSetChanged();
            }
        });
        this.save_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ScrollActivity.this.customAdapter.selectedPos.size() == 0) {
                    ScrollBottomSheet scrollBottomSheet = new ScrollBottomSheet(ScrollActivity.this.path_list, ScrollActivity.this.file_name, 2, ScrollActivity.this.getApplicationContext());
                    scrollBottomSheet.show(ScrollActivity.this.getSupportFragmentManager(), "ScrollBottomSheet");
                    scrollBottomSheet.setCancelable(false);
                } else {
                    ScrollBottomSheet scrollBottomSheet2 = new ScrollBottomSheet(ScrollActivity.this.customAdapter.selectedPos, ScrollActivity.this.file_name, 2, ScrollActivity.this.getApplicationContext());
                    scrollBottomSheet2.show(ScrollActivity.this.getSupportFragmentManager(), "ScrollBottomSheet");
                    scrollBottomSheet2.setCancelable(false);
                }
                ScrollActivity.this.hideBtn();
                ScrollActivity.this.customAdapter.selectedPos.clear();
                ScrollActivity.this.customAdapter.clear = true;
                ScrollActivity.this.customAdapter.notifyDataSetChanged();
            }
        });
        this.select_all_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!ScrollActivity.this.customAdapter.selected_all) {
                    ScrollActivity.this.customAdapter.list_init();
                    ScrollActivity.this.customAdapter.selectedPos.addAll(ScrollActivity.this.path_list);
                    ScrollActivity.this.customAdapter.selected_all = true;
                    ScrollActivity.this.customAdapter.clear = false;
                    ScrollActivity.this.customAdapter.notifyDataSetChanged();
                    ScrollActivity.this.select_all_btn.setText(R.string.clear_all);
                } else if (!ScrollActivity.this.customAdapter.clear) {
                    ScrollActivity.this.customAdapter.selectedPos.clear();
                    ScrollActivity.this.customAdapter.selected_all = false;
                    ScrollActivity.this.customAdapter.clear = true;
                    ScrollActivity.this.customAdapter.notifyDataSetChanged();
                    ScrollActivity.this.select_all_btn.setText(R.string.elect_all);
                    ScrollActivity.this.hideBtn();
                }
            }
        });
        this.start_dict_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ScrollActivity.this, DictPagerActivity.class);
                intent.putExtra("pathlist", ScrollActivity.this.path_list);
                intent.putExtra("position", 0);
                ScrollActivity.this.startActivity(intent);
                Log.d("DICT", "Start dictpager");
            }
        });
        this.modify_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ArrayList arrayList = new ArrayList();
                Iterator<String> it = ScrollActivity.this.customAdapter.selectedPos.iterator();
                while (it.hasNext()) {
                    ScrollActivity scrollActivity = ScrollActivity.this;
                    arrayList.add(new ImageAttrs(ScanUtils.getOutlinePoints(new File(scrollActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.original/"), it.next()).getPath()), 0.0f, "original"));
                }
                boolean z = false;
                if (ScrollActivity.this.customAdapter.selectedPos.size() > 1) {
                    z = true;
                }
                Intent intent = new Intent(ScrollActivity.this, ImagePagerActivity.class);
                intent.putExtra("pathlist", new ArrayList(ScrollActivity.this.customAdapter.selectedPos));
                intent.putParcelableArrayListExtra("imageattrs", arrayList);
                intent.putExtra("batch_mode", z);
                intent.putExtra("modify_images", true);
                ScrollActivity.this.hideBtn();
                ScrollActivity.this.customAdapter.selectedPos.clear();
                ScrollActivity.this.customAdapter.clear = true;
                ScrollActivity.this.customAdapter.notifyDataSetChanged();
                ScrollActivity.this.startActivityForResult(intent, ScrollActivity.MODIFY_REQUEST_CODE);
            }
        });
    }

    public void hideBtn() {
        this.save_btn.setVisibility(View.VISIBLE);
        this.share_btn.setVisibility(View.VISIBLE);
        this.btnSetTopRightLL.setVisibility(View.VISIBLE);
        this.btnSetTopRightLL.setClickable(true);
        this.select_all_btn.setVisibility(View.GONE);
        this.select_all_btn.setClickable(false);
        this.start_dict_btn.setVisibility(View.VISIBLE);
        this.start_dict_btn.setClickable(true);
        this.modify_btn.setVisibility(View.GONE);
        this.modify_btn.setClickable(false);
    }

    public void showBtn() {
        this.save_btn.setVisibility(View.GONE);
        this.share_btn.setVisibility(View.GONE);
        this.btnSetTopRightLL.setVisibility(View.GONE);
        this.btnSetTopRightLL.setClickable(false);
        this.select_all_btn.setVisibility(View.VISIBLE);
        this.select_all_btn.setClickable(true);
        this.start_dict_btn.setVisibility(View.GONE);
        this.start_dict_btn.setClickable(false);
        this.modify_btn.setVisibility(View.VISIBLE);
        this.modify_btn.setClickable(true);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        try {
            if (i == CAMERA_REQUEST_CODE && i2 == -1 && intent != null) {
                final ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("pathlist");
                this.path_list.addAll(stringArrayListExtra);
                this.customAdapter.notifyDataSetChanged();
                AsyncTask.execute(new Runnable() {
                    public void run() {
                        FileActivity.listItems.get(FileActivity.clickedFile).addImages(stringArrayListExtra);
                        Utils.writeGSON(FileActivity.listItems, ScrollActivity.this.getApplicationContext());
                    }
                });
                super.onActivityResult(i, i2, intent);
            }
            if (i == MODIFY_REQUEST_CODE && i2 == -1 && intent != null) {
                this.customAdapter.notifyDataSetChanged();
            }
            super.onActivityResult(i, i2, intent);
        } catch (Exception unused) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    public void onBackPressed() {
        if (this.customAdapter.selectedPos.size() != 0) {
            hideBtn();
            this.customAdapter.selectedPos.clear();
            this.customAdapter.clear = true;
            this.customAdapter.notifyDataSetChanged();
            return;
        }
        super.onBackPressed();
    }

    public void applyRename(String str) {
        FileActivity.listItems.add(0, new FileData(str, new SimpleDateFormat("yyyy.MM.dd").format(new Date()), new SimpleDateFormat("HH:mm:ss").format(new Date()), FileActivity.listItems.get(FileActivity.clickedFile).getIconuri()));
        FileActivity.listItems.get(0).addImages(FileActivity.listItems.get(FileActivity.clickedFile + 1).getImages());
        FileActivity.listItems.remove(FileActivity.clickedFile + 1);
        Utils.writeGSON(FileActivity.listItems, this);
        this.file_name = str;
        this.file_name_tv.setText(str);
    }
}

