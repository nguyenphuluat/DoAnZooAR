package com.lteam.zooar.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lteam.zooar.Constans;
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

import static com.lteam.zooar.Ultils.UltilsLogin.saveStatusUserLogin;
import static com.lteam.zooar.Ultils.UltilsLogin.saveUser;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edtUserNameLogin)
    EditText edtUserNameLogin;
    @BindView(R.id.edtPassWordLogin)
    EditText edtPassWordLogin;
    @BindView(R.id.btnLogin)
    ImageView btnLogin;
    @BindView(R.id.register)
    TextView register;
    @BindView(R.id.txtForgotPassword)
    TextView txtForgotPassword;
    Intent intent=new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnLogin, R.id.register, R.id.txtForgotPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                String username=edtUserNameLogin.getText().toString().trim();
                String pass=edtPassWordLogin.getText().toString().trim();
                if(username.length()>0 && pass.length()>0){
                    DataClient dataClient= APIUtils.getData();
                    Call<User> call=dataClient.login(username,pass);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if(!response.body().equals("0")){
                                User user=response.body();
                                // đúng tài khoan mật khẩu
                                saveUser(LoginActivity.this,user);
                                saveStatusUserLogin(true,LoginActivity.this);
                                //intent.putExtra(Constans.Login.DATA_RESULT_USER_LOGIN,user);
                                setResult(RESULT_OK,intent);
                                finish();
                            }else Toast.makeText(LoginActivity.this, R.string.wrong_account, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, R.string.wrong_account, Toast.LENGTH_SHORT).show();
                        }
                    });
                }else Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show();
                break;
            case R.id.register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.txtForgotPassword:
                Intent intent1=new Intent(LoginActivity.this,FogotPasswordActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
