package com.example.letsscan.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letsscan.R;

import java.util.List;

public class L3RecyclerView extends RecyclerView.Adapter<L3RecyclerView.ProgrammingViewHolderSRV> {
    private List<String> synonyms;

    L3RecyclerView(List<String> list) {
        this.synonyms = list;
    }

    public ProgrammingViewHolderSRV onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ProgrammingViewHolderSRV(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.synonym_item_layout, viewGroup, false));
    }

    public void onBindViewHolder(ProgrammingViewHolderSRV programmingViewHolderSRV, int i) {
        programmingViewHolderSRV.synonymitem.setText(this.synonyms.get(i));
    }

    public int getItemCount() {
        return this.synonyms.size();
    }

    static class ProgrammingViewHolderSRV extends RecyclerView.ViewHolder {
        TextView synonymitem;

        ProgrammingViewHolderSRV(View view) {
            super(view);
            this.synonymitem = (TextView) view.findViewById(R.id.synonym_item);
        }
    }
}
