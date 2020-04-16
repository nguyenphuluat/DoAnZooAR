package com.lteam.zooar.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lteam.zooar.Constans;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.lteam.zooar.Server.DataClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.imgAvatarRegist)
    ImageView imgAvatarRegist;
    @BindView(R.id.editTextUserName)
    EditText editTextUserName;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.imgviewRegist)
    ImageView imgviewRegist;
    String real_path = "";
//    @BindView(R.id.edtPhoneSignUp)
//    EditText edtPhoneSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.imgAvatarRegist, R.id.imgviewRegist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgAvatarRegist:
                //get ảnh
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Constans.Login.REQUEST_CODE_IMAGE_AVATAR_SIGN_UP);
                break;
            case R.id.imgviewRegist:
                String username = editTextUserName.getText().toString().trim();
                String pass = editTextPassword.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String display = editTextName.getText().toString().trim();
                //String phone=edtPhoneSignUp.getText().toString().trim();

                if(!username.isEmpty() && !pass.isEmpty() && !email.isEmpty() && !display.isEmpty()){
                    File file = new File(real_path);
                    String file_path = file.getAbsolutePath();
                    String[] mangTenFile = file_path.split("\\.");

                    file_path = "";
                    if (mangTenFile.length >= 2) {
                        for (int i = 0; i < mangTenFile.length - 1; i++) {
                            file_path += mangTenFile[i];
                        }
                    }

                    file_path += System.currentTimeMillis() + "." + mangTenFile[mangTenFile.length - 1];
                    file_path=file_path.replace(" ","");
                    file_path=file_path.replace("[","");
                    file_path=file_path.replace("]","");

                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("upload_file", file_path, requestBody);
                    DataClient dataClient= APIUtils.getData();
                    Call<String> call=dataClient.uploadImageAvatar(body);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(!response.body().equals("0")){
                                String mes=response.body();
                                Call<String> call2=dataClient.regist(username,pass,"php/image/" + mes,display,email,"0");
                                call2.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> respons) {
                                        if(respons.body().equals("1")){
                                            Toast.makeText(RegisterActivity.this, R.string.access, Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else if(respons.body().equals("0")) Toast.makeText(RegisterActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
                                        else if(respons.body().equals("2")) Toast.makeText(RegisterActivity.this,R.string.duplicate, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(RegisterActivity.this, R.string.fail_connect, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else
                                Toast.makeText(RegisterActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(RegisterActivity.this,R.string.fail_connect, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constans.Login.REQUEST_CODE_IMAGE_AVATAR_SIGN_UP && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            real_path = getRealPathFromURI(uri);
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgAvatarRegist.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }
}
