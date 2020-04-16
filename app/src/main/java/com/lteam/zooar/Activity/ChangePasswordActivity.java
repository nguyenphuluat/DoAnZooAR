package com.lteam.zooar.Activity;

import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lteam.zooar.Model.User;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.lteam.zooar.Server.DataClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lteam.zooar.Ultils.UltilsLogin.getUser;
import static com.lteam.zooar.Ultils.UltilsLogin.saveUser;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.edtOldPassWord)
    EditText edtOldPassWord;
    @BindView(R.id.edtNewPassWord)
    EditText edtNewPassWord;
    @BindView(R.id.edtNewPassWord2)
    EditText edtNewPassWord2;
    @BindView(R.id.btnChangePassword)
    ImageView btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnChangePassword)
    public void onViewClicked() {
        String oldpass=edtOldPassWord.getText().toString().trim();
        if(oldpass.equals(getUser(this).getPassword())){
            String newpass=edtNewPassWord.getText().toString();
            String newpass2=edtNewPassWord2.getText().toString();
            if(newpass.length()>0 && newpass2.length()>0 && newpass.equals(newpass2)){
                DataClient dataClient= APIUtils.getData();
                Call<String> call=dataClient.changePass(newpass,getUser(this).getId());
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().equals("1")){
                            Toast.makeText(ChangePasswordActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
                            User user=getUser(ChangePasswordActivity.this);
                            user.setPassword(newpass);
                            saveUser(ChangePasswordActivity.this,user);
                            finish();
                        }else Toast.makeText(ChangePasswordActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(ChangePasswordActivity.this, R.string.fail_connect, Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, R.string.wrong_account, Toast.LENGTH_SHORT).show();
        }
    }
}
