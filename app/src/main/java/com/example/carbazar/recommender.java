package com.example.carbazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
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
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.example.carbazar.Models.common;
import com.example.carbazar.Models.priceRecommender;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterMyAds;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterRecommender;
import com.mongodb.util.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class recommender extends AppCompatActivity {

    private Toolbar toolbar;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    private List<JSONObject> recommendedList;
    ProgressDialog mDialog;

    private CrystalRangeSeekbar seekBarPrice;
    private AppCompatEditText price1;
    private AppCompatEditText price2;

    private AppCompatButton best_model_btn;
    private AppCompatButton best_brand_btn;
    private AppCompatButton best_mileage_btn;
    private AppCompatButton price_range_btn;

    private AppCompatTextView RC_label;

    private AppCompatButton write_review_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommender);

        toolbar = findViewById(R.id.toolbar);

        seekBarPrice = findViewById(R.id.seekBarPrice);
        price1 = findViewById(R.id.price1_field);
        price2 = findViewById(R.id.price2_field);

        best_model_btn = findViewById(R.id.best_model_btn);
        best_brand_btn = findViewById(R.id.best_brand_btn);
        best_mileage_btn = findViewById(R.id.best_mileage_btn);
        price_range_btn = findViewById(R.id.price_range_btn);

        RC_label = findViewById(R.id.RC_label);
        RC_label.setVisibility(View.GONE);

        write_review_btn = findViewById(R.id.write_review_btn);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Car Recommendations");

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        recommendedList = new ArrayList<JSONObject>();

        mDialog = new ProgressDialog(recommender.this);

        write_review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(recommender.this, writeReview.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        seekBarPrice.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                price1.setText("RS " + NumberFormat.getInstance().format(minValue));
                price2.setText("RS " + NumberFormat.getInstance().format(maxValue));
            }
        });

        price1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
                    try {
                        price1.setText("RS " + NumberFormat.getInstance()
                                .format( nf.parse(price1.getText().toString())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        price2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
                    if(!price2.getText().toString().contentEquals("RS 5,000,000+")){
                        try {
                            price2.setText("RS " + NumberFormat.getInstance()
                                    .format( nf.parse(price2.getText().toString())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        best_model_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendedList.clear();
                mDialog.setMessage("Please wait...");
                mDialog.show();
                compositeDisposable.add(iMyService.bestModel()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object response) throws Exception {
                                //my own
                                JSONArray jsonArray = new JSONArray(JSON.serialize(response));
                                int i=0;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                recommendedList.add(jsonObject);
                                initRecyclerView();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(recommender.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }));
            }
        });

        best_brand_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendedList.clear();
                mDialog.setMessage("Please wait...");
                mDialog.show();
                compositeDisposable.add(iMyService.bestBrand()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object response) throws Exception {
                                //my own
                                JSONArray jsonArray = new JSONArray(JSON.serialize(response));
                                int i=0;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                recommendedList.add(jsonObject);
                                initRecyclerView();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(recommender.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }));
            }
        });

        best_mileage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendedList.clear();
                mDialog.setMessage("Please wait...");
                mDialog.show();
                compositeDisposable.add(iMyService.bestMileage()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object response) throws Exception {
                                //my own
                                JSONArray jsonArray = new JSONArray(JSON.serialize(response));
                                int i=0;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                recommendedList.add(jsonObject);
                                initRecyclerView();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(recommender.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }));
            }
        });

        price_range_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendedList.clear();
                mDialog.setMessage("Please wait...");
                mDialog.show();

                String p1 = price1.getText().toString();
                p1 = p1.replaceAll("RS","").replace(" ", "").replace(",", "");
                String p2 = price2.getText().toString();
                p2 = p2.replaceAll("RS","").replace(" ", "").replace(",", "");

                priceRecommender PR = new priceRecommender(p1, p2);
                compositeDisposable.add(iMyService.bestPrice(PR)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object response) throws Exception {
                                //my own
                                JSONArray jsonArray = new JSONArray(JSON.serialize(response));
                                int i=0;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                recommendedList.add(jsonObject);
                                initRecyclerView();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(recommender.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }));
            }
        });



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
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
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

    private void initRecyclerView(){
        RecyclerView recyclerViewSavedAds = findViewById(R.id.recyclerViewRecommender);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSavedAds.setLayoutManager(layoutManager4);

        recyclerViewAdapterRecommender adapter = new recyclerViewAdapterRecommender(this,recommendedList);
        recyclerViewSavedAds.setItemAnimator( new DefaultItemAnimator());
        recyclerViewSavedAds.setAdapter(adapter);
        RC_label.setVisibility(View.VISIBLE);
        mDialog.dismiss();
    }

}
