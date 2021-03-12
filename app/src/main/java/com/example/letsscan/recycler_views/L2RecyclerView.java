package com.example.letsscan.recycler_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letsscan.R;

import java.util.List;

public class L2RecyclerView extends RecyclerView.Adapter<L2RecyclerView.ProgrammingViewHolderDERV> {
    List<L3DictData> l3DictData;
    Context main_context;

    L2RecyclerView(List<L3DictData> list, Context context) {
        this.l3DictData = list;
        this.main_context = context;
    }

    public ProgrammingViewHolderDERV onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ProgrammingViewHolderDERV(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.def_ex_syn_item_layout, viewGroup, false));
    }

    public void onBindViewHolder(ProgrammingViewHolderDERV programmingViewHolderDERV, int i) {
        TextView textView = programmingViewHolderDERV.def_number;
        textView.setText((i + 1) + ".");
        programmingViewHolderDERV.definition.setText(this.l3DictData.get(i).definition);
        programmingViewHolderDERV.example.setText(this.l3DictData.get(i).example);
        if (!this.l3DictData.get(i).example_available) {
            programmingViewHolderDERV.example.setHeight(0);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.main_context);
        linearLayoutManager.setOrientation(0);
        programmingViewHolderDERV.synonym_rview.setLayoutManager(linearLayoutManager);
        programmingViewHolderDERV.synonym_rview.setAdapter(new L3RecyclerView(this.l3DictData.get(i).synonyms));
        if (!this.l3DictData.get(i).synonyms_available) {
            programmingViewHolderDERV.synonym_rview.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1.0f));
        }
    }

    public int getItemCount() {
        return this.l3DictData.size();
    }

    static class ProgrammingViewHolderDERV extends RecyclerView.ViewHolder {
        TextView def_number;
        TextView definition;
        TextView example;
        RecyclerView synonym_rview;

        ProgrammingViewHolderDERV(View view) {
            super(view);
            this.def_number = (TextView) view.findViewById(R.id.def_number);
            this.definition = (TextView) view.findViewById(R.id.definition);
            this.example = (TextView) view.findViewById(R.id.example);
            this.synonym_rview = (RecyclerView) view.findViewById(R.id.synonym_rview);
        }
    }
}
