package com.example.letsscan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.Transformation;
import com.example.letsscan.Crop.FilterTransform;
import com.example.letsscan.Crop.MathUtils;
import com.example.letsscan.Crop.PolygonView;
import com.example.letsscan.Functions.PreProcess;

import java.io.File;
import java.util.ArrayList;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    Context context;
    private ArrayList<ImageAttrs> imageAttrs;
    private ArrayList<String> path_list;
    public int i2,i3;

    public ViewPagerAdapter(Context context2, ArrayList<String> arrayList, ArrayList<ImageAttrs> arrayList2) {
        this.context = context2;
        this.path_list = arrayList;
        this.imageAttrs = arrayList2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_pager_single, viewGroup, false));
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        FrameLayout.LayoutParams layoutParams;
        int i4;
        int min;
        ViewHolder viewHolder2 = viewHolder;
        int i5 = i;
        Context context2 = this.context;
        File file = new File(context2.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/.original/"), this.path_list.get(i5));
        Log.d("NewD", String.valueOf(file));
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        int dimension = (int) this.context.getResources().getDimension(R.dimen.scanPadding);
        int dimension2 = (int) this.context.getResources().getDimension(R.dimen.circleSize);
        Matrix matrix = new Matrix();
        Size bitmapSizeCalc = PreProcess.bitmapSizeCalc(file.getPath());
        int width = bitmapSizeCalc.getWidth();
        int height = bitmapSizeCalc.getHeight();
        float rot = (this.imageAttrs.get(i5).getRot() + ((float) PreProcess.convertX(this.context, file.getPath()))) % 360.0f;
        Log.d("MYROT", String.valueOf(this.imageAttrs.get(i5).getRot()));
        if (rot == 90.0f || rot == 270.0f) {
            float f = (float) width;
            float f2 = (float) height;
            float f3 = f / f2;
            if (((double) f3) > 1.3333333333333333d) {
                min = ((displayMetrics.widthPixels - (dimension * 4)) * 4) / 3;
                i4 = (int) Math.min(((double) (min * 3)) / 4.0d, (double) (((float) min) / f3));
            } else {
                int i6 = displayMetrics.widthPixels - (dimension * 4);
                i4 = i6;
                min = (int) Math.min(((double) (i6 * 4)) / 3.0d, (double) (f3 * ((float) i6)));
            }
            float f4 = (float) i3;
            float f5 = (float) i2;
            matrix.postScale(f4 / f, f5 / f2);
            int i7 = (i2 / 2) - (i3 / 2);
            matrix.postTranslate((float) i7, (float) (-i7));
            matrix.postRotate(rot, f5 / 2.0f, f4 / 2.0f);
            layoutParams = new FrameLayout.LayoutParams(i2 + dimension2, dimension2 + i3);
        } else {
            float f6 = (float) height;
            float f7 = (float) width;
            float f8 = f6 / f7;
            if (((double) f8) > 1.3333333333333333d) {
                i3 = ((displayMetrics.widthPixels - (dimension * 4)) * 4) / 3;
                i2 = (int) Math.min(((double) (i3 * 3)) / 4.0d, (double) (((float) i3) / f8));
            } else {
                i2 = displayMetrics.widthPixels - (dimension * 4);
                i3 = (int) Math.min(((double) (i2 * 4)) / 3.0d, (double) (f8 * ((float) i2)));
            }
            float f9 = (float) i2;
            float f10 = (float) i3;
            matrix.postScale(f9 / f7, f10 / f6);
            matrix.postRotate(rot, f9 / 2.0f, f10 / 2.0f);
            layoutParams = new FrameLayout.LayoutParams(i2 + dimension2, dimension2 + i3);
        }
        viewHolder2.polygonView.polyMat = matrix;
        matrix.invert(viewHolder2.polygonView.inversePolyMat);
        viewHolder2.polygonView.position = i5;
        viewHolder2.polygonView.setPoints(MathUtils.applyMatrix(this.imageAttrs.get(i5).getCropPoints(), matrix));
        viewHolder2.polygonView.setVisibility(View.VISIBLE);
        viewHolder2.polygonView.setLayoutParams(layoutParams);
        layoutParams.gravity = 17;
        viewHolder2.imageView.setLayoutParams(new FrameLayout.LayoutParams(i2, i3, 17));
        ((RequestBuilder) Glide.with(this.context).load(file).transform((Transformation<Bitmap>) new FilterTransform(this.imageAttrs.get(i5).getFilter(), this.imageAttrs.get(i5).getRot()))).into(viewHolder2.imageView);
    }

    public int getItemCount() {
        return this.path_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public PolygonView polygonView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.imageviewpager);
            this.polygonView = (PolygonView) view.findViewById(R.id.polygonView);
        }
    }

    public void onViewRecycled(ViewHolder viewHolder) {
        Glide.with(this.context).clear((View) viewHolder.imageView);
        super.onViewRecycled(viewHolder);
    }
}
