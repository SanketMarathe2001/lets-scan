package com.example.letsscan;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.signature.ObjectKey;
import com.example.letsscan.Dictionary.DictPagerActivity;

import java.io.File;
import java.util.ArrayList;
import org.opencv.videoio.Videoio;

public class ScrollAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int TYPE_FOOTER = 1;
    int TYPE_NORMAL = 2;
    boolean clear = false;
    Context context;
    ArrayList<String> path_lists;
    int select_all = 0;
    ArrayList<String> selectedPos = new ArrayList<>();
    boolean selected_all = false;

    public ScrollAdapter(ArrayList<String> arrayList, Context context2) {
        this.path_lists = arrayList;
        this.context = context2;
    }

    public void list_init() {
        this.selectedPos.clear();
    }

    public class ScrollItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconViewFile;
        public ImageView select_iv;
        public TextView textView;

        public ScrollItemViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.name);
            this.iconViewFile = (ImageView) view.findViewById(R.id.image);
            this.select_iv = (ImageView) view.findViewById(R.id.select_img_view);
        }
    }

    public class ScrollFooterViewHolder extends RecyclerView.ViewHolder {
        ImageButton addBtn;
        FrameLayout add_frame_layout;

        ScrollFooterViewHolder(View view) {
            super(view);
            this.addBtn = (ImageButton) view.findViewById(R.id.addImageScrollBtn);
            this.add_frame_layout = (FrameLayout) view.findViewById(R.id.scroll_add_frame_layout);
        }
    }

    public int getItemViewType(int i) {
        if (i == this.path_lists.size()) {
            return this.TYPE_FOOTER;
        }
        return this.TYPE_NORMAL;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == this.TYPE_NORMAL) {
            return new ScrollItemViewHolder(LayoutInflater.from(this.context).inflate(R.layout.scrollview_item, viewGroup, false));
        }
        return new ScrollFooterViewHolder(LayoutInflater.from(this.context).inflate(R.layout.add_scroll_layout, viewGroup, false));
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        if (getItemViewType(i) == this.TYPE_NORMAL) {
            Context context2 = this.context;
            File file = new File(context2.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.annotated/"), this.path_lists.get(i));
            ScrollItemViewHolder scrollItemViewHolder = (ScrollItemViewHolder) viewHolder;
            ((RequestBuilder) ((RequestBuilder) ((RequestBuilder) Glide.with(this.context).load(file).centerCrop()).signature(new ObjectKey(Long.valueOf(System.currentTimeMillis())))).override(Videoio.CAP_PROP_XI_CC_MATRIX_01, 640)).into(scrollItemViewHolder.iconViewFile);
            TextView textView = scrollItemViewHolder.textView;
            textView.setText("" + (i + 1));
            scrollItemViewHolder.select_iv.setVisibility(View.INVISIBLE);
            scrollItemViewHolder.iconViewFile.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (ScrollAdapter.this.selectedPos.size() == 0) {
                        Intent intent = new Intent(ScrollAdapter.this.context, DictPagerActivity.class);
                        intent.putExtra("pathlist", ScrollAdapter.this.path_lists);
                        intent.putExtra("position", i);
                        ScrollAdapter.this.context.startActivity(intent);
                        Log.d("DICT", "Start dictpager");
                    } else if (!ScrollAdapter.this.selectedPos.contains(ScrollAdapter.this.path_lists.get(i))) {
                        ((ScrollItemViewHolder) viewHolder).select_iv.setVisibility(View.VISIBLE);
                        ScrollAdapter.this.selectedPos.add(ScrollAdapter.this.path_lists.get(i));
                        Log.d("DICT", "Start dictpager 2");
                    } else {
                        ((ScrollItemViewHolder) viewHolder).select_iv.setVisibility(View.GONE);
                        ScrollAdapter.this.selectedPos.remove(ScrollAdapter.this.path_lists.get(i));
                        if (ScrollAdapter.this.selectedPos.size() == 0) {
                            ((ScrollActivity) ScrollAdapter.this.context).hideBtn();
                        }
                        Log.d("DICT", "Start dictpager 1");
                    }
                }
            });
            scrollItemViewHolder.iconViewFile.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View view) {
                    if (ScrollAdapter.this.selectedPos.size() == 0) {
                        ((ScrollActivity) ScrollAdapter.this.context).showBtn();
                        ((ScrollItemViewHolder) viewHolder).select_iv.setVisibility(View.VISIBLE);
                        ScrollAdapter.this.selectedPos.add(ScrollAdapter.this.path_lists.get(i));
                        Log.d("DICT", "Start dictpager 3");
                        return true;
                    }
                    Log.d("DICT", "Start Nothing");
                    return false;
                }
            });
            if (this.selected_all) {
                scrollItemViewHolder.select_iv.setVisibility(View.VISIBLE);
            }
            if (this.clear) {
                scrollItemViewHolder.select_iv.setVisibility(View.GONE);
                return;
            }
            return;
        }
        ScrollFooterViewHolder scrollFooterViewHolder = (ScrollFooterViewHolder) viewHolder;
        scrollFooterViewHolder.add_frame_layout.setLayoutParams(new FrameLayout.LayoutParams(Videoio.CAP_PROP_XI_CC_MATRIX_01, 640, 17));
        scrollFooterViewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((ScrollActivity) ScrollAdapter.this.context).startActivityForResult(new Intent((ScrollActivity) ScrollAdapter.this.context, CameraActivity.class), ScrollActivity.CAMERA_REQUEST_CODE);
            }
        });
    }

    public int getItemCount() {
        return this.path_lists.size() + 1;
    }
}
