package com.example.equationsolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class History extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.dataRecyclar);
        textView = findViewById(R.id.textViewNo);


        AppDatabase db = AppDatabase.getInstance(this);
        DataDao dataDao = db.dataDao();

        final equationsAdapter adapter = new equationsAdapter(dataDao , this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);


        dataDao.getAll().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Data>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Data> data) {

                        adapter.setList(data);
                        adapter.notifyDataSetChanged();
                        if(data.isEmpty()){
                            textView.setVisibility(View.VISIBLE);
                        }else {
                            textView.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

}