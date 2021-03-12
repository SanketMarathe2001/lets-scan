package com.example.letsscan.Dictionary;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letsscan.R;
import com.example.letsscan.Views.CustomView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DictPagerAdapter extends RecyclerView.Adapter<DictPagerAdapter.ViewHolder> {
    private Context context;
    public Boolean dictEnabled;
    private DiffUtil.ItemCallback<VisionAPIData> diffCallback;
    private AsyncListDiffer<VisionAPIData> mDiffer;

    DictPagerAdapter(Context context2, Boolean bool) {
        DiffUtil.ItemCallback r0 = new DiffUtil.ItemCallback<VisionAPIData>() {
            public boolean areItemsTheSame(VisionAPIData visionAPIData, VisionAPIData visionAPIData2) {
                return visionAPIData.ID == visionAPIData2.ID;
            }

            public boolean areContentsTheSame(VisionAPIData visionAPIData, VisionAPIData visionAPIData2) {
                return visionAPIData.display == visionAPIData2.display;
            }

            public Object getChangePayload(VisionAPIData visionAPIData, VisionAPIData visionAPIData2) {
                Bundle bundle = new Bundle();
                if (!visionAPIData.path.equals(visionAPIData2.path)) {
                    return null;
                }
                if (visionAPIData.display != visionAPIData2.display) {
                    bundle.putParcelableArrayList("Rectangles", visionAPIData2.rectangles);
                    bundle.putStringArrayList("Words", visionAPIData2.words);
                    bundle.putBoolean("Display", visionAPIData2.display);
                }
                if (bundle.size() == 0) {
                    return null;
                }
                return bundle;
            }
        };
        this.diffCallback = r0;
        this.context = context2;
        this.mDiffer = new AsyncListDiffer<>((RecyclerView.Adapter) this, r0);
        this.dictEnabled = bool;
    }

    public int getItemCount() {
        return this.mDiffer.getCurrentList().size();
    }

    public void submitList(List<VisionAPIData> list) {
        this.mDiffer.submitList(list);
    }

    public VisionAPIData getItem(int i) {
        return this.mDiffer.getCurrentList().get(i);
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dict_pager_single, viewGroup, false));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CustomView customView;

        public ViewHolder(View view) {
            super(view);
            this.customView = (CustomView) view.findViewById(R.id.dict_pager_customview);
        }
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i, List<Object> list) {
        Log.d("TAGY", "Payload");
        if (!list.isEmpty()) {
            Log.d("TAGY", "Payload 1");
            Bundle bundle = (Bundle) list.get(0);
            if (viewHolder.customView.visionApiStatus) {
                Log.d("TAGY", "Payload 2");
                viewHolder.customView.displayBox = bundle.getBoolean("Display");
                viewHolder.customView.invalidate();
                return;
            }
            Log.d("TAGY", "Payload 3");
            ArrayList parcelableArrayList = bundle.getParcelableArrayList("Rectangles");
            ArrayList<String> stringArrayList = bundle.getStringArrayList("Words");
            viewHolder.customView.visionApiStatus = true;
            viewHolder.customView.words = stringArrayList;
            viewHolder.customView.rectangles = parcelableArrayList;
            viewHolder.customView.displayBox = true;
            viewHolder.customView.invalidate();
            return;
        }
        Log.d("TAGY", "Payload pass");
        super.onBindViewHolder(viewHolder, i, list);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Log.d("TAGY", "Normal");
        VisionAPIData item = getItem(i);
        Context context2 = this.context;
        viewHolder.customView.imageTrigger(new File(context2.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.annotated/"), item.path).getAbsolutePath(), item.words);
    }
}
