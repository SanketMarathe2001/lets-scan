package com.example.letsscan.recycler_views;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.letsscan.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.opencv.videoio.Videoio;

public class FilesRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int TYPE_HEADER = 1;
    int TYPE_NORMAL = 2;
    int checkedPosition = 0;
    private Context context;
    public ArrayList<FileData> listItems;
    /* access modifiers changed from: private */
    public OnClickThumbListener mOnClickThumbListener;
    String wish;

    public interface OnClickThumbListener {
        void onClickFile(int i);

        void onfileSelect(int i);
    }

    public FilesRecyclerView(ArrayList<FileData> arrayList, Context context2, Activity activity, String str) {
        this.listItems = arrayList;
        this.context = context2;
        this.mOnClickThumbListener = (OnClickThumbListener) activity;
        this.wish = str;
        setResources(context2);
    }

    public static void setResources(Context context2) {
        Locale locale;
        locale = new Locale("en");
        Resources resources = context2.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, (DisplayMetrics) null);
    }

    public class FileItemViewHolder extends RecyclerView.ViewHolder {
        public ImageButton dropDownBtn;
        public ImageView iconViewFile;
        public LinearLayout linearLayout;
        public TextView textViewDate;
        public TextView textViewFile;
        public TextView textViewPageCount;

        public FileItemViewHolder(View view) {
            super(view);
            this.textViewFile = (TextView) view.findViewById(R.id.fileName);
            this.textViewDate = (TextView) view.findViewById(R.id.fileDate);
            this.textViewPageCount = (TextView) view.findViewById(R.id.pg_count_file_act);
            this.iconViewFile = (ImageView) view.findViewById(R.id.fileImage);
            this.dropDownBtn = (ImageButton) view.findViewById(R.id.dropdown_menu);
            this.linearLayout = (LinearLayout) view.findViewById(R.id.fileLinearLayout);
        }
    }

    public class FileHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView wishTV;

        FileHeaderViewHolder(View view) {
            super(view);
            this.wishTV = (TextView) view.findViewById(R.id.homeWish);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == this.TYPE_NORMAL) {
            return new FileItemViewHolder(LayoutInflater.from(this.context).inflate(R.layout.one_file_view, viewGroup, false));
        }
        return new FileHeaderViewHolder(LayoutInflater.from(this.context).inflate(R.layout.wishes_file_activity, viewGroup, false));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        if (getItemViewType(i) == this.TYPE_NORMAL) {
            FileData fileData = this.listItems.get(i - 1);
            FileItemViewHolder fileItemViewHolder = (FileItemViewHolder) viewHolder;
            fileItemViewHolder.textViewFile.setText(fileData.getName());
            fileItemViewHolder.textViewDate.setText(fileData.getDate());
            TextView textView = fileItemViewHolder.textViewPageCount;
            textView.setText("" + fileData.getImages().size());
            Context context2 = this.context;
            ((RequestBuilder) ((RequestBuilder) Glide.with(this.context).load(new File(context2.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.annotated/"), fileData.getIconuri())).override(Videoio.CAP_PROP_XI_BUFFER_POLICY, 335)).centerCrop()).into(fileItemViewHolder.iconViewFile);
            fileItemViewHolder.iconViewFile.setClipToOutline(true);
            fileItemViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    FilesRecyclerView.this.mOnClickThumbListener.onClickFile(i - 1);
                }
            });
            fileItemViewHolder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View view) {
                    FilesRecyclerView.this.mOnClickThumbListener.onfileSelect(i - 1);
                    return true;
                }
            });
            fileItemViewHolder.dropDownBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    FilesRecyclerView.this.mOnClickThumbListener.onfileSelect(i - 1);
                }
            });
            return;
        }
        ((FileHeaderViewHolder) viewHolder).wishTV.setText(this.wish);
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return this.TYPE_HEADER;
        }
        return this.TYPE_NORMAL;
    }

    public int getItemCount() {
        return this.listItems.size() + 1;
    }

    public void filterList(ArrayList<FileData> arrayList) {
        this.listItems = arrayList;
        notifyDataSetChanged();
    }
}
