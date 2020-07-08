package com.example.carbazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.carbazar.Models.common;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterRecommender;
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

public class searchCarReviews extends AppCompatActivity {

    private Toolbar toolbar;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    ProgressDialog mDialog;

    private Spinner makeSpinnerSelection;
    private Spinner modelSpinnerSelection;
    private Spinner versionSpinnerSelection;

    private AppCompatButton post_btn;

    List<String> makeSpinnerItems;
    List<String> modelSpinnerItems;
    List<String> versionSpinnerItems;

    private AppCompatButton write_review_btn;

    private List<JSONObject> searchedList;

    private AppCompatTextView R_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_car_reviews);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Car Reviews");

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        mDialog = new ProgressDialog(searchCarReviews.this);

        makeSpinnerSelection = findViewById(R.id.makeSpinnerSelection);
        modelSpinnerSelection = findViewById(R.id.modelSpinnerSelection);
        versionSpinnerSelection = findViewById(R.id.versionSpinnerSelection);

        post_btn = findViewById(R.id.post_btn);

        makeSpinnerItems = new ArrayList<String>();
        modelSpinnerItems = new ArrayList<String>();
        versionSpinnerItems = new ArrayList<String>();

        searchedList = new ArrayList<JSONObject>();


        write_review_btn = findViewById(R.id.write_review_btn);

        R_label = findViewById(R.id.R_label);
        R_label.setVisibility(View.GONE);

        write_review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(searchCarReviews.this, writeReview.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        getDataInMakeSpinner();
        getDataInOtherSpinners();

        makeSpinnerSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0 && position !=1){
                    getDataInModelSpinner(makeSpinnerSelection.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        modelSpinnerSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0 && position !=1){
                    getDataInVersionSpinner(modelSpinnerSelection.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                        sendData();
                }else{
                    Toast.makeText(searchCarReviews.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendData() {

        if (makeSpinnerSelection.getSelectedItem() == null) {
            Toast.makeText(searchCarReviews.this, "Select Make", Toast.LENGTH_SHORT).show();
            return;
        }

        if (makeSpinnerSelection.getSelectedItemPosition() == 0 || makeSpinnerSelection.getSelectedItemPosition() == 1) {
            Toast.makeText(searchCarReviews.this, "Select Make", Toast.LENGTH_SHORT).show();
            makeSpinnerSelection.setFocusable(true);
            makeSpinnerSelection.setFocusableInTouchMode(true);
            makeSpinnerSelection.requestFocus();
            makeSpinnerSelection.performClick();
            return;
        }

        if (modelSpinnerSelection.getSelectedItemPosition() == 0 || modelSpinnerSelection.getSelectedItemPosition() == 1) {
            Toast.makeText(searchCarReviews.this, "Select Model", Toast.LENGTH_SHORT).show();
            modelSpinnerSelection.setFocusable(true);
            modelSpinnerSelection.setFocusableInTouchMode(true);
            modelSpinnerSelection.requestFocus();
            modelSpinnerSelection.performClick();
            return;
        }

        if (versionSpinnerSelection.getSelectedItemPosition() == 0 || versionSpinnerSelection.getSelectedItemPosition() == 1) {
            Toast.makeText(searchCarReviews.this, "Select Registration City", Toast.LENGTH_SHORT).show();
            versionSpinnerSelection.setFocusable(true);
            versionSpinnerSelection.setFocusableInTouchMode(true);
            versionSpinnerSelection.requestFocus();
            versionSpinnerSelection.performClick();
            return;
        }

        mDialog.setMessage("Please wait...");
        mDialog.show();
        searchedList.clear();
        compositeDisposable.add(iMyService.searchReviews(makeSpinnerSelection.getSelectedItem().toString(),
                                                        modelSpinnerSelection.getSelectedItem().toString(),
                                                        versionSpinnerSelection.getSelectedItem().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //my own
                        JSONArray jsonArray = new JSONArray(JSON.serialize(response));

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        JSONArray jsonArray1 = jsonObject.getJSONArray("comments");

                        int i=0;
                        while (i < jsonArray1.length()){
                            searchedList.add(jsonArray1.getJSONObject(i));
                            i++;
                        }
                        initRecyclerView();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(searchCarReviews.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));


    }

    private void getDataInMakeSpinner(){
        mDialog.setMessage("Please wait...");
        mDialog.show();
        makeSpinnerItems.clear();
        makeSpinnerItems.add("Select Make *");
        makeSpinnerItems.add(".....");
        if(isOnline()){
            compositeDisposable.add(iMyService.getMakes()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object s) throws Exception {

                            //my own
                            JSONArray jsonArray = new JSONArray(JSON.serialize(s));
                            int i=0;
                            while(i<jsonArray.length()){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                makeSpinnerItems.add(jsonObject.get("_id").toString());
                                i++;
                            }
                            ArrayAdapter<String> adapter0 = new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.spinner_layout, makeSpinnerItems);
                            adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            makeSpinnerSelection.setAdapter(adapter0);
                            mDialog.dismiss();
                            makeSpinnerSelection.setFocusable(true);
                            makeSpinnerSelection.setFocusableInTouchMode(true);
                            makeSpinnerSelection.requestFocus();
                            makeSpinnerSelection.performClick();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(searchCarReviews.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }else{
            Toast.makeText(searchCarReviews.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataInModelSpinner(String selectedMake){
        modelSpinnerItems.clear();
        modelSpinnerItems.add("Select Model *");
        modelSpinnerItems.add(".....");
        if(isOnline()){
            compositeDisposable.add(iMyService.getModels(selectedMake)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object s) throws Exception {
                            //my own
                            JSONArray jsonArray = new JSONArray(JSON.serialize(s));
                            int i=0;
                            while(i<jsonArray.length()){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (!modelSpinnerItems.contains(jsonObject.get("_id").toString())){
                                    modelSpinnerItems.add(jsonObject.get("_id").toString());
                                }
                                i++;
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.spinner_layout, modelSpinnerItems);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            modelSpinnerSelection.setAdapter(adapter);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(searchCarReviews.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }else{
            Toast.makeText(searchCarReviews.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataInVersionSpinner(String selectedModel){
        versionSpinnerItems.clear();
        versionSpinnerItems.add("Select Version *");
        versionSpinnerItems.add(".....");
        if(isOnline()){
            compositeDisposable.add(iMyService.getVersions(selectedModel)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object s) throws Exception {
                            //my own
                            JSONArray jsonArray = new JSONArray(JSON.serialize(s));
                            int i=0;
                            while(i<jsonArray.length()){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (!versionSpinnerItems.contains(jsonObject.get("version").toString())){
                                    versionSpinnerItems.add(jsonObject.get("version").toString());
                                }
                                i++;
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.spinner_layout, versionSpinnerItems);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            versionSpinnerSelection.setAdapter(adapter);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(searchCarReviews.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }else{
            Toast.makeText(searchCarReviews.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataInOtherSpinners() {
        modelSpinnerItems.add("Select \"Make\" first");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_layout, modelSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinnerSelection.setAdapter(adapter);

        versionSpinnerItems.add("Select \"Model\" first");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_layout, versionSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        versionSpinnerSelection.setAdapter(adapter);
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

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void initRecyclerView(){
        RecyclerView recyclerViewSavedAds = findViewById(R.id.recyclerViewReviews);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSavedAds.setLayoutManager(layoutManager4);

        recyclerViewAdapterRecommender adapter = new recyclerViewAdapterRecommender(this,searchedList);
        recyclerViewSavedAds.setItemAnimator( new DefaultItemAnimator());
        recyclerViewSavedAds.setAdapter(adapter);
        R_label.setVisibility(View.VISIBLE);
        if(searchedList.isEmpty()){
            R_label.setText("No Reviews for this car");
        }
        else{
            R_label.setText("Reviews");
        }
        mDialog.dismiss();
    }
}
