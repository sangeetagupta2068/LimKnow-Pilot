package com.pukhuriandbeels.limknowpilot;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LakeARActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 701;
    public static int count = 0;
    private ArFragment arFragment;
    private TextView textViewGuide;
    private ModelRenderable modelRenderable;
    private String MODEL_URL;
    private FirebaseAuth firebaseAuth;
    private float scale;
    private FloatingActionButton floatingActionButton;
    private String filename;
    private boolean isSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lake_ar);
        firebaseAuth = FirebaseAuth.getInstance();
        initialize();
        setListeners();
    }

    private void initialize() {
        isSaved = true;
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.lake_ar_fragment);
        textViewGuide = findViewById(R.id.lake_ar_guide);
        floatingActionButton = findViewById(R.id.camera_button);

        MODEL_URL = LakeARQuizActivity.animals.get(LakeARQuizActivity.questionCount).getAnimalARModelURL();
        scale = LakeARQuizActivity.animals.get(LakeARQuizActivity.questionCount).getAnimalScale();
        arFragment.getArSceneView().getPlaneRenderer().setVisible(true);
    }

    private void setListeners() {
        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING)
                    return;
                placeModel(hitResult.createAnchor());
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(LakeARActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LakeARActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                } else {
                    takePhoto();
                }
                if (isSaved) {
                    Toast.makeText(LakeARActivity.this, "Image is saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LakeARActivity.this, "Couldn't save image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void placeModel(Anchor anchor) {
        ModelRenderable.builder().setSource(arFragment.getContext(), RenderableSource.builder()
                .setSource(this, Uri.parse(MODEL_URL), RenderableSource.SourceType.GLB)
                .setScale(scale)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build()

        ).setRegistryId(MODEL_URL)
                .build()
                .thenAccept(renderable -> {
                    addNodeToScene(renderable, anchor);
                }).exceptionally(throwable -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(throwable.getMessage()).show();
            return null;
        });
    }

    private void addNodeToScene(ModelRenderable renderable, Anchor anchor) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                Intent intent = new Intent(LakeARActivity.this, ARAnimalItemActivity.class);
                startActivity(intent);
            }
        });
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

    private void saveBitmapToDisk(Bitmap bitmap) {
        try {
            String filename = "JPEG_" + System.currentTimeMillis() + ".jpeg";
            OutputStream outputStream = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver contentResolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (uri != null) {
                    outputStream = contentResolver.openOutputStream(uri);
                }
            } else {
                String path = Environment.getExternalStorageDirectory().toString();
                File folder = new File(path + "/LimKnow");
                folder.mkdirs();
                File file = new File(folder, filename);
                outputStream = new FileOutputStream(file);
            }

            ByteArrayOutputStream outputData = new ByteArrayOutputStream();
            isSaved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputData);
            if (outputStream != null) {
                outputData.writeTo(outputStream);
                outputStream.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void takePhoto() {
        ArSceneView view = arFragment.getArSceneView();

        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();

        PixelCopy.request(view, bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public void onPixelCopyFinished(int copyResult) {
                if (copyResult == PixelCopy.SUCCESS) {
                    try {
                        saveBitmapToDisk(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    handlerThread.quitSafely();
                }
            }
        }, new Handler(handlerThread.getLooper()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Gallery Permission denied", Toast.LENGTH_SHORT).show();
            } else {
                takePhoto();
            }
        }
    }
}