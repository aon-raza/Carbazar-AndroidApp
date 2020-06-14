package com.example.carbazar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.carbazar.Models.common;
import com.example.carbazar.Models.user;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mongodb.util.JSON;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class account extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private AppCompatTextView viewAndEdit;
    private AppCompatTextView user_name;
    private CircleImageView user_image;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    private AppCompatTextView savedAdsBtn;
    private AppCompatTextView privacyBtn;
    private AppCompatTextView notificationsBtn;
    private AppCompatTextView contactBtn;
    private AppCompatTextView helpAndGuidelineBtn;
    private AppCompatTextView rateBtn;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        android.view.Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        toolbar = findViewById(R.id.toolbar);
        mDialog = new ProgressDialog(account.this);

        viewAndEdit = findViewById(R.id.viewAndEdit);
        user_name = findViewById(R.id.user_name);
        user_image = findViewById(R.id.user_image);

        savedAdsBtn = findViewById(R.id.savedAdsBtn);
        privacyBtn = findViewById(R.id.privacyBtn);
        notificationsBtn = findViewById(R.id.notificationsBtn);
        contactBtn = findViewById(R.id.contactBtn);
        helpAndGuidelineBtn = findViewById(R.id.helpAndGuidelineBtn);
        rateBtn = findViewById(R.id.rateBtn);


        savedAdsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account.this, savedAds.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        privacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account.this, privacy.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account.this, notifications.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account.this, contactUs.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        helpAndGuidelineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account.this, helpGuidelines.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        if(common.currentUser == null){
            Intent intent = new Intent(account.this, com.example.carbazar.signin.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }else {
            mDialog.setMessage("Please wait...");
            mDialog.show();
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Account & Settings");
            user_name.setText(common.currentUser.getName());

            Glide.with(this)
                    .asBitmap()
                    .load(common.currentUser.getPhoto())
                    .into(user_image);

            Retrofit retrofit = RetrofitClient.getInstance();
            iMyService = retrofit.create(IMyService.class);

            mDialog.dismiss();

            viewAndEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOnline()) {
                        Intent intent = new Intent(account.this, OtherProfile.class);
                        intent.putExtra("posterID", common.currentUser.getId().replaceAll(" ",""));
                        intent.putExtra("posterName", common.currentUser.getName());
                        intent.putExtra("posterRole", common.currentUser.getRole());

                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        Toast.makeText(account.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(account.this, MainActivity.class);
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
                Intent intent = new Intent(account.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.post_nav:

                Intent intent1 = new Intent(account.this, postAd.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.chatbot_nav:
                //
                Intent intent3 = new Intent(account.this, chatbot.class);
                startActivity(intent3);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.ar_vr_nav:
                //
                Intent intent2 = new Intent(account.this, ARHome.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.account_nav:
                //
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent1 = new Intent(account.this, MainActivity.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
