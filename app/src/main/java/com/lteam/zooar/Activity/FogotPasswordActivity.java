package com.lteam.zooar.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.lteam.zooar.Server.DataClient;

import java.util.FormatFlagsConversionMismatchException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FogotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.edtUserNameForgotPassword)
    EditText edtUserNameForgotPassword;
    @BindView(R.id.edtEmailForgotPassword)
    EditText edtEmailForgotPassword;
    @BindView(R.id.btnGetPass)
    ImageView btnGetPass;
    @BindView(R.id.txtPassWordForgotPassword)
    TextView txtPassWordForgotPassword;
    @BindView(R.id.register)
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fogot_password);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnGetPass, R.id.register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnGetPass:
                String userName = edtUserNameForgotPassword.getText().toString().trim();
                String email = edtEmailForgotPassword.getText().toString().trim();

                if (!userName.isEmpty() && !email.isEmpty()) {
                    DataClient dataClient = APIUtils.getData();
                    Call<String> callback = dataClient.getPassword(userName, email);
                    callback.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String pass = response.body();
                            if (pass != null) {
                                if (pass.equals("1"))
                                    txtPassWordForgotPassword.setText(R.string.checkemail);
                                else if (pass.equals("0"))
                                    txtPassWordForgotPassword.setText(R.string.wrongemail);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(FogotPasswordActivity.this, R.string.fail_connect, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(FogotPasswordActivity.this, R.string.empty, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register:
                Intent intent = new Intent(FogotPasswordActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
}
