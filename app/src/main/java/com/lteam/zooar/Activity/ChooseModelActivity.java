package com.lteam.zooar.Activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.lteam.zooar.Adapter.AdapterPage;
import com.lteam.zooar.Model.Animal;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.lteam.zooar.Server.DataClient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lteam.zooar.Ultils.UltilsLogin.getUser;

public class ChooseModelActivity extends AppCompatActivity {
    ArrayList<Animal> animals;
    @BindView(R.id.viewPagerChooseAnimal)
    ViewPager viewPagerChooseAnimal;
    AdapterPage pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_model);
        ButterKnife.bind(this);
        getAnimal();
        FragmentManager fragmentManager = getSupportFragmentManager();
        pageAdapter = new AdapterPage(fragmentManager, animals);
        viewPagerChooseAnimal.setAdapter(pageAdapter);
    }

    private void getAnimal() {
        animals = new ArrayList<>();
        DataClient dataClient = APIUtils.getData();
        Call<ArrayList<Animal>> callBack = dataClient.getModelMove(getUser(this).getLevel());
        callBack.enqueue(new Callback<ArrayList<Animal>>() {
            @Override
            public void onResponse(Call<ArrayList<Animal>> call, Response<ArrayList<Animal>> response) {
                if(!response.body().equals("0")){
                    animals.addAll(response.body());
                    pageAdapter.notifyDataSetChanged();
                }else Toast.makeText(ChooseModelActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ArrayList<Animal>> call, Throwable t) {
                Toast.makeText(ChooseModelActivity.this, R.string.fail_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
