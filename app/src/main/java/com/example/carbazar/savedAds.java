package com.example.carbazar;

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
import android.widget.Toast;

import com.example.carbazar.Interfaces.OnOptionClickListener;
import com.example.carbazar.Models.common;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterMyAds;
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

public class savedAds extends AppCompatActivity implements OnOptionClickListener {

    private Toolbar toolbar;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    private List<JSONObject> postsListSavedAds;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_ads);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Saved Ads");

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        postsListSavedAds = new ArrayList<JSONObject>();

        mDialog = new ProgressDialog(savedAds.this);

        if (isOnline()){
            getSavedAdsData();
        }
        else{
            Toast.makeText(savedAds.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void getSavedAdsData(){
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
                            i++;
                            String token = "Bearer "+ common.currentUser.getToken();
                            String id = common.currentUser.getId().replaceAll(" ","");
                            compositeDisposable.add(iMyService.getSavedAds(token,id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Object>() {
                                        @Override
                                        public void accept(Object s) throws Exception {
                                            int a=0;
                                            JSONArray jsonArraySaved = new JSONArray(JSON.serialize(s));
                                            while (a<jsonArraySaved.length()){
                                                JSONObject jsonObjectSaved = jsonArraySaved.getJSONObject(a);
//                                                Toast.makeText(savedAds.this, ""+jsonObjectSaved.getString("_id")
//                                                                .contentEquals(jsonObject.getString("_id"))
//                                                        , Toast.LENGTH_SHORT).show();
                                                if(jsonObjectSaved.getString("_id").contentEquals(jsonObject.getString("_id"))){
                                                    postsListSavedAds.add(jsonObject);
                                                }
                                                a++;
                                            }
                                            initRecyclerView();
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(savedAds.this, "Server Error!", Toast.LENGTH_SHORT).show();
                                        }
                                    }));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(savedAds.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(savedAds.this, detailedPostCarBazar.class);
        intent.putExtra("postID", postID);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initRecyclerView(){
        RecyclerView recyclerViewSavedAds = findViewById(R.id.recyclerViewSavedAds);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSavedAds.setLayoutManager(layoutManager4);

        recyclerViewAdapterMyAds adapter = new recyclerViewAdapterMyAds(this,postsListSavedAds);
        recyclerViewSavedAds.setItemAnimator( new DefaultItemAnimator());
        recyclerViewSavedAds.setAdapter(adapter);
        mDialog.dismiss();
    }
}
