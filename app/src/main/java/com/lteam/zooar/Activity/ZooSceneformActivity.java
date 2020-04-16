package com.lteam.zooar.Activity;

import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.lteam.zooar.Adapter.AdapterAnimal;
import com.lteam.zooar.Model.Animal;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.lteam.zooar.Server.DataClient;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lteam.zooar.Ultils.UltilsLogin.getUser;
import static com.lteam.zooar.Ultils.UtilsAudio.GetStatusAudio;
import static com.lteam.zooar.Ultils.UtilsAudio.SaveStatusAudio;

public class ZooSceneformActivity extends AppCompatActivity {
    public static final String TAG = ZooSceneformActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    @BindView(R.id.rcvanimal)
    RecyclerView rcvanimal;
    @BindView(R.id.mute)
    ImageView mute;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.txtInformationAnimal)
    TextView txtInformationAnimal;
    @BindView(R.id.bmb)
    BoomMenuButton bmb;

    private ModelRenderable modelRenderable;
    private ArFragment arFragment;
    private List<Animal> animals;
    private AdapterAnimal adapterAnimal;
    private AnimationData danceData;
    private ModelAnimator animation;
    private ArrayList<AnchorNode> anchorNodes = new ArrayList<>();
    private String urlAudioSoundEnd;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isSuport(this)) {
            return;
        }
        setContentView(R.layout.activity_ux);
        ButterKnife.bind(this);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if (modelRenderable == null) {
                Log.d(TAG, "onCreate: model null");
                Toast.makeText(this, "chọn đối tượng", Toast.LENGTH_SHORT).show();
                return;
            }
            //Create the Anchor (tạo ra một neo tại vị trí chạm)
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            //create the tranformable
            TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
            transformableNode.setParent(anchorNode);
            transformableNode.setRenderable(modelRenderable);
            transformableNode.select();

            animation();

            anchorNodes.add(anchorNode);
        });
        floatingActionButton.setOnLongClickListener(v -> {
            while (!anchorNodes.isEmpty()) {
                removeAnchorNode(anchorNodes.get(0));
                anchorNodes.remove(0);
            }
            Toast.makeText(this, "delete all", Toast.LENGTH_SHORT).show();
            return true;
        });
        getAnimal();
        if (GetStatusAudio(this)) mute.setImageResource(R.drawable.ic_volume_up_black_24dp);
        else mute.setImageResource(R.drawable.ic_volume_off_black_24dp);
        txtInformationAnimal.setMovementMethod(new ScrollingMovementMethod());

        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_3);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_3);

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            bmb.addBuilder(BuilderManager.getHamButtonBuilderWithDifferentPieceColor(i+4).listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    sort(String.valueOf(index));
                }
            }));
        }
    }

    private void removeAnchorNode(AnchorNode nodeToremove) {
        //Remove an anchor node
        arFragment.getArSceneView().getScene().removeChild(nodeToremove);
        nodeToremove.getAnchor().detach();
        nodeToremove.setParent(null);
        nodeToremove = null;
        Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
    }

    private void getAnimal() {
        animals = new ArrayList<>();

        rcvanimal = findViewById(R.id.rcvanimal);
        adapterAnimal = new AdapterAnimal(animals, this) {
            @Override
            public void renderModel(Animal animal) {
                renderable(animal.getUrlmodel());
                urlAudioSoundEnd = animal.getUrlaudio();

                txtInformationAnimal.setText(animal.getInfomation());
                upLoadView(animal);
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvanimal.setLayoutManager(linearLayoutManager);
        rcvanimal.setAdapter(adapterAnimal);

        DataClient dataClient = APIUtils.getData();
        Call<ArrayList<Animal>> callBack = dataClient.getModel(getUser(this).getLevel());
        callBack.enqueue(new Callback<ArrayList<Animal>>() {
            @Override
            public void onResponse(Call<ArrayList<Animal>> call, Response<ArrayList<Animal>> response) {
                if (!response.body().equals("0")) {
                    animals.addAll(response.body());
                    adapterAnimal.notifyDataSetChanged();
                } else
                    Toast.makeText(ZooSceneformActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ArrayList<Animal>> call, Throwable t) {
                Toast.makeText(ZooSceneformActivity.this, R.string.fail_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void upLoadView(Animal animal) {
        DataClient dataClient = APIUtils.getData();
        Call<String> call = dataClient.model(getUser(this).getId(), animal.getId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body().equals("0"))
                    Toast.makeText(ZooSceneformActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ZooSceneformActivity.this, R.string.fail_connect + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void animation() {
        try {
            try {
                mediaPlayer = new MediaPlayer();
                //kiểu phát mediaplayer (stream nhạc)
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //truyề data qua url lấy trên host . firebase ngon
                mediaPlayer.setDataSource(APIUtils.base_url + urlAudioSoundEnd);
                //hình như trong thred nó chỉ chạy khi đã prepare
                mediaPlayer.prepareAsync();
            } catch (IOException ex) {
                Log.e(TAG, "animation: " + ex.toString());
            }

            danceData = modelRenderable.getAnimationData(0);
            animation = new ModelAnimator(danceData, modelRenderable);
            int sumAnimation = modelRenderable.getAnimationDataCount();
            loopAnimation(sumAnimation);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
    }

    private void loopAnimation(int sumAnimation) {
        animation.start();
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                try {
                    if (GetStatusAudio(ZooSceneformActivity.this) && !mediaPlayer.isPlaying())
                        mediaPlayer.start();
                } catch (Exception e) {
                }
            }

            @Override
            public void onAnimationEnd(Animator animatio) {
//                if (sumAnimation > 1) {
//                    danceData = modelRenderable.getAnimationData(++currentAnimation % sumAnimation);
//                    if (currentAnimation == sumAnimation) currentAnimation = 0;
//                    animation = new ModelAnimator(danceData, modelRenderable);
//                }
//                loopAnimation(sumAnimation);
                animatio.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private boolean isSuport(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        } else {
            Log.d(TAG, "isSuport: " + openGlVersionString);
        }
        return true;
    }

    private void renderable(String url) {
        url = APIUtils.base_url + url;
        Toast toast = Toast.makeText(ZooSceneformActivity.this, "Loading ...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        ModelRenderable.builder()
                .setSource(this, Uri.parse(url))
                .build()
                .thenAccept(modelRenderable1 -> {
                    this.modelRenderable = modelRenderable1;
                    Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "error " + throwable.toString(), Toast.LENGTH_SHORT).show();
                    return null;
                });
    }

    @OnClick({R.id.mute, R.id.floatingActionButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mute:
                if (SaveStatusAudio(this))
                    mute.setImageResource(R.drawable.ic_volume_up_black_24dp);
                else mute.setImageResource(R.drawable.ic_volume_off_black_24dp);
                break;
            case R.id.floatingActionButton:
                if (!anchorNodes.isEmpty()) {
                    removeAnchorNode(anchorNodes.get(anchorNodes.size() - 1));
                    anchorNodes.remove(anchorNodes.size() - 1);
                    Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void sort(String which) {
        DataClient dataClient = APIUtils.getData();
        Call<ArrayList<Animal>> call = dataClient.sortModel(which, getUser(this).getId());
        call.enqueue(new Callback<ArrayList<Animal>>() {
            @Override
            public void onResponse(Call<ArrayList<Animal>> call, Response<ArrayList<Animal>> response) {
                if (!response.body().equals("0")) {
                    animals.clear();
                    animals.addAll(response.body());
                    adapterAnimal.notifyDataSetChanged();
                } else
                    Toast.makeText(ZooSceneformActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ArrayList<Animal>> call, Throwable t) {
                Toast.makeText(ZooSceneformActivity.this, R.string.fail_connect + t.toString(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.toString());
            }
        });
    }
}
