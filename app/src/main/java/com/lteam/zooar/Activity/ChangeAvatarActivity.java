package com.lteam.zooar.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lteam.zooar.Constans;
import com.lteam.zooar.Image.CircleImage;
import com.lteam.zooar.Model.User;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.lteam.zooar.Server.DataClient;
import com.squareup.picasso.Picasso;

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

import static com.lteam.zooar.Ultils.UltilsLogin.getUser;
import static com.lteam.zooar.Ultils.UltilsLogin.saveUser;

public class ChangeAvatarActivity extends AppCompatActivity {

    @BindView(R.id.imgAvatarNew)
    CircleImage imgAvatarNew;
    @BindView(R.id.btnPostAvatar)
    ImageView btnPostAvatar;
    String real_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);
        ButterKnife.bind(this);

        Picasso.get()
                .load(APIUtils.base_url+getUser(this).getUrlAvatar())
                .error(R.drawable.shark)
                .into(imgAvatarNew);
    }

    @OnClick({R.id.imgAvatarNew, R.id.btnPostAvatar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgAvatarNew:
                //get ảnh
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Constans.Login.REQUEST_CODE_IMAGE_AVATAR_SIGN_UP);
                break;
            case R.id.btnPostAvatar:
                if(!real_path.equals("")) updateAvatar();
                else Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateAvatar() {
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
                    Call<String> call1=dataClient.changeAvatar("php/image/"+response.body(),getUser(ChangeAvatarActivity.this).getId());
                    call1.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> respons) {
                            if(!response.body().equals("0")){
                                Toast.makeText(ChangeAvatarActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
                                User user=getUser(ChangeAvatarActivity.this);
                                user.setUrlAvatar("php/image/"+response.body());
                                saveUser(ChangeAvatarActivity.this,user);

                                finish();
                            }else
                                Toast.makeText(ChangeAvatarActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(ChangeAvatarActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else Toast.makeText(ChangeAvatarActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ChangeAvatarActivity.this, R.string.fail_connect, Toast.LENGTH_SHORT).show();
                Log.e("ok",t.toString() );
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constans.Login.REQUEST_CODE_IMAGE_AVATAR_SIGN_UP && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            real_path = getRealPathFromURI(uri);
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgAvatarNew.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
