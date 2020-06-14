package com.example.carbazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import android.widget.SearchView;
import android.widget.Toast;

import com.example.carbazar.Interfaces.OnOptionClickListener;
import com.example.carbazar.Models.common;
import com.example.carbazar.Models.user;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterCarBazar;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterOLX;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterPKMotors;
import com.example.carbazar.recyclerViewAdapters.recyclerViewAdapterPakWheels;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mongodb.util.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, OnOptionClickListener {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;
    IMyService iMyServiceFlask;

    private List<JSONObject> postsListCarBazar;
    private List<String> postsListOLX;
    private List<String> postsListPakWheels;
    private List<String> postsListPKMotors;
    private List<JSONObject> postsListBuyerAds;
    ProgressDialog mDialog;
    ProgressDialog mDialog1;

    private SearchView searchView;

    private AppCompatTextView carBazarLable;
    private AppCompatTextView OLXLable;
    private AppCompatTextView PakWheelsLable;
    private AppCompatTextView PKMotorsLable;
    private AppCompatTextView buyers_posts_Label;

    private CardView filtersBtn;

    String queryFilter = "";
    ArrayList<String> filtersValues = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        android.view.Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CarBazar");

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        postsListCarBazar = new ArrayList<JSONObject>();
        postsListOLX = new ArrayList<String>();
        postsListPakWheels = new ArrayList<String>();
        postsListPKMotors = new ArrayList<String>();
        postsListBuyerAds = new ArrayList<JSONObject>();
        mDialog = new ProgressDialog(MainActivity.this);
        mDialog1 = new ProgressDialog(MainActivity.this);

        searchView = findViewById(R.id.searchView);

        carBazarLable = findViewById(R.id.CarBazar_Label);
        OLXLable = findViewById(R.id.olx_Label);
        PakWheelsLable = findViewById(R.id.pak_wheels_Label);
        PKMotorsLable = findViewById(R.id.pk_motors_Label);
        buyers_posts_Label = findViewById(R.id.buyers_posts_Label);

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

        mDialog1.setMessage("Please wait...");
        mDialog1.show();

        Retrofit retrofitFlask = RetrofitClientFlask.getInstance();
        iMyServiceFlask = retrofitFlask.create(IMyService.class);
        if (isOnline()){
            File file = new File(getFilesDir(), "my_sensitive_data.txt");
            if(file.exists()){
                StringBuffer stringBuffer = new StringBuffer();
                try (BufferedReader reader =
                             new BufferedReader(new FileReader(file))) {

                    String line = reader.readLine();
//                while (line != null) {
//                    Toast.makeText(profile.this, line, Toast.LENGTH_SHORT).show();
//                    stringBuffer.append(line).append('\n');
//                    line = reader.readLine();
//                }
                    String[] details = line.split("_\\+_");
                    user USER = new user();
                    USER.setToken(details[0]);
                    USER.setId(details[1]);
                    USER.setPassword(details[2]);

                    common.currentUser = USER;

                    if (isOnline()){
                        String token = "Bearer "+ common.currentUser.getToken();
                        String id = common.currentUser.getId().replaceAll(" ","");
                        compositeDisposable.add(iMyService.profile(token,id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Object>() {
                                    @Override
                                    public void accept(Object s) throws Exception {
                                        String response = "";
                                        if(s.toString().contains("error")){
                                            response = s.toString().replace("{error=","");
                                            response=response.replace("}","");
                                            Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();
                                        }
                                        if(s.toString().contains("message")){
                                            response = s.toString().replace("{message=","");
                                            response=response.replace("}","");
                                            Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();
                                        }

                                        //my own
                                        JSONObject jsonObject = new JSONObject(JSON.serialize(s));
                                        //String saint = jsonObject.getString("email");

                                        common.currentUser.setName(jsonObject.getString("name"));
                                        common.currentUser.setEmail(jsonObject.getString("email"));
                                        common.currentUser.setPhone(jsonObject.getString("phone"));
                                        common.currentUser.setRole(jsonObject.getString("role"));
                                        if (jsonObject.has("photo")){
                                            common.currentUser.setPhoto(jsonObject.getString("photo"));
                                        }

                                        mDialog1.dismiss();
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Toast.makeText(MainActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }));
                    }else {
                        Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    // Error occurred opening raw file for reading.
                    Toast.makeText(MainActivity.this, "File open error", Toast.LENGTH_SHORT).show();
                }
            }

            Bundle extras = getIntent().getExtras();
            if(extras!= null){
                ArrayList<String> allFilters = extras.getStringArrayList("allFilters");
                filtersValues = allFilters;
                getCarBazarData(allFilters);
                getScrappeddataOLX(allFilters);
                getScrappeddataPakWheels(allFilters);
                getScrappeddataPKMotors(allFilters);
                getBuyersAdsData();
            }
            else{
                getCarBazarData();
                getBuyersAdsData();
                getScrappeddataOLX();
                getScrappeddataPakWheels();
                getScrappeddataPKMotors();
            }
        }
        else{
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(search.this, query,Toast.LENGTH_SHORT).show();
                if (isOnline()){
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    carBazarLable.setText("CarBazar Ads");
                    OLXLable.setText("OLX Ads");
                    PakWheelsLable.setText("PakWheels Ads");
                    PKMotorsLable.setText("PKMotors Ads");

                    postsListCarBazar.clear();
                    getCarBazarData(query);

                    postsListOLX.clear();
                    getScrappeddataOLX(query);
                    postsListPakWheels.clear();
                    getScrappeddataPakWheels(query);
                    postsListPKMotors.clear();
                    getScrappeddataPKMotors(query);

                    /////////////////
                    getBuyersAdsData();
                }
                else{
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryFilter = newText;
                return false;
            }
        });

//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus){
//                    bottomNavigationView.setVisibility(View.INVISIBLE);
//                }
//                else {
//                    bottomNavigationView.setVisibility(View.VISIBLE);
//                }
//            }
//        });


        filtersBtn = findViewById(R.id.filter_btn);
        filtersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, filters.class);
                intent.putExtra("queryFilter", queryFilter);
                intent.putStringArrayListExtra("filtersValues", filtersValues);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                moveTaskToBack(true);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.home_nav:
                //
                return true;

            case R.id.post_nav:
                //
                Intent intent1 = new Intent(MainActivity.this, postAd.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.chatbot_nav:
                //
                Intent intent3 = new Intent(MainActivity.this, chatbot.class);
                startActivity(intent3);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.ar_vr_nav:
                //
                Intent intent2 = new Intent(MainActivity.this, ARHome.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.account_nav:
                //
                Intent intent = new Intent(MainActivity.this, account.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;


        }
        return false;
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void getCarBazarData(){
        mDialog.setMessage("Please wait...");
        mDialog.show();
        carBazarLable.setText("CarBazar Ads  (Loading... )");
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
                            postsListCarBazar.add(jsonObject);
                            i++;
                        }
                        carBazarLable.setText("CarBazar Ads");
                        initRecyclerViewCarBazar();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void getBuyersAdsData(){
        buyers_posts_Label.setText("Buyer Ads  (Loading... )");
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
                            postsListBuyerAds.add(jsonObject);
                            i++;
                        }
                        buyers_posts_Label.setText("Buyer Ads");
                        initRecyclerViewBuyerAds();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void getScrappeddataOLX(){

        OLXLable.setText("OLX Ads  (Loading... )");
        compositeDisposable.add(iMyServiceFlask.OLXHome()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();

                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        //String saint = jsonObject.getString("email");

                        jsonObject.length();
                        int i=1;
                        while(i<=jsonObject.length()){
                            String ss =  jsonObject.getString(Integer.toString(i));
                            ss = ss.substring(1,ss.length()-1);
                            postsListOLX.add(ss);
                            i++;
                        }
                        OLXLable.setText("OLX Ads");
                        initRecyclerViewOLX();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void getScrappeddataPakWheels(){

        PakWheelsLable.setText("PakWheels Ads  (Loading... )");
        compositeDisposable.add(iMyServiceFlask.PakWheelsHome()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();

                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        //String saint = jsonObject.getString("email");

                        jsonObject.length();
                        int i=1;
                        while(i<=jsonObject.length()){
                            String ss =  jsonObject.getString(Integer.toString(i));
                            postsListPakWheels.add(ss);
                            i++;
                        }
                        PakWheelsLable.setText("PakWheels Ads");
                        initRecyclerViewPakWheels();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void getScrappeddataPKMotors(){

        PKMotorsLable.setText("PKMotors Ads  (Loading... )");
        compositeDisposable.add(iMyServiceFlask.PKMotorsHome()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();
                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        //String saint = jsonObject.getString("email");
                        jsonObject.length();
                        int i=1;
                        while(i<=jsonObject.length()){
                            String ss =  jsonObject.getString(Integer.toString(i));
                            postsListPKMotors.add(ss);
                            i++;
                        }
                        PKMotorsLable.setText("PKMotors Ads");
                        initRecyclerViewPKMotors();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void getCarBazarData(String query){
        mDialog.setMessage("Please wait...");
        mDialog.show();

        carBazarLable.setText("CarBazar Ads  (Loading... )");
        compositeDisposable.add(iMyService.searchCarBazaar(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();

                        //my own
                        JSONArray jsonArray = new JSONArray(JSON.serialize(response));
                        int i=0;
                        while(i<jsonArray.length()){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            postsListCarBazar.add(jsonObject);
                            i++;
                        }
                        if(postsListCarBazar.isEmpty()){
                            carBazarLable.setText("CarBazar Ads (No Results Found)");
                        }
                        else{
                            carBazarLable.setText("CarBazar Ads");
                        }
                        initRecyclerViewCarBazar();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void getScrappeddataOLX(String query){
        mDialog.setMessage("Please wait...");
        mDialog.show();

        OLXLable.setText("OLX Ads  (Loading... )");
        compositeDisposable.add(iMyServiceFlask.OLXSearch(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();

                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        //String saint = jsonObject.getString("email");

                        jsonObject.length();
                        int i=1;
                        while(i<=jsonObject.length()){
                            String ss =  jsonObject.getString(Integer.toString(i));
                            ss = ss.substring(1,ss.length()-1);
                            postsListOLX.add(ss);
                            i++;
                        }
                        if(postsListOLX.isEmpty()){
                            OLXLable.setText("OLX Ads  (No Results Found)");
                        }
                        else{
                            OLXLable.setText("OLX Ads");
                        }
                        initRecyclerViewOLX();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void getScrappeddataPakWheels(String query){
        PakWheelsLable.setText("PakWheels Ads  (Loading... )");
        compositeDisposable.add(iMyServiceFlask.PakWheelsSearch(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();

                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        //String saint = jsonObject.getString("email");

                        jsonObject.length();
                        int i=1;
                        while(i<=jsonObject.length()){
                            String ss =  jsonObject.getString(Integer.toString(i));
                            postsListPakWheels.add(ss);
                            i++;
                        }
                        if(postsListPakWheels.isEmpty()){
                            PakWheelsLable.setText("PakWheels Ads  (No Results Found)");
                        }
                        else{
                            PakWheelsLable.setText("PakWheels Ads");
                        }
                        initRecyclerViewPakWheels();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void getScrappeddataPKMotors(String query){
        PKMotorsLable.setText("PKMotors Ads  (Loading... )");
        compositeDisposable.add(iMyServiceFlask.PKMotorsSearch(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();
                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        //String saint = jsonObject.getString("email");
                        jsonObject.length();
                        int i=1;
                        while(i<=jsonObject.length()){
                            String ss =  jsonObject.getString(Integer.toString(i));
                            postsListPKMotors.add(ss);
                            i++;
                        }
                        if(postsListPKMotors.isEmpty()){
                            PKMotorsLable.setText("PKMotors Ads  (No Results Found)");
                        }
                        else{
                            PKMotorsLable.setText("PKMotors Ads");
                        }
                        initRecyclerViewPKMotors();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void getCarBazarData(ArrayList<String> allFilters){
        mDialog.setMessage("Please wait...");
        mDialog.show();

        carBazarLable.setText("CarBazar Ads  (Loading... )");

        String kmDriven123 = allFilters.get(2);
        if (allFilters.get(2).contentEquals("0 - 500000+") || allFilters.get(2).contentEquals("0  -  500000+") || allFilters.get(2).contentEquals("0-500000+")){
            kmDriven123 = "";
        }

        String[] price = allFilters.get(0).split("-");
        if (allFilters.get(0).contentEquals("0-5000000+")){
            price[0] = "";
            price[1] = "";
        }

        String[] year  = allFilters.get(1).split("-");
        if (allFilters.get(1).contentEquals("1970-2020")){
            year[0] = "";
            year[1] = "";
        }

        String[] engCapacity = allFilters.get(3).split("-");
        if (allFilters.get(3).contentEquals("600-3500")){
            engCapacity[0] = "";
        }

        compositeDisposable.add(iMyService.CarBazaarFilters(price[0],
                price[1],
                year[0],
                year[1],
                kmDriven123,
                engCapacity[0],
                allFilters.get(4),
                allFilters.get(5),
                allFilters.get(6),
                allFilters.get(7),
                allFilters.get(8),
                allFilters.get(9),
                allFilters.get(10),
                allFilters.get(11),
                allFilters.get(12))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();

                        //my own
                        JSONArray jsonArray = new JSONArray(JSON.serialize(response));
                        int i=0;
                        while(i<jsonArray.length()){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            postsListCarBazar.add(jsonObject);
                            i++;
                        }
                        if(postsListCarBazar.isEmpty()){
                            carBazarLable.setText("CarBazar Ads (No Results Found)");
                        }
                        else{
                            carBazarLable.setText("CarBazar Ads");
                        }
                        initRecyclerViewCarBazar();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }


    private void getScrappeddataOLX(ArrayList<String> allFilters){
        mDialog.setMessage("Please wait...");
        mDialog.show();

        OLXLable.setText("OLX Ads  (Loading... )");
        compositeDisposable.add(iMyServiceFlask.olxFilters(allFilters.get(0),
                allFilters.get(1),
                allFilters.get(2),
                allFilters.get(3),
                allFilters.get(4),
                allFilters.get(5),
                allFilters.get(6),
                allFilters.get(7),
                allFilters.get(8),
                allFilters.get(9),
                allFilters.get(10),
                allFilters.get(11),
                allFilters.get(12))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();

                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        //String saint = jsonObject.getString("email");

                        jsonObject.length();
                        int i=1;
                        while(i<=jsonObject.length()){
                            String ss =  jsonObject.getString(Integer.toString(i));
                            ss = ss.substring(1,ss.length()-1);
                            postsListOLX.add(ss);
                            i++;
                        }
                        if(postsListOLX.isEmpty()){
                            OLXLable.setText("OLX Ads  (No Results Found)");
                        }
                        else{
                            OLXLable.setText("OLX Ads");
                        }
                        initRecyclerViewOLX();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void getScrappeddataPakWheels(ArrayList<String> allFilters){
        PakWheelsLable.setText("PakWheels Ads  (Loading... )");
        compositeDisposable.add(iMyServiceFlask.pakWheelsFilters(allFilters.get(0),
                allFilters.get(1),
                allFilters.get(2),
                allFilters.get(3),
                allFilters.get(4),
                allFilters.get(5),
                allFilters.get(6),
                allFilters.get(7),
                allFilters.get(8),
                allFilters.get(9),
                allFilters.get(10),
                allFilters.get(11),
                allFilters.get(12))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();

                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        //String saint = jsonObject.getString("email");

                        jsonObject.length();
                        int i=1;
                        while(i<=jsonObject.length()){
                            String ss =  jsonObject.getString(Integer.toString(i));
                            postsListPakWheels.add(ss);
                            i++;
                        }
                        if(postsListPakWheels.isEmpty()){
                            PakWheelsLable.setText("PakWheels Ads  (No Results Found)");
                        }
                        else{
                            PakWheelsLable.setText("PakWheels Ads");
                        }
                        initRecyclerViewPakWheels();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void getScrappeddataPKMotors(ArrayList<String> allFilters){
        PKMotorsLable.setText("PKMotors Ads  (Loading... )");
        compositeDisposable.add(iMyServiceFlask.pkMotorsFilters(allFilters.get(0),
                allFilters.get(1),
                allFilters.get(2),
                allFilters.get(3),
                allFilters.get(4),
                allFilters.get(5),
                allFilters.get(6),
                allFilters.get(7),
                allFilters.get(8),
                allFilters.get(9),
                allFilters.get(10),
                allFilters.get(11),
                allFilters.get(12))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //Toast.makeText(MainActivity.this,""+response, Toast.LENGTH_SHORT).show();
                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        //String saint = jsonObject.getString("email");
                        jsonObject.length();
                        int i=1;
                        while(i<=jsonObject.length()){
                            String ss =  jsonObject.getString(Integer.toString(i));
                            postsListPKMotors.add(ss);
                            i++;
                        }
                        if(postsListPKMotors.isEmpty()){
                            PKMotorsLable.setText("PKMotors Ads  (No Results Found)");
                        }
                        else{
                            PKMotorsLable.setText("PKMotors Ads");
                        }
                        initRecyclerViewPKMotors();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public void onOptionClick(String Option, String Label) {

    }

    @Override
    public void onpostClick(String postID, String postType) {
        if (postType.contentEquals("Seller")){
            Intent intent = new Intent(MainActivity.this, detailedPostCarBazar.class);
            intent.putExtra("postID", postID);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        else if (postType.contentEquals("Buyer")){
            Intent intent2 = new Intent(MainActivity.this, detailedBuyerPost.class);
            intent2.putExtra("postID", postID);
            startActivity(intent2);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void initRecyclerViewOLX(){

        RecyclerView recyclerViewOlx = findViewById(R.id.recyclerViewOlx);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewOlx.setLayoutManager(layoutManager);

        recyclerViewAdapterOLX adapter = new recyclerViewAdapterOLX(this,postsListOLX);
        recyclerViewOlx.setItemAnimator( new DefaultItemAnimator());
        recyclerViewOlx.setAdapter(adapter);
        recyclerViewOlx.getLayoutManager().scrollToPosition(Integer.MAX_VALUE / 2);
        mDialog.dismiss();
    }

    private void initRecyclerViewPakWheels(){

        RecyclerView recyclerViewPakWheels = findViewById(R.id.recyclerViewPakWheels);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPakWheels.setLayoutManager(layoutManager2);

        recyclerViewAdapterPakWheels adapter = new recyclerViewAdapterPakWheels(this,postsListPakWheels);
        recyclerViewPakWheels.setItemAnimator( new DefaultItemAnimator());
        recyclerViewPakWheels.setAdapter(adapter);
        recyclerViewPakWheels.getLayoutManager().scrollToPosition(Integer.MAX_VALUE / 2);
        mDialog.dismiss();
    }

    private void initRecyclerViewPKMotors(){
        RecyclerView recyclerViewPkMotors = findViewById(R.id.recyclerViewPkMotors);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPkMotors.setLayoutManager(layoutManager3);

        recyclerViewAdapterPKMotors adapter = new recyclerViewAdapterPKMotors(this,postsListPKMotors);
        recyclerViewPkMotors.setItemAnimator( new DefaultItemAnimator());
        recyclerViewPkMotors.setAdapter(adapter);
        recyclerViewPkMotors.getLayoutManager().scrollToPosition(Integer.MAX_VALUE / 2);
        mDialog.dismiss();
    }

    private void initRecyclerViewCarBazar(){
        RecyclerView recyclerViewCarBazar = findViewById(R.id.recyclerViewCarBazar);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCarBazar.setLayoutManager(layoutManager4);

        recyclerViewAdapterCarBazar adapter = new recyclerViewAdapterCarBazar(this,postsListCarBazar);
        recyclerViewCarBazar.setItemAnimator( new DefaultItemAnimator());
        recyclerViewCarBazar.setAdapter(adapter);
        recyclerViewCarBazar.getLayoutManager().scrollToPosition(Integer.MAX_VALUE / 2);
        mDialog.dismiss();
    }

    private void initRecyclerViewBuyerAds(){
        RecyclerView recyclerViewBuyerPosts = findViewById(R.id.recyclerViewBuyerPosts);
        LinearLayoutManager layoutManager5 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewBuyerPosts.setLayoutManager(layoutManager5);

        recyclerViewAdapterCarBazar adapter = new recyclerViewAdapterCarBazar(this,postsListBuyerAds);
        recyclerViewBuyerPosts.setItemAnimator( new DefaultItemAnimator());
        recyclerViewBuyerPosts.setAdapter(adapter);
        recyclerViewBuyerPosts.getLayoutManager().scrollToPosition(Integer.MAX_VALUE / 2);
        mDialog.dismiss();
    }
}
