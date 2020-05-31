package com.example.carbazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.carbazar.Interfaces.OnOptionClickListener;
import com.example.carbazar.Models.common;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterMyAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mongodb.util.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class myAds extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, OnOptionClickListener {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    private List<JSONObject> postsListmyAds;
    ProgressDialog mDialog;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        android.view.Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your Ads");

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        postsListmyAds = new ArrayList<JSONObject>();

        mDialog = new ProgressDialog(myAds.this);

        searchView = findViewById(R.id.searchView);

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


        if (isOnline()){
            getMyAdsData();
        }
        else{
            Toast.makeText(myAds.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(search.this, query,Toast.LENGTH_SHORT).show();
                if (isOnline()){
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(myAds.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent1 = new Intent(myAds.this, postAd.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(myAds.this, postAd.class);
        startActivity(intent1);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.home_nav:
                //
                Intent intent = new Intent(myAds.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.post_nav:
                //
                return true;

            case R.id.chatbot_nav:
                //
                Intent intent1 = new Intent(myAds.this, chatbot.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.ar_vr_nav:
                //
                Intent intent2 = new Intent(myAds.this, ARHome.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.account_nav:
                //
                Intent intent3 = new Intent(myAds.this, account.class);
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

    private void getMyAdsData(){
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

                            if(jsonObject.getJSONObject("postedBy").getString("_id").contentEquals(common.currentUser.getId().replaceAll(" ", ""))){
                                postsListmyAds.add(jsonObject);
                            }
                            i++;
                        }
                        initRecyclerView();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(myAds.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onpostClick(String postID) {
        Intent intent = new Intent(myAds.this, detailedPostCarBazar.class);
        intent.putExtra("postID", postID);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initRecyclerView(){
        RecyclerView recyclerViewMyAds = findViewById(R.id.recyclerViewMyAds);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewMyAds.setLayoutManager(layoutManager4);

        recyclerViewAdapterMyAds adapter = new recyclerViewAdapterMyAds(this,postsListmyAds);
        recyclerViewMyAds.setItemAnimator( new DefaultItemAnimator());
        recyclerViewMyAds.setAdapter(adapter);
        mDialog.dismiss();
    }
}
