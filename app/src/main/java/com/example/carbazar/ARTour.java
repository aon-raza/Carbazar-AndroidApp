package com.example.carbazar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Objects;

import static com.google.ar.sceneform.math.Vector3.*;

public class ARTour extends AppCompatActivity /*implements View.OnClickListener */ {
    ArFragment arFragment;
    TransformableNode generalTransformableNode;
    AppCompatImageView zoomOut;
    AppCompatImageView zoomIn;
    AppCompatImageView rotateRight;
    AppCompatImageView rotateLeft;
    AppCompatImageView upBtn;
    AppCompatImageView downBtn;
    AppCompatImageView leftBtn;
    AppCompatImageView rightBtn;
    //private String ASSET_3D="http://705fb825.ngrok.io/scene.gltf";
    private ModelRenderable
            bmwRenderable,
            porschRenderable,
            benzRenderable,
            elantraRenderable,
            acuraRenderable,
            acurablueRenderable,
            porsche_redRenderable;
    //ImageView bmw, porsch, elantra, benz;
    //View arrayView[];
    String selectedModel = "BMW";    //Default bmw is selected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_tour);

        arFragment=(ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);

        generalTransformableNode = null;

        zoomOut = findViewById(R.id.zoomOutBtn);
        zoomIn = findViewById(R.id.zoomInBtn);

        rotateRight = findViewById(R.id.rotateRightBtn);
        rotateLeft = findViewById(R.id.rotateLeftBtn);

        upBtn = findViewById(R.id.upBtn);
        downBtn = findViewById(R.id.downBtn);
        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);
        
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generalTransformableNode != null){
                    Vector3 currentPosition = new Vector3();
                    Vector3 move = new Vector3();
                    currentPosition = Objects.requireNonNull(generalTransformableNode.getLocalPosition());
                    move.set(currentPosition.x, currentPosition.y, (float) (currentPosition.z - 0.1));
                    generalTransformableNode.setLocalPosition(move);
                }
            }
        });

        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generalTransformableNode != null){
                    Vector3 currentPosition = new Vector3();
                    Vector3 move = new Vector3();
                    currentPosition = Objects.requireNonNull(generalTransformableNode.getLocalPosition());
                    move.set(currentPosition.x, currentPosition.y, (float) (currentPosition.z + 0.1));
                    generalTransformableNode.setLocalPosition(move);
                }
            }
        });


        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generalTransformableNode != null){
//                    Vector3 currentPosition = new Vector3();
//                    Vector3 move = new Vector3();
//                    currentPosition = Objects.requireNonNull(generalTransformableNode.getLocalPosition());
//                    rotateRight(generalTransformableNode, objAxis, currentPosition);
//                    generalTransformableNode.setLocalPosition(move);
                    Quaternion q1 = generalTransformableNode.getLocalRotation();
                    Quaternion q2 = Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 3f);
                    generalTransformableNode.setLocalRotation(Quaternion.multiply(q1, q2));
                }
            }
        });

        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generalTransformableNode != null){
//                    Vector3 currentPosition = new Vector3();
//                    Vector3 move = new Vector3();
//                    currentPosition = Objects.requireNonNull(generalTransformableNode.getLocalPosition());
//                    //rotateRight(generalTransformableNode, objAxis, currentPosition);
//                    generalTransformableNode.setLocalPosition(move);
                    Quaternion q1 = generalTransformableNode.getLocalRotation();
                    Quaternion q2 = Quaternion.axisAngle(new Vector3(0f, 1f, 0f), -3f);
                    generalTransformableNode.setLocalRotation(Quaternion.multiply(q1, q2));
                }
            }
        });


        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generalTransformableNode != null){
                    Vector3 currentPosition = new Vector3();
                    Vector3 move = new Vector3();
                    currentPosition = Objects.requireNonNull(generalTransformableNode.getLocalPosition());
                    move.set(currentPosition.x, (float) (currentPosition.y + 0.1), currentPosition.z);
                    generalTransformableNode.setLocalPosition(move);
                }
            }
        });

        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generalTransformableNode != null){
                    Vector3 currentPosition = new Vector3();
                    Vector3 move = new Vector3();
                    currentPosition = Objects.requireNonNull(generalTransformableNode.getLocalPosition());
                    move.set(currentPosition.x, (float) (currentPosition.y - 0.1), currentPosition.z);
                    generalTransformableNode.setLocalPosition(move);
                }
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generalTransformableNode != null){
                    Vector3 currentPosition = new Vector3();
                    Vector3 move = new Vector3();
                    currentPosition = Objects.requireNonNull(generalTransformableNode.getLocalPosition());
                    move.set((float) (currentPosition.x - 0.1), currentPosition.y, currentPosition.z);
                    //move.set(currentPosition.x, currentPosition.y, (float) (currentPosition.z - 0.1));
                    generalTransformableNode.setLocalPosition(move);
                }
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generalTransformableNode != null){
                    Vector3 currentPosition = new Vector3();
                    Vector3 move = new Vector3();
                    currentPosition = Objects.requireNonNull(generalTransformableNode.getLocalPosition());
                    move.set((float) (currentPosition.x + 0.1), currentPosition.y, currentPosition.z);
                    generalTransformableNode.setLocalPosition(move);
                }
            }
        });



        Bundle extras = getIntent().getExtras();
        if(extras!= null){
            selectedModel = extras.getString("key");
        }

        setupModel(selectedModel);
        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                // On tap of the screen a model will be placed on to the user screen
                Anchor anchor= hitResult.createAnchor();
                AnchorNode anchorNode= new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());
                createModel(anchorNode, selectedModel);
                arFragment.setOnTapArPlaneListener(null);

            }
        });
    }
    private void setupModel(String selectedModel) {

        if(selectedModel.contentEquals("BMW")){
            ModelRenderable.builder()
                    .setSource(this, R.raw.bmw)
                    .build().thenAccept(renderable -> bmwRenderable = renderable)
                    .exceptionally(
                            throwable ->
                            {
                                Toast.makeText(this,"Unable to load bmw model", Toast.LENGTH_SHORT).show();
                                return null;
                            }
                    );
        }

        else if(selectedModel.contentEquals("Porsch")){
            ModelRenderable.builder()
                    .setSource(this, R.raw.porsch)
                    .build().thenAccept(renderable -> porschRenderable = renderable)
                    .exceptionally(
                            throwable ->
                            {
                                Toast.makeText(this,"Unable to load porsch model", Toast.LENGTH_SHORT).show();
                                return null;
                            }
                    );
        }


        else if(selectedModel.contentEquals("Benz")){
            ModelRenderable.builder()
                    .setSource(this, R.raw.benz)
                    .build().thenAccept(renderable -> benzRenderable = renderable)
                    .exceptionally(
                            throwable ->
                            {
                                Toast.makeText(this,"Unable to load benz model", Toast.LENGTH_SHORT).show();
                                return null;
                            }
                    );
        }

        else if(selectedModel.contentEquals("Elentra")){
            ModelRenderable.builder()
                    .setSource(this, R.raw.elantra)
                    .build().thenAccept(renderable -> elantraRenderable = renderable)
                    .exceptionally(
                            throwable ->
                            {
                                Toast.makeText(this,"Unable to load elantra model", Toast.LENGTH_SHORT).show();
                                return null;
                            }
                    );
        }

        else if(selectedModel.contentEquals("acura")){
            ModelRenderable.builder()
                    .setSource(this, R.raw.acura)
                    .build().thenAccept(renderable -> acuraRenderable = renderable)
                    .exceptionally(
                            throwable ->
                            {
                                Toast.makeText(this,"Unable to load acura model", Toast.LENGTH_SHORT).show();
                                return null;
                            }
                    );
        }

        else if(selectedModel.contentEquals("acurablue")){
            ModelRenderable.builder()
                    .setSource(this, R.raw.acurablue)
                    .build().thenAccept(renderable -> acurablueRenderable = renderable)
                    .exceptionally(
                            throwable ->
                            {
                                Toast.makeText(this,"Unable to load acurablue model", Toast.LENGTH_SHORT).show();
                                return null;
                            }
                    );
        }

        else if(selectedModel.contentEquals("porsche_red")){
            ModelRenderable.builder()
                    .setSource(this, R.raw.porsche_red)
                    .build().thenAccept(renderable -> porsche_redRenderable = renderable)
                    .exceptionally(
                            throwable ->
                            {
                                Toast.makeText(this,"Unable to load elantra model", Toast.LENGTH_SHORT).show();
                                return null;
                            }
                    );
        }

    }


    private void createModel(AnchorNode anchorNode, String selectedModel)
    {
        if(selectedModel.contentEquals("BMW"))
        {
            TransformableNode bmw= new TransformableNode(arFragment.getTransformationSystem());
            bmw.setParent(anchorNode);
            bmw.setRenderable(bmwRenderable);
            bmw.select();

            generalTransformableNode = bmw;
        }
        else if(selectedModel.contentEquals("Porsch"))
        {
            TransformableNode porsch= new TransformableNode(arFragment.getTransformationSystem());
            porsch.setParent(anchorNode);
            porsch.setRenderable(porschRenderable);
            porsch.select();

            generalTransformableNode = porsch;
        }
        else if(selectedModel.contentEquals("Benz"))
        {
            TransformableNode benz= new TransformableNode(arFragment.getTransformationSystem());
            benz.setParent(anchorNode);
            benz.setRenderable(benzRenderable);
            benz.select();
            generalTransformableNode = benz;
        }
        else if(selectedModel.contentEquals("Elentra"))
        {
            TransformableNode elantra= new TransformableNode(arFragment.getTransformationSystem());
            elantra.setParent(anchorNode);
            elantra.setRenderable(elantraRenderable);
            elantra.select();

            generalTransformableNode = elantra;
        }

        else if(selectedModel.contentEquals("acura"))
        {
            TransformableNode acura= new TransformableNode(arFragment.getTransformationSystem());
            acura.setParent(anchorNode);
            acura.setRenderable(acuraRenderable);
            acura.select();

            generalTransformableNode = acura;
        }

        else if(selectedModel.contentEquals("acurablue"))
        {
            TransformableNode acurablue= new TransformableNode(arFragment.getTransformationSystem());
            acurablue.setParent(anchorNode);
            acurablue.setRenderable(acurablueRenderable);
            acurablue.select();

            generalTransformableNode = acurablue;
        }

        else if(selectedModel.contentEquals("porsche_red"))
        {
            TransformableNode porsche_red= new TransformableNode(arFragment.getTransformationSystem());
            porsche_red.setParent(anchorNode);
            porsche_red.setRenderable(porsche_redRenderable);
            porsche_red.select();

            generalTransformableNode = porsche_red;
        }
    }
}