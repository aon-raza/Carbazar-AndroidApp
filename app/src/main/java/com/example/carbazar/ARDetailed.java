package com.example.carbazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.carbazar.Models.ARComments;
import com.example.carbazar.Models.ARModel;
import com.example.carbazar.Models.common;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterARComments;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mongodb.util.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ARDetailed extends AppCompatActivity  implements BottomNavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    String selectedModel = "BMW";
    String selectedModelID = "";
    private AppCompatButton viewInARBTN;
    private AppCompatButton view_in_vr;


    private CircleImageView car_image_ARDetail;
    private AppCompatTextView arModelName;
    private AppCompatImageView car_image;

    private AppCompatImageView likeBtn;
    private AppCompatImageView dislikeBtn;
    private AppCompatImageView commentBtn;
    private AppCompatTextView likeTV;
    private AppCompatTextView dislikeTV;
    AppCompatEditText commentET;
    RelativeLayout commentSendBtn;

    boolean liked = false;
    boolean disliked = false;

    List<ARComments> commentsList;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ardetailed);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        android.view.Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        toolbar = findViewById(R.id.toolbar);
        car_image_ARDetail = findViewById(R.id.car_image_ARDetail);
        arModelName = findViewById(R.id.arModelName);
        car_image = findViewById(R.id.car_image);

        likeBtn = findViewById(R.id.like);
        dislikeBtn = findViewById(R.id.dislike);
        commentBtn = findViewById(R.id.comment);
        likeTV = findViewById(R.id.likeTV);
        dislikeTV = findViewById(R.id.dislikeTV);

        commentET = findViewById(R.id.commentET);
        commentSendBtn = findViewById(R.id.commentSendBtn);

        arModelName.requestFocus();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commentsList = new ArrayList<ARComments>();

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        mDialog = new ProgressDialog(ARDetailed.this);

        mDialog.setMessage("Please wait...");
        mDialog.show();
        Bundle extras = getIntent().getExtras();
        if(extras!= null){
            selectedModel = extras.getString("key");

            compositeDisposable.add(iMyService.getAllArModels()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object s) throws Exception {
                            //my own
                            JSONArray jsonArray = new JSONArray(JSON.serialize(s));

                            int i = 0;
                            while(i < jsonArray.length()){
                                String modelName = jsonArray.getJSONObject(i).getString("name");
                                if (selectedModel.contentEquals(modelName)){
                                    selectedModelID = jsonArray.getJSONObject(i).getString("_id");
                                    break;
                                }
                                i++;
                            }

                            ARModel arModel = new ARModel();
                            arModel.setModelId(selectedModelID);
                            compositeDisposable.add(iMyService.singleArModel(arModel)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Object>() {
                                        @Override
                                        public void accept(Object s) throws Exception {
                                            JSONObject jsonObject = new JSONObject(JSON.serialize(s));
                                            JSONArray commentUsers = jsonObject.getJSONArray("likes");
                                            int i = 0;
                                            while(i < commentUsers.length()) {
                                                String commentId = commentUsers.getString(i);
                                                if (commentId.contentEquals(common.currentUser.getId().replaceAll(" ", ""))) {
                                                    likeBtn.setImageResource(R.drawable.liked);
                                                    dislikeBtn.setImageResource(R.drawable.dislike);
                                                    likeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                    dislikeTV.setTextColor(getResources().getColor(R.color.colorIconsAR));
                                                    liked = true;
                                                    disliked = false;
                                                    break;
                                                }
                                                i++;
                                            }

                                            commentUsers = jsonObject.getJSONArray("dislikes");
                                            i = 0;
                                            while(i < commentUsers.length()) {
                                                String commentId = commentUsers.getString(i);
                                                if (commentId.contentEquals(common.currentUser.getId().replaceAll(" ", ""))) {
                                                    dislikeBtn.setImageResource(R.drawable.disliked);
                                                    likeBtn.setImageResource(R.drawable.like);
                                                    dislikeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                    likeTV.setTextColor(getResources().getColor(R.color.colorIconsAR));
                                                    disliked = true;
                                                    liked = false;
                                                    break;
                                                }
                                                i++;
                                            }

                                            JSONArray allComments = jsonObject.getJSONArray("comments");
                                            i = 0;
                                            while(i < allComments.length()) {
                                                JSONObject aComment = allComments.getJSONObject(i);

                                                ARComments arComments = new ARComments();
                                                arComments.setComment(aComment.getString("text"));
                                                arComments.setCommenterName(aComment.getJSONObject("author").getString("username"));
                                                commentsList.add(arComments);

                                                i++;
                                            }
                                            initRecyclerView();
                                            mDialog.dismiss();
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(ARDetailed.this, "Server Error! "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(ARDetailed.this, "Server Error! "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));

        }

        if(selectedModel.contentEquals("BMW")){
            getSupportActionBar().setTitle("BMW");
            car_image_ARDetail.setImageResource(R.drawable.black);
            arModelName.setText("BMW");
            car_image.setImageResource(R.drawable.black);

        }

        else if(selectedModel.contentEquals("Porsch")){
            getSupportActionBar().setTitle("Porsch");
            car_image_ARDetail.setImageResource(R.drawable.bmw);
            arModelName.setText("Porsch");
            car_image.setImageResource(R.drawable.bmw);
        }


        else if(selectedModel.contentEquals("Benz")){
            getSupportActionBar().setTitle("Mercedes Benz");
            car_image_ARDetail.setImageResource(R.drawable.audi);
            arModelName.setText("Mercedes Benz");
            car_image.setImageResource(R.drawable.audi);
        }

        else if(selectedModel.contentEquals("Elentra")){
            getSupportActionBar().setTitle("Elentra");
            car_image_ARDetail.setImageResource(R.drawable.corolla);
            arModelName.setText("Elentra");
            car_image.setImageResource(R.drawable.corolla);
        }

//        view_in_vr = findViewById(R.id.view_in_vr);
//        view_in_vr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ARDetailed.this, viewInVR.class);
//                startActivity(intent);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            }
//        });

        viewInARBTN = findViewById(R.id.view_in_AR);
        viewInARBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedModel.contentEquals("BMW")){
                    Intent intent = new Intent(ARDetailed.this, ARTour.class);
                    intent.putExtra("key", "BMW");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

                else if(selectedModel.contentEquals("Porsch")){
                    Intent intent = new Intent(ARDetailed.this, ARTour.class);
                    intent.putExtra("key", "Porsch");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }


                else if(selectedModel.contentEquals("Benz")){
                    Intent intent = new Intent(ARDetailed.this, ARTour.class);
                    intent.putExtra("key", "Benz");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

                else if(selectedModel.contentEquals("Elentra")){
                    Intent intent = new Intent(ARDetailed.this, ARTour.class);
                    intent.putExtra("key", "Elentra");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!liked){
                    likeBtn.setImageResource(R.drawable.liked);
                    dislikeBtn.setImageResource(R.drawable.dislike);
                    likeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                    dislikeTV.setTextColor(getResources().getColor(R.color.colorIconsAR));
                    liked = true;
                    disliked = false;

                    ARModel arModel = new ARModel();
                    arModel.setModelId(selectedModelID);
                    arModel.setUserId(common.currentUser.getId().replaceAll(" ",""));
                    compositeDisposable.add(iMyService.likeARModel(arModel)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object s) throws Exception {
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(ARDetailed.this, "Server Error!", Toast.LENGTH_SHORT).show();
                                }
                            }));

                    compositeDisposable.add(iMyService.undislikeARModel(arModel)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object s) throws Exception {
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(ARDetailed.this, "Server Error!", Toast.LENGTH_SHORT).show();
                                }
                            }));
                }
                else {
                    likeBtn.setImageResource(R.drawable.like);
                    likeTV.setTextColor(getResources().getColor(R.color.colorIconsAR));
                    liked = false;
                    ARModel arModel = new ARModel();
                    arModel.setModelId(selectedModelID);
                    arModel.setUserId(common.currentUser.getId().replaceAll(" ",""));
                    compositeDisposable.add(iMyService.unlikeARModel(arModel)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object s) throws Exception {
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(ARDetailed.this, "Server Error!", Toast.LENGTH_SHORT).show();
                                }
                            }));
                }
            }
        });

        dislikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!disliked){
                    dislikeBtn.setImageResource(R.drawable.disliked);
                    likeBtn.setImageResource(R.drawable.like);
                    dislikeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                    likeTV.setTextColor(getResources().getColor(R.color.colorIconsAR));
                    disliked = true;
                    liked = false;

                    ARModel arModel = new ARModel();
                    arModel.setModelId(selectedModelID);
                    arModel.setUserId(common.currentUser.getId().replaceAll(" ",""));
                    compositeDisposable.add(iMyService.dislikeARModel(arModel)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object s) throws Exception {
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(ARDetailed.this, "Server Error!", Toast.LENGTH_SHORT).show();
                                }
                            }));

                    compositeDisposable.add(iMyService.unlikeARModel(arModel)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object s) throws Exception {
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(ARDetailed.this, "Server Error!", Toast.LENGTH_SHORT).show();
                                }
                            }));
                }
                else {
                    dislikeBtn.setImageResource(R.drawable.dislike);
                    dislikeTV.setTextColor(getResources().getColor(R.color.colorIconsAR));
                    disliked = false;

                    ARModel arModel = new ARModel();
                    arModel.setModelId(selectedModelID);
                    arModel.setUserId(common.currentUser.getId().replaceAll(" ",""));
                    compositeDisposable.add(iMyService.undislikeARModel(arModel)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object s) throws Exception {
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(ARDetailed.this, "Server Error!", Toast.LENGTH_SHORT).show();
                                }
                            }));
                }
            }
        });

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentET.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        commentSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    if(common.currentUser == null){
                        Toast.makeText(ARDetailed.this, "Sign in first", Toast.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(ARDetailed.this, signin.class);
                        startActivity(intent3);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    else{
                        if(commentET.getText().toString().contentEquals("")){
                            Toast.makeText(getApplicationContext(), "Enter Comment first", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            ARComments arComments = new ARComments();
                            arComments.setComment(commentET.getText().toString());
                            arComments.setCommenterName(common.currentUser.getName());
                            commentsList.add(arComments);

                            hideKeyboard(v);
                            initRecyclerView();

                            ARModel arModel = new ARModel();
                            arModel.setModelId(selectedModelID);
                            arModel.setUser_id(common.currentUser.getId().replaceAll(" ",""));
                            arModel.setUsername(common.currentUser.getName());
                            arModel.setText(commentET.getText().toString());

                            commentET.setText("");

                            compositeDisposable.add(iMyService.postARmodelComment(arModel)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Object>() {
                                        @Override
                                        public void accept(Object s) throws Exception {
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(ARDetailed.this, "Server Error!", Toast.LENGTH_SHORT).show();
                                        }
                                    }));
                        }
                    }
                }else{
                    Toast.makeText(ARDetailed.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible)
            {
                if(isVisible){
                    bottomNavigationView.setVisibility(View.GONE);
                }
                else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.home_nav:
                //
                Intent intent2 = new Intent(ARDetailed.this, MainActivity.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.post_nav:
                //
                Intent intent1 = new Intent(ARDetailed.this, postAd.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.chatbot_nav:
                //
                Intent intent3 = new Intent(ARDetailed.this, chatbot.class);
                startActivity(intent3);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.ar_vr_nav:
                //
                return true;

            case R.id.account_nav:
                //
                Intent intent = new Intent(ARDetailed.this, account.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;


        }
        return false;
    }

    private void initRecyclerView(){

        RecyclerView recyclerView = findViewById(R.id.recyclerViewComments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapterARComments adapter = new recyclerViewAdapterARComments(this,commentsList);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
