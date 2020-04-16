package com.lteam.zooar.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.lteam.zooar.Adapter.AdapterAccount;
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

public class UserManageActivity extends AppCompatActivity {

    @BindView(R.id.rcvAccount)
    RecyclerView rcvAccount;

    ArrayList<User> users=new ArrayList<>();
    AdapterAccount adapterAccount;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage);
        ButterKnife.bind(this);

        adapterAccount=new AdapterAccount(users,UserManageActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvAccount.setLayoutManager(linearLayoutManager);
        rcvAccount.setAdapter(adapterAccount);

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
        Call<ArrayList<User>> call=dataClient.getAllUser();
        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if(!response.body().equals("0")){
                    users.clear();
                    users.addAll(response.body());
                    adapterAccount.notifyDataSetChanged();
                    mWaveSwipeRefreshLayout.setRefreshing(false);
                }else Toast.makeText(UserManageActivity.this, R.string.wrong,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                Toast.makeText(UserManageActivity.this, R.string.fail_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
