package com.example.carbazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.carbazar.Models.common;
import com.example.carbazar.Models.reviewLDmodel;
import com.example.carbazar.Models.writeReviewModel;
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

public class writeReview extends AppCompatActivity {

    private Toolbar toolbar;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    ProgressDialog mDialog;

    private Spinner makeSpinnerSelection;
    private Spinner modelSpinnerSelection;
    private Spinner versionSpinnerSelection;
    private Spinner familiaritySpinnerSelection;

    private AppCompatRatingBar brand_rating;
    private AppCompatRatingBar price_value_rating;
    private AppCompatRatingBar mileage_rating;
    private AppCompatRatingBar model_rating;

    private AppCompatEditText review_title_field;
    private AppCompatEditText description_field;

    private AppCompatButton post_btn;

    List<String> makeSpinnerItems;
    List<String> modelSpinnerItems;
    List<String> versionSpinnerItems;
    List<String> familiaritySpinnerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Write a Review");

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        mDialog = new ProgressDialog(writeReview.this);

        makeSpinnerSelection = findViewById(R.id.makeSpinnerSelection);
        modelSpinnerSelection = findViewById(R.id.modelSpinnerSelection);
        versionSpinnerSelection = findViewById(R.id.versionSpinnerSelection);
        familiaritySpinnerSelection = findViewById(R.id.familiaritySpinnerSelection);

        brand_rating = findViewById(R.id.brand_rating);
        price_value_rating = findViewById(R.id.price_value_rating);
        mileage_rating = findViewById(R.id.mileage_rating);
        model_rating = findViewById(R.id.model_rating);

        review_title_field = findViewById(R.id.review_title_field);
        description_field = findViewById(R.id.description_field);

        post_btn = findViewById(R.id.post_btn);

        makeSpinnerItems = new ArrayList<String>();
        modelSpinnerItems = new ArrayList<String>();
        versionSpinnerItems = new ArrayList<String>();
        familiaritySpinnerItems = new ArrayList<String>();

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
                    if(common.currentUser == null){
                        Toast.makeText(writeReview.this, "Sign in first", Toast.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(writeReview.this, signin.class);
                        startActivity(intent3);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    else{
                        sendData();
                    }
                }else{
                    Toast.makeText(writeReview.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendData() {

        if (makeSpinnerSelection.getSelectedItem() == null) {
            Toast.makeText(writeReview.this, "Select Make", Toast.LENGTH_SHORT).show();
            return;
        }

        if (makeSpinnerSelection.getSelectedItemPosition() == 0 || makeSpinnerSelection.getSelectedItemPosition() == 1) {
            Toast.makeText(writeReview.this, "Select Make", Toast.LENGTH_SHORT).show();
            makeSpinnerSelection.setFocusable(true);
            makeSpinnerSelection.setFocusableInTouchMode(true);
            makeSpinnerSelection.requestFocus();
            makeSpinnerSelection.performClick();
            return;
        }

        if (modelSpinnerSelection.getSelectedItemPosition() == 0 || modelSpinnerSelection.getSelectedItemPosition() == 1) {
            Toast.makeText(writeReview.this, "Select Model", Toast.LENGTH_SHORT).show();
            modelSpinnerSelection.setFocusable(true);
            modelSpinnerSelection.setFocusableInTouchMode(true);
            modelSpinnerSelection.requestFocus();
            modelSpinnerSelection.performClick();
            return;
        }

        if (versionSpinnerSelection.getSelectedItemPosition() == 0 || versionSpinnerSelection.getSelectedItemPosition() == 1) {
            Toast.makeText(writeReview.this, "Select Registration City", Toast.LENGTH_SHORT).show();
            versionSpinnerSelection.setFocusable(true);
            versionSpinnerSelection.setFocusableInTouchMode(true);
            versionSpinnerSelection.requestFocus();
            versionSpinnerSelection.performClick();
            return;
        }

        if (review_title_field.getText().toString().contentEquals("")) {
            Toast.makeText(writeReview.this, "Enter a valid title", Toast.LENGTH_SHORT).show();
            review_title_field.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            return;
        }

        if (familiaritySpinnerSelection.getSelectedItemPosition() == 0 || familiaritySpinnerSelection.getSelectedItemPosition() == 1) {
            Toast.makeText(writeReview.this, "Select Familiarity", Toast.LENGTH_SHORT).show();
            familiaritySpinnerSelection.setFocusable(true);
            familiaritySpinnerSelection.setFocusableInTouchMode(true);
            familiaritySpinnerSelection.requestFocus();
            familiaritySpinnerSelection.performClick();
            return;
        }

        writeReviewModel writeReviewModel1 = new writeReviewModel();
        writeReviewModel1.setMake(makeSpinnerSelection.getSelectedItem().toString());
        writeReviewModel1.setModel(modelSpinnerSelection.getSelectedItem().toString());
        writeReviewModel1.setVersion(versionSpinnerSelection.getSelectedItem().toString());
        writeReviewModel1.setTitle(review_title_field.getText().toString());
        writeReviewModel1.setDescription(description_field.getText().toString());
        writeReviewModel1.setPriceReview(price_value_rating.getRating()+"");
        writeReviewModel1.setModelReview(model_rating.getRating()+"");
        writeReviewModel1.setBrandReview(brand_rating.getRating()+"");
        writeReviewModel1.setMileageReview(mileage_rating.getRating()+"");
        writeReviewModel1.setFamiliarity(familiaritySpinnerSelection.getSelectedItem().toString());
        writeReviewModel1.setUser_id(common.currentUser.getId().replaceAll(" ",""));
        writeReviewModel1.setUsername(common.currentUser.getName());


        compositeDisposable.add(iMyService.createReview(writeReviewModel1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object s) throws Exception {

                        //my own
//                        JSONArray jsonArray = new JSONArray(JSON.serialize(s));
                        LayoutInflater inflater = (LayoutInflater) writeReview.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View formElementsView = inflater.inflate(R.layout.contact_seller_alert,null, false);

                        final AppCompatTextView titleTextView = formElementsView.findViewById(R.id.title_Label);
                        final AppCompatEditText nameEditText = formElementsView.findViewById(R.id.name_field);
                        final AppCompatEditText emailEditText = formElementsView.findViewById(R.id.email_field_alert);
                        final AppCompatEditText messageEditText = formElementsView.findViewById(R.id.message_field);

                        titleTextView.setText("We would like your final impression about this car!");
                        nameEditText.setVisibility(View.GONE);
                        emailEditText.setVisibility(View.GONE);
                        messageEditText.setVisibility(View.GONE);

                        final AlertDialog dialog = new AlertDialog.Builder(writeReview.this).setView(formElementsView)
                                .setTitle("Overall Impression")
                                .setPositiveButton("                   ", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        reviewLDmodel reviewLDmodel1 = new reviewLDmodel();
                                        reviewLDmodel1.setMake(makeSpinnerSelection.getSelectedItem().toString());
                                        reviewLDmodel1.setModel(modelSpinnerSelection.getSelectedItem().toString());
                                        reviewLDmodel1.setVersion(versionSpinnerSelection.getSelectedItem().toString());
                                        reviewLDmodel1.setUserId(common.currentUser.getId().replaceAll(" ",""));
                                        compositeDisposable.add(iMyService.like(reviewLDmodel1)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<Object>() {
                                                    @Override
                                                    public void accept(Object s) throws Exception {

                                                        //my own
//                                                        JSONArray jsonArray = new JSONArray(JSON.serialize(s));
                                                        finish();
                                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                        Toast.makeText(writeReview.this, "Review Submitted", Toast.LENGTH_SHORT).show();
                                                    }
                                                }, new Consumer<Throwable>() {
                                                    @Override
                                                    public void accept(Throwable throwable) throws Exception {
                                                        Toast.makeText(writeReview.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }));
                                    }
                                })
                                .setNegativeButton(" ", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        reviewLDmodel reviewLDmodel1 = new reviewLDmodel();
                                        reviewLDmodel1.setMake(makeSpinnerSelection.getSelectedItem().toString());
                                        reviewLDmodel1.setModel(modelSpinnerSelection.getSelectedItem().toString());
                                        reviewLDmodel1.setVersion(versionSpinnerSelection.getSelectedItem().toString());
                                        reviewLDmodel1.setUserId(common.currentUser.getId().replaceAll(" ",""));
                                        compositeDisposable.add(iMyService.dislike(reviewLDmodel1)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<Object>() {
                                                    @Override
                                                    public void accept(Object s) throws Exception {

                                                        //my own
//                                                        JSONArray jsonArray = new JSONArray(JSON.serialize(s));
                                                        finish();
                                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                        Toast.makeText(writeReview.this, "Review Submitted", Toast.LENGTH_SHORT).show();

                                                    }
                                                }, new Consumer<Throwable>() {
                                                    @Override
                                                    public void accept(Throwable throwable) throws Exception {
                                                        Toast.makeText(writeReview.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }));
                                    }
                                }).create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                                Drawable drawable = getApplicationContext().getResources().getDrawable(
                                        R.drawable.liked);

                                drawable.setBounds((int) (drawable.getIntrinsicWidth() * 0.5),
                                        0, (int) (drawable.getIntrinsicWidth() * 1.5),
                                        drawable.getIntrinsicHeight());
                                button.setCompoundDrawables(drawable, null, null, null);

                                Button button2 = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                                Drawable drawable2 = getApplicationContext().getResources().getDrawable(
                                        R.drawable.dislike);

                                drawable2.setBounds((int) (drawable2.getIntrinsicWidth() * 0.5),
                                        0, (int) (drawable2.getIntrinsicWidth() * 1.5),
                                        drawable2.getIntrinsicHeight());
                                button2.setCompoundDrawables(drawable2, null, null, null);
                            }
                        });

                        dialog.show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(writeReview.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(writeReview.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }else{
            Toast.makeText(writeReview.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(writeReview.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }else{
            Toast.makeText(writeReview.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(writeReview.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }else{
            Toast.makeText(writeReview.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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

        familiaritySpinnerItems.add("Select Familiarity");
        familiaritySpinnerItems.add(".....");
        familiaritySpinnerItems.add("I owned this car");
        familiaritySpinnerItems.add("I drove this car");
        familiaritySpinnerItems.add("I never owned neither drove this car");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_layout, familiaritySpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        familiaritySpinnerSelection.setAdapter(adapter);
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
}
