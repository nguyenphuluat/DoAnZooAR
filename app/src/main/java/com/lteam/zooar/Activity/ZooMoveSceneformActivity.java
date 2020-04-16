package com.lteam.zooar.Activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.text.method.Touch;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.math.Vector3Evaluator;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.lteam.zooar.Model.Animal;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;

import java.io.IOException;

import static com.google.sceneform_animation.cq.w;

public class ZooMoveSceneformActivity extends AppCompatActivity {

    public static final String TAG = "ZooSceneformActivity";
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ModelRenderable modelRenderable;
    private ArFragment arFragment;
    private AnimationData danceData;
    private ModelAnimator animation;
    private MediaPlayer mediaPlayer;
    private Animal animal;
    int t = 0;
    private boolean isEmpty = true;
    AnchorNode anchorNode;
    AnchorNode moveNode;
    Node startNode, andy, endNode;
    ObjectAnimator objectAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isSuport(this)) {
            return;
        }

        setContentView(R.layout.activity_ux_move);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment_move);

        //nhận dữ liệu của animal
        Intent intent1=getIntent();
        animal=intent1.getParcelableExtra("animal");
        //render model animal
        renderable(animal.getUrlmodel());

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if (modelRenderable == null) {
                return;
            }
            onPlaneTap(hitResult, plane, motionEvent);
        });
    }

    private void onPlaneTap(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        if (modelRenderable == null) {
            return;
        }
        // Create the Anchor.
        Anchor anchor = hitResult.createAnchor();

        // Create the starting position.
        if (startNode == null) {
            startNode = new AnchorNode(anchor);
            startNode.setParent(arFragment.getArSceneView().getScene());

            // Create the transformable andy and add it to the anchor.
            andy = new Node();
            andy.setParent(startNode);
            andy.setRenderable(modelRenderable);
            animation();
        } else {
            // Create the end position and start the animation.
            endNode = new AnchorNode(anchor);
            endNode.setParent(arFragment.getArSceneView().getScene());

            //xoay


            Vector3 start = andy.getLocalPosition();
            Vector3 end = endNode.getLocalPosition();
            Vector3 axis = Vector3.cross(start, end);
            axis.normalized();
            Vector3 vAxis = new Vector3(axis.x, axis.y, axis.z);
            float AngleFromZaxis = Vector3.angleBetweenVectors(new Vector3(0, 0, 1), vAxis);
            Quaternion q = new Quaternion(vAxis, AngleFromZaxis);

            andy.setLocalRotation(q);
            //andy.setLocalRotation(Quaternion.rotationBetweenVectors(start,end));
            startWalking();
        }
    }

    private void startWalking() {
        objectAnimation = new ObjectAnimator();
        objectAnimation.setAutoCancel(true);
        objectAnimation.setTarget(andy);

        // All the positions should be world positions
        // The first position is the start, and the second is the end.
        objectAnimation.setObjectValues(andy.getWorldPosition(), endNode.getWorldPosition());

        // Use setWorldPosition to position andy.
        objectAnimation.setPropertyName("worldPosition");

        // The Vector3Evaluator is used to evaluator 2 vector3 and return the next
        // vector3.  The default is to use lerp.
        objectAnimation.setEvaluator(new Vector3Evaluator());
        // This makes the animation linear (smooth and uniform).
        objectAnimation.setInterpolator(new LinearInterpolator());
        // Duration in ms of the animation.
        objectAnimation.setDuration(5000);
        objectAnimation.start();
    }

    private void animation() {
        try {
            danceData = modelRenderable.getAnimationData(0);
            animation = new ModelAnimator(danceData, modelRenderable);
            if (!animation.isRunning()) {
                animation.start();
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animation.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
    }

    private boolean isSuport(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
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
        Toast toast = Toast.makeText(this, "Loading ...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        ModelRenderable.builder()
                .setSource(this, Uri.parse(url))
                .build()
                .thenAccept(modelRenderable1 -> this.modelRenderable = modelRenderable1)
                .exceptionally(throwable -> {
                    Log.e(TAG, "renderable: unabale to load Renderable", throwable);
                    return null;
                });
    }
}
