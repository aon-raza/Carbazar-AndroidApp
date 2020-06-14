package com.example.carbazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carbazar.Interfaces.OnOptionClickListener;
import com.example.carbazar.Models.common;
import com.example.carbazar.Models.followModel;
import com.example.carbazar.Models.reportModel;
import com.example.carbazar.Models.unfollowModel;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterMyAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mongodb.util.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class OtherProfile extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, OnOptionClickListener {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    private List<JSONObject> postsListAds;
    private List<JSONObject> postsListBuyersAds;
    ProgressDialog mDialog;

    private String posterID;
    private String posterName;
    private String posterRole;

    private AppCompatTextView user_name;
    private AppCompatTextView role;
    private AppCompatTextView ads_label;
    private AppCompatTextView buyers_ads_label;
    private AppCompatButton follow_btn;


    private AppCompatTextView following;
    private AppCompatTextView followers;

    private CircleImageView user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        android.view.Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        toolbar = findViewById(R.id.toolbar);
        user_name = findViewById(R.id.user_name);
        role = findViewById(R.id.role);
        ads_label = findViewById(R.id.ads_label);
        buyers_ads_label = findViewById(R.id.buyers_ads_label);

        follow_btn = findViewById(R.id.follow_btn);
        following = findViewById(R.id.following);
        followers = findViewById(R.id.followers);

        user_image = findViewById(R.id.user_image);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        postsListAds = new ArrayList<JSONObject>();
        postsListBuyersAds = new ArrayList<JSONObject>();

        mDialog = new ProgressDialog(OtherProfile.this);

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

        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            posterID = extras.getString("posterID");
            posterName = extras.getString("posterName");
            posterRole = extras.getString("posterRole");

            user_name.setText(posterName);
            role.setText(posterRole);
            String token = "Bearer "+common.currentUser.getToken();
//            if (posterID == null){
//                posterID = common.currentUser.getId().replaceAll(" ","");
//            }
            compositeDisposable.add(iMyService.profile(token,posterID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object s) throws Exception {
                            String response = "";
                            //my own
                            JSONObject jsonObject = new JSONObject(JSON.serialize(s));
                            //String saint = jsonObject.getString("email");
                            if (jsonObject.has("photo")){
                                Glide.with(getApplicationContext())
                                        .asBitmap()
                                        .load(jsonObject.getString("photo"))
                                        .into(user_image);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(OtherProfile.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));

//            Glide.with(this)
//                    .asBitmap()
//                    .load(common.ip + "/user/photo/" + posterID+"?"+new Date().getTime())
//                    .into(user_image);

            ads_label.setText(posterName+"'s Ads");
            buyers_ads_label.setText(posterName+"'s Buyer Ads");

            if(posterID.contentEquals(common.currentUser.getId().replaceAll(" ", ""))){
                follow_btn.setVisibility(View.GONE);
            }

            if (isOnline()){
                getAdsData();
                getBuyerAdsData();
                countFollowFollowing();
            }
            else{
                Toast.makeText(OtherProfile.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()){
                    if(follow_btn.getText().toString().contentEquals("Follow")){
                        follow_btn.setText("Following");
                        String token = "Bearer "+common.currentUser.getToken();
                        followModel ends = new followModel(common.currentUser.getId().replaceAll(" ", ""), posterID);
                        compositeDisposable.add(iMyService.follow(token, ends)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Object>() {
                                    @Override
                                    public void accept(Object s) throws Exception {
                                        countFollowFollowing();
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Toast.makeText(OtherProfile.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }));
                    }
                    else if(follow_btn.getText().toString().contentEquals("Following")) {
                        follow_btn.setText("Follow");
                        String token = "Bearer "+common.currentUser.getToken();
                        unfollowModel ends = new unfollowModel(common.currentUser.getId().replaceAll(" ", ""), posterID);
                        compositeDisposable.add(iMyService.unfollow(token, ends)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Object>() {
                                    @Override
                                    public void accept(Object s) throws Exception {
                                        countFollowFollowing();
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Toast.makeText(OtherProfile.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }));
                    }
                }
                else{
                    Toast.makeText(OtherProfile.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.edit_and_report,menu);

        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            posterID = extras.getString("posterID");
            if(posterID.contentEquals(common.currentUser.getId().replaceAll(" ",""))){
                MenuItem item = menu.findItem(R.id.reportUser);
                item.setVisible(false);
            }
            else{
                MenuItem item = menu.findItem(R.id.editProfile);
                item.setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.editProfile:
                Intent intent = new Intent(OtherProfile.this, editProfile.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.reportUser:
                if(isOnline()) {
                    if(common.currentUser == null){
                        Toast.makeText(OtherProfile.this, "Sign in first", Toast.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(OtherProfile.this, signin.class);
                        startActivity(intent3);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    else{
                        LayoutInflater inflater = (LayoutInflater) OtherProfile.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View formElementsView = inflater.inflate(R.layout.contact_seller_alert,null, false);

                        final AppCompatTextView titleTextView = formElementsView.findViewById(R.id.title_Label);
                        final AppCompatEditText nameEditText = formElementsView.findViewById(R.id.name_field);
                        final AppCompatEditText emailEditText = formElementsView.findViewById(R.id.email_field_alert);
                        final AppCompatEditText messageEditText = formElementsView.findViewById(R.id.message_field);

                        titleTextView.setText("Please Write a reason of reporting.");
                        nameEditText.setText(common.currentUser.getName());
                        nameEditText.setFocusable(false);
                        emailEditText.setText(common.currentUser.getEmail());
                        emailEditText.setFocusable(false);
                        messageEditText.setText(" ");


                        new AlertDialog.Builder(OtherProfile.this).setView(formElementsView)
                                .setTitle("Report User")
                                .setPositiveButton("Report", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {

                                        reportModel RModel = new reportModel();
                                        RModel.setUser_id(common.currentUser.getId().replaceAll(" ",""));
                                        RModel.setUsername(common.currentUser.getName());
                                        RModel.setReported_user_id(posterID);
                                        RModel.setText(messageEditText.getText().toString());

                                        String token = "Bearer "+common.currentUser.getToken();
                                        compositeDisposable.add(iMyService.reportUser(token, RModel)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<Object>() {
                                                    @Override
                                                    public void accept(Object response) throws Exception {
                                                        Toast.makeText(OtherProfile.this, "Thanks for your feedback.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }, new Consumer<Throwable>() {
                                                    @Override
                                                    public void accept(Throwable throwable) throws Exception {
                                                        Toast.makeText(OtherProfile.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }));
                                    }

                                })
                                .setNegativeButton("Cancel", null).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.home_nav:
                //
                Intent intent = new Intent(OtherProfile.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.post_nav:
                //
                Intent intent4 = new Intent(OtherProfile.this, postAd.class);
                startActivity(intent4);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.chatbot_nav:
                //
                Intent intent1 = new Intent(OtherProfile.this, chatbot.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.ar_vr_nav:
                //
                Intent intent2 = new Intent(OtherProfile.this, ARHome.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.account_nav:
                //
                Intent intent3 = new Intent(OtherProfile.this, account.class);
                startActivity(intent3);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;


        }
        return false;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void getAdsData(){
        mDialog.setMessage("Please wait...");
        mDialog.show();
        compositeDisposable.add(iMyService.carBazarPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //my own
                        JSONArray jsonArray = new JSONArray(JSON.serialize(response));
                        int i=0;
                        while(i<jsonArray.length()){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            if(jsonObject.getJSONObject("postedBy").getString("_id").contentEquals(posterID)){
                                postsListAds.add(jsonObject);
                            }
                            i++;
                        }
                        initRecyclerView();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(OtherProfile.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void getBuyerAdsData(){
        compositeDisposable.add(iMyService.carBazarBuyersPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //my own
                        JSONArray jsonArray = new JSONArray(JSON.serialize(response));
                        int i=0;
                        while(i<jsonArray.length()){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            if(jsonObject.getJSONObject("postedBy").getString("_id").contentEquals(posterID)){
                                postsListBuyersAds.add(jsonObject);
                            }
                            i++;
                        }
                        initRecyclerViewBuyersAds();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(OtherProfile.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onOptionClick(String Option, String Label) {

    }

    @Override
    public void onpostClick(String postID, String postType) {
        if (postType.contentEquals("Seller")){
            Intent intent = new Intent(OtherProfile.this, detailedPostCarBazar.class);
            intent.putExtra("postID", postID);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        else if (postType.contentEquals("Buyer")){
            Intent intent2 = new Intent(OtherProfile.this, detailedBuyerPost.class);
            intent2.putExtra("postID", postID);
            startActivity(intent2);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void initRecyclerView(){
        RecyclerView recyclerViewMyAds = findViewById(R.id.recyclerViewAdsOfUser);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewMyAds.setLayoutManager(layoutManager4);

        recyclerViewAdapterMyAds adapter = new recyclerViewAdapterMyAds(this,postsListAds);
        recyclerViewMyAds.setItemAnimator( new DefaultItemAnimator());
        recyclerViewMyAds.setAdapter(adapter);
        mDialog.dismiss();
    }

    private void initRecyclerViewBuyersAds(){
        RecyclerView recyclerViewMyAds = findViewById(R.id.recyclerViewBuyerAdsOfUser);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewMyAds.setLayoutManager(layoutManager3);

        recyclerViewAdapterMyAds adapter = new recyclerViewAdapterMyAds(this,postsListBuyersAds);
        recyclerViewMyAds.setItemAnimator( new DefaultItemAnimator());
        recyclerViewMyAds.setAdapter(adapter);
        mDialog.dismiss();
    }

    private void countFollowFollowing(){
        String token = "Bearer "+common.currentUser.getToken();
        String id = posterID;
        compositeDisposable.add(iMyService.profile(token,id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object s) throws Exception {
                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(s));

                        String numFollowers = ""+jsonObject.getJSONArray("followers").length();
                        String numFollowing = ""+jsonObject.getJSONArray("following").length();

                        followers.setText(numFollowers);
                        following.setText(numFollowing);

                        JSONArray jsonArray = jsonObject.getJSONArray("followers");
                        int i = 0;
                        while(i < jsonArray.length()){
                            String thisID = jsonArray.getJSONObject(0).getString("_id");
                            if (thisID.contentEquals(common.currentUser.getId().replaceAll(" ",""))){
                                follow_btn.setText("Following");
                                break;
                            }
                            i++;
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(OtherProfile.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}
