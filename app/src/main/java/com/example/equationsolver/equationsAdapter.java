package com.example.equationsolver;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class equationsAdapter extends RecyclerView.Adapter<equationsAdapter.equations> {
    DataDao dataDao;
    Context context;

    public equationsAdapter(DataDao dataDao , Context context) {
        this.dataDao = dataDao;
        this.context = context;
    }

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
        holder.deletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = holder.getAdapterPosition();


                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        dataDao.delete(itemsArrayList.get(i).getDid());
                        itemsArrayList.remove(i);
                        ((Activity)context).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                notifyItemRemoved(i);
                                notifyItemRangeChanged(i, itemsArrayList.size());
                                Toasty.success(context, " deleted ", Toast.LENGTH_SHORT, true).show();

                            }
                        });

                    }
                });


            }
        });

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
        private ImageButton deletBtn;

        public equations(@NonNull View itemView) {
            super(itemView);

            inputText = itemView.findViewById(R.id.inputText);
            resText = itemView.findViewById(R.id.resText);
            deletBtn = itemView.findViewById(R.id.deleteData);
        }

        @Override
        public void onClick(View v) {
        }
    }

}
