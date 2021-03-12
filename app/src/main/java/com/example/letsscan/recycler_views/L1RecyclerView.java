package com.example.letsscan.recycler_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letsscan.R;

import java.util.List;

public class L1RecyclerView extends RecyclerView.Adapter<L1RecyclerView.ProgrammingViewHolder> {
    private List<L2DictData> l2DictData;
    private Context main_context;

    public L1RecyclerView(List<L2DictData> list, Context context) {
        this.main_context = context;
        this.l2DictData = list;
    }

    public ProgrammingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ProgrammingViewHolder(LayoutInflater.from(this.main_context).inflate(R.layout.list_item_layout, viewGroup, false));
    }

    public void onBindViewHolder(ProgrammingViewHolder programmingViewHolder, int i) {
        programmingViewHolder.pos.setText(this.l2DictData.get(i).partOfSpeech);
        programmingViewHolder.def_ex_rview.setLayoutManager(new LinearLayoutManager(this.main_context));
        programmingViewHolder.def_ex_rview.setAdapter(new L2RecyclerView(this.l2DictData.get(i).l3DictData, this.main_context));
    }

    public int getItemCount() {
        return this.l2DictData.size();
    }

    static class ProgrammingViewHolder extends RecyclerView.ViewHolder {
        RecyclerView def_ex_rview;
        TextView pos;

        ProgrammingViewHolder(View view) {
            super(view);
            this.def_ex_rview = (RecyclerView) view.findViewById(R.id.def_ex_rview);
            this.pos = (TextView) view.findViewById(R.id.pos);
        }
    }
}
