package com.pukhuriandbeels.limknowpilot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class LakeARActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private Button buttonAR;

    private ModelRenderable modelRenderable;
    private String MODEL_URL = "https://github.com/sangeetagupta2068/LimKnow-Pilot/blob/master/app/sampledata/sample/Astronaut.gltf";
    private FirebaseAuth firebaseAuth;
    private StorageReference firebaseStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lake_ar);
        initialize();
        setListeners();
    }

    private  void initialize(){
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.lake_ar_fragment);
        buttonAR = findViewById(R.id.button_lake_ar);
    }

    private void setListeners(){
        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                placeModel(hitResult.createAnchor());
            }
        });
    }

    private void placeModel(Anchor anchor){
        ModelRenderable.builder().setSource(this, RenderableSource.builder()
                .setSource(this, Uri.parse(MODEL_URL),RenderableSource.SourceType.GLB)
                .setScale(0.75f)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build()

        ).setRegistryId(MODEL_URL)
                .build()
                .thenAccept(renderable -> {
                    addNodeToScene(renderable,anchor);
                }).exceptionally(throwable -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(throwable.getMessage()).show();
            return null;
        });
    }

    private void addNodeToScene(ModelRenderable renderable, Anchor anchor) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setRenderable(renderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

//    private void setupModel(){
//        ModelRenderable.builder().setSource(
//                this,
//                RenderableSource
//                        .builder()
//                        .setSource(this, Uri.parse(MODEL_URL),RenderableSource.SourceType.GLB).setScale(0.75f)
//                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
//                        .build())
//                        .setRegistryId(MODEL_URL)
//                        .build()
//                        .thenAccept(renderable -> modelRenderable = renderable)
//                        .exceptionally(throwable -> {
//                            Toast.makeText(LakeARActivity.this, "Unable to load model.", Toast.LENGTH_SHORT).show();
//                            return null;
//                        });
//    }
//
//    private void setupPlane() {
//        arFragment.setOnTapArPlaneListener(((hitResult, plane, motionEvent) -> {
//            Anchor anchor = hitResult.createAnchor();
//            AnchorNode anchorNode = new AnchorNode(anchor);
//            anchorNode.setRenderable(modelRenderable);
//           arFragment.getArSceneView().getScene().addChild(anchorNode);
////            createModel(anchorNode);
//
//        }));
//    }
//
//    private void createModel(AnchorNode anchorNode){
//        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
//        transformableNode.setParent(anchorNode);
//        transformableNode.setRenderable(modelRenderable);
//        transformableNode.select();
//    }
//
//    private void setupFirebase(){
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        firebaseStorageReference = FirebaseStorage.getInstance().getReference();
//        StorageReference macrophyteStorageReference = firebaseStorageReference.child("AR Animals");
//        StorageReference sampleReference = macrophyteStorageReference.child("AR Animals/BlackNeckedStilt.glb");
//
//        try {
//            File file = File.createTempFile("BlackNeckedStilt","glb");
//            sampleReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    buildModel(file);
//                    Toast.makeText(getApplicationContext(),"Reached here", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void buildModel(File file) {
//        RenderableSource renderableSource = RenderableSource.builder()
//                .setSource(this,Uri.parse(file.getPath()),RenderableSource.SourceType.GLB)
//                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
//                .build();
//
//        ModelRenderable.builder().setSource(this, renderableSource).setRegistryId(Uri.parse(file.getPath())).build().thenAccept(renderable -> {
//            modelRenderable = renderable;
//        });
//    }
}