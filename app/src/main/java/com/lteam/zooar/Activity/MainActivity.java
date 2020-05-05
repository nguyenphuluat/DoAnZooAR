package com.lteam.zooar.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lteam.zooar.Constans;
import com.lteam.zooar.Image.CircleImage;
import com.lteam.zooar.Model.User;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lteam.zooar.Ultils.UltilsLogin.getStattusUserLogin;
import static com.lteam.zooar.Ultils.UltilsLogin.getUser;
import static com.lteam.zooar.Ultils.UltilsLogin.saveStatusUserLogin;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    @BindView(R.id.bmb2)
    BoomMenuButton bmb2;
    @BindView(R.id.imgAvatar)
    CircleImage imgAvatar;
    @BindView(R.id.txtTen)
    TextView txtTen;
    @BindView(R.id.bmb3)
    BoomMenuButton bmb3;
    @BindView(R.id.shimmer_tv)
    ShimmerTextView shimmerTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bmb2.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        bmb2.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);

        for (int i = 0; i < bmb2.getPiecePlaceEnum().pieceNumber(); i++) {
            bmb2.addBuilder(BuilderManager.getHamButtonBuilderWithDifferentPieceColor(i).listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    switch (index) {
                        case 0:
                            startActivity(new Intent(MainActivity.this, ZooSceneformActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(MainActivity.this, ChooseModelActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(MainActivity.this, com.lteam.zooar.CloudAnchor.MainActivity.class));
                            break;
                        case 3:
                            logout();
                            break;
                    }
                }
            }));
        }
        bmb2.setAutoHide(false);
        bmb2.setAutoBoom(true);

        bmb3.setPiecePlaceEnum(PiecePlaceEnum.DOT_2_1);
        bmb3.setButtonPlaceEnum(ButtonPlaceEnum.SC_2_1);
        if (getStattusUserLogin(this) && getUser(this).getLevel().equals("2")) {
            bmb3.setPiecePlaceEnum(PiecePlaceEnum.DOT_4_1);
            bmb3.setButtonPlaceEnum(ButtonPlaceEnum.SC_4_1);
        }
        for (int i = 0; i < bmb3.getPiecePlaceEnum().pieceNumber(); i++)
            bmb3.addBuilder(BuilderManager.getTextOutsideCircleButtonBuilder(i).listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    switch (index) {
                        case 0:
                            startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(MainActivity.this, ChangeAvatarActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(MainActivity.this, UserManageActivity.class));
                            break;
                        case 3:
                            startActivity(new Intent(MainActivity.this, ModelManagerActivity.class));
                            break;
                    }
                }
            }));
        bmb3.setAutoHide(false);

        Shimmer shimmer = new Shimmer();
        shimmer.start(shimmerTv);
    }

    private void logout() {
        bmb3.clearBuilders();
        saveStatusUserLogin(false, this);
        Intent intent2 = new Intent(this, LoginActivity.class);
        startActivityForResult(intent2, Constans.Login.KEY_REQUEST_CODE_LOGIN);
    }

    private void loadAvatar() {
        User user = getUser(this);
        Picasso.get()
                .load(Uri.parse(APIUtils.base_url + user.getUrlAvatar()))
                .error(R.drawable.shark)
                .into(imgAvatar);
        txtTen.setText(user.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getStattusUserLogin(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, Constans.Login.KEY_REQUEST_CODE_LOGIN);
        } else {
            loadAvatar();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constans.Login.KEY_REQUEST_CODE_LOGIN) {
//            saveStatusUserLogin(true, this);
//            saveUser(this, data.getParcelableExtra(Constans.Login.DATA_RESULT_USER_LOGIN));

            bmb3.setPiecePlaceEnum(PiecePlaceEnum.DOT_2_1);
            bmb3.setButtonPlaceEnum(ButtonPlaceEnum.SC_2_1);
            if (getStattusUserLogin(this) && getUser(this).getLevel().equals("2")) {
                bmb3.setPiecePlaceEnum(PiecePlaceEnum.DOT_4_1);
                bmb3.setButtonPlaceEnum(ButtonPlaceEnum.SC_4_1);
                Log.e(TAG, "onActivityResult: ok");
            }
            for (int i = 0; i < bmb3.getPiecePlaceEnum().pieceNumber(); i++)
                bmb3.addBuilder(BuilderManager.getTextOutsideCircleButtonBuilder(i).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        switch (index) {
                            case 0:
                                startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
                                break;
                            case 1:
                                startActivity(new Intent(MainActivity.this, ChangeAvatarActivity.class));
                                break;
                            case 2:
                                startActivity(new Intent(MainActivity.this, UserManageActivity.class));
                                break;
                            case 3:
                                startActivity(new Intent(MainActivity.this, ModelManagerActivity.class));
                                break;
                        }
                    }
                }));
        }
    }
}
