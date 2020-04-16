package com.lteam.zooar.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lteam.zooar.Model.User;
import com.lteam.zooar.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lteam.zooar.Ultils.UltilsLogin.getUser;

public class OptionActivity extends AppCompatActivity {

    @BindView(R.id.btnChangePassword)
    Button btnChangePassword;
    @BindView(R.id.btnChangeAvatar)
    Button btnChangeAvatar;
    @BindView(R.id.btnUserManager)
    Button btnUserManager;
    @BindView(R.id.modelManager)
    Button modelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        ButterKnife.bind(this);

        User user=getUser(this);
        if(!user.getLevel().equals("2")){
            modelManager.setVisibility(View.INVISIBLE);
            btnUserManager.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick({R.id.btnChangePassword, R.id.btnChangeAvatar, R.id.btnUserManager, R.id.modelManager})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnChangePassword:
                Intent intent=new Intent(OptionActivity.this,ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.btnChangeAvatar:
                Intent intent1=new Intent(OptionActivity.this,ChangeAvatarActivity.class);
                startActivity(intent1);
                break;
            case R.id.btnUserManager:
                Intent intent2=new Intent(OptionActivity.this,UserManageActivity.class);
                startActivity(intent2);
                break;
            case R.id.modelManager:
                Intent intent3=new Intent(OptionActivity.this,ModelManagerActivity.class);
                startActivity(intent3);
                break;
        }
    }
}
