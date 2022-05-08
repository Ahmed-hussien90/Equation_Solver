package com.example.equationsolver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class equationsAdapter extends RecyclerView.Adapter<equationsAdapter.equations> {

    List<Data> itemsArrayList = new ArrayList<>();

    @NonNull
    @Override
    public equations onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new equations(LayoutInflater.from(parent.getContext()).inflate(R.layout.data_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull equations holder, int position) {

        holder.inputText.setText(itemsArrayList.get(position).getInputText());
        holder.resText.setText(itemsArrayList.get(position).getSolutionText());

    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public void setList(List<Data> itemsArraylist) {
        this.itemsArrayList = itemsArraylist;
        notifyDataSetChanged();
    }

    public class equations extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView inputText, resText;

        public equations(@NonNull View itemView) {
            super(itemView);

            inputText = itemView.findViewById(R.id.inputText);
            resText = itemView.findViewById(R.id.resText);

        }

        @Override
        public void onClick(View v) {
        }
    }
}
