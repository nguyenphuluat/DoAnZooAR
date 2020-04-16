package com.lteam.zooar.CloudAnchor;

import android.animation.Animator;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.lteam.zooar.Adapter.AdapterAnimal;
import com.lteam.zooar.Model.Animal;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.lteam.zooar.Server.DataClient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lteam.zooar.Ultils.UltilsLogin.getUser;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    private enum AppAnchorState {
        NONE,
        HOSTING,
        HOSTED,
        RESOLVING,
        RESOLVED
    }

    @BindView(R.id.room_code_text)
    TextView roomCodeText;
    @BindView(R.id.rcvanimal)
    RecyclerView rcvanimal;
    @BindView(R.id.clear_button)
    Button clearButton;
    @BindView(R.id.resolve_button)
    Button resolveButton;

    private ArFragment fragment;
    private Anchor cloudAnchor;
    private AppAnchorState appAnchorState = AppAnchorState.NONE;
    private final SnackbarHelper snackbarHelper = new SnackbarHelper();
    private StorageManager storageManager;
    private ArrayList<Animal> animals = new ArrayList<>();
    private Animal animal;
    private AdapterAnimal adapterAnimal;
    private AnimationData danceData;
    private ModelAnimator animation;

    @OnClick({R.id.clear_button, R.id.resolve_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.clear_button:
                setCloudAnchor(null);
                visiRecycleview(true);
                break;
            case R.id.resolve_button:
                if (cloudAnchor != null) {
                    snackbarHelper.showMessageWithDismiss(MainActivity.this, "Please clear Anchor");
                    return;
                }
                ResolveDialogFragment dialog = new ResolveDialogFragment();
                dialog.setOkListener(MainActivity.this::onResolveOkPressed);
                dialog.show(getSupportFragmentManager(), "Resolve");
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_anchor);
        ButterKnife.bind(this);
        getAnimal();

        fragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        //To add
        fragment.getPlaneDiscoveryController().hide();
        fragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
        fragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING ||
                            appAnchorState != AppAnchorState.NONE && animal == null) {
                        return;
                    }

                    Anchor newAnchor = fragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                    setCloudAnchor(newAnchor);
                    appAnchorState = AppAnchorState.HOSTING;
                    snackbarHelper.showMessage(this, "Now hosting anchor...");
                    placeObject(fragment, cloudAnchor, Uri.parse(APIUtils.base_url + animal.getUrlmodel()));
                }
        );

        storageManager = new StorageManager(this);
    }

    private void visiRecycleview(boolean vision){
        if(vision) rcvanimal.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        else rcvanimal.setLayoutParams(new LinearLayout.LayoutParams(0,0));
    }

    private void getAnimal() {
        animals = new ArrayList<>();

        rcvanimal = findViewById(R.id.rcvanimal);
        adapterAnimal = new AdapterAnimal(animals, this) {
            @Override
            public void renderModel(Animal anima) {
                animal = anima;
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
                if(!response.body().equals("0")){
                    animals.addAll(response.body());
                    adapterAnimal.notifyDataSetChanged();
                }else Toast.makeText(MainActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ArrayList<Animal>> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.fail_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onResolveOkPressed(String dialogValue) {
        int shortCode = Integer.parseInt(dialogValue);
        roomCodeText.setText(String.valueOf(shortCode));
        visiRecycleview(false);
        storageManager.getCloudAnchorID(shortCode, (cloudAnchorId) -> {
            String rs[] = cloudAnchorId.split("&");
            // rs gồm có
            // 1 id của google cloud lưu anchor
            // 2 lưu urlModel cua doi tuong ngan cach bang &
            Anchor resolvedAnchor = fragment.getArSceneView().getSession().resolveCloudAnchor(rs[0]);
            Log.e(TAG, rs[0]);
            setCloudAnchor(resolvedAnchor);
            placeObject(fragment, cloudAnchor, Uri.parse(APIUtils.base_url + rs[1]));
            snackbarHelper.showMessage(this, "Now Resolving Anchor...");
            appAnchorState = AppAnchorState.RESOLVING;
        });
    }

    private void setCloudAnchor(Anchor newAnchor) {
        if (cloudAnchor != null) {
            cloudAnchor.detach();
        }

        cloudAnchor = newAnchor;
        appAnchorState = AppAnchorState.NONE;
        snackbarHelper.hide(this);
    }

    private void onUpdateFrame(FrameTime frameTime) {
        checkUpdatedAnchor();
    }

    private synchronized void checkUpdatedAnchor() {
        if (appAnchorState != AppAnchorState.HOSTING && appAnchorState != AppAnchorState.RESOLVING) {
            return;
        }
        Anchor.CloudAnchorState cloudState = cloudAnchor.getCloudAnchorState();
        if (appAnchorState == AppAnchorState.HOSTING) {
            if (cloudState.isError()) {
                snackbarHelper.showMessageWithDismiss(this, "Error hosting anchor.. "
                        + cloudState);
                appAnchorState = AppAnchorState.NONE;
            } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                storageManager.nextShortCode((shortCode) -> {
                    if (shortCode == null) {
                        snackbarHelper.showMessageWithDismiss(this, "Could not get shortCode");
                        return;
                    }
                    try{
                        storageManager.storeUsingShortCode(shortCode, cloudAnchor.getCloudAnchorId(), this.animal.getUrlmodel());
                    }catch (Exception e){}

                    snackbarHelper.showMessageWithDismiss(this, "Anchor hosted! Cloud Short Code: " +
                            shortCode);
                    roomCodeText.setText(String.valueOf(shortCode));
                    Log.e(TAG, "shortCode: " + shortCode);
                });

                appAnchorState = AppAnchorState.HOSTED;
            }
        } else if (appAnchorState == AppAnchorState.RESOLVING) {
            if (cloudState.isError()) {
                snackbarHelper.showMessageWithDismiss(this, "Error resolving anchor.. "
                        + cloudState);
                appAnchorState = AppAnchorState.NONE;
            } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                snackbarHelper.showMessageWithDismiss(this, "Anchor resolved successfully");
                appAnchorState = AppAnchorState.RESOLVED;
            }
        }
    }

    private void animationMethod(ModelRenderable modelRenderable) {
        try {
            danceData = modelRenderable.getAnimationData(0);
            animation = new ModelAnimator(danceData, modelRenderable);
            int sumAnimation = modelRenderable.getAnimationDataCount();
            loopAnimation(sumAnimation);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
    }

    private void loopAnimation(int sumAnimation){
        animation.start();
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animatio) {
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

    private void placeObject(ArFragment fragment, Anchor anchor, Uri model) {
        ModelRenderable.builder()
                .setSource(fragment.getContext(), model)
                .build()
                .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable))
                .exceptionally((throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .setTitle("Error!");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                }));

    }

    private void addNodeToScene(ArFragment fragment, Anchor anchor, ModelRenderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();

        animationMethod(renderable);
    }
}
