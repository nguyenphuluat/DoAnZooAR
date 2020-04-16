package com.lteam.zooar.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.ar.sceneform.animation.ModelAnimator;
import com.lteam.zooar.Adapter.AdapterAccount;
import com.lteam.zooar.Adapter.AdapterModel;
import com.lteam.zooar.Model.Animal;
import com.lteam.zooar.Model.User;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.lteam.zooar.Server.DataClient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModelManagerActivity extends AppCompatActivity {
    @BindView(R.id.rcvAccount)
    RecyclerView rcvAccount;

    ArrayList<Animal> model=new ArrayList<>();
    AdapterModel adapterModel;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage);
        ButterKnife.bind(this);

        adapterModel=new AdapterModel(model,ModelManagerActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvAccount.setLayoutManager(linearLayoutManager);
        rcvAccount.setAdapter(adapterModel);

        loadAccount();

        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                // Do work to refresh the list here.
                loadAccount();
            }
        });
    }

    private void loadAccount() {
        DataClient dataClient= APIUtils.getData();
        Call<ArrayList<Animal>> call=dataClient.getModel("2");
        call.enqueue(new Callback<ArrayList<Animal>>() {
            @Override
            public void onResponse(Call<ArrayList<Animal>> call, Response<ArrayList<Animal>> response) {
                if(!response.body().equals("0")){
                    model.addAll(response.body());
                    adapterModel.notifyDataSetChanged();
                    mWaveSwipeRefreshLayout.setRefreshing(false);
                }else Toast.makeText(ModelManagerActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ArrayList<Animal>> call, Throwable t) {
                Toast.makeText(ModelManagerActivity.this, R.string.fail_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
