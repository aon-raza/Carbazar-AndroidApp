package com.example.carbazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.carbazar.Models.common;
import com.example.carbazar.Models.saveUnsave;
import com.example.carbazar.Models.user;
import com.mongodb.util.JSON;

import org.json.JSONObject;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class privacy extends AppCompatActivity {

    private Toolbar toolbar;
    private SwitchCompat number_in_ads_switch;
    private AppCompatEditText new_password_field;
    private AppCompatEditText confirm_new_password_field;
    private AppCompatButton change_password_btn;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Privacy");

        number_in_ads_switch = findViewById(R.id.number_in_ads_switch);
        new_password_field = findViewById(R.id.new_password_field);
        confirm_new_password_field = findViewById(R.id.confirm_new_password_field);
        change_password_btn = findViewById(R.id.change_password_btn);

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        mDialog = new ProgressDialog(privacy.this);

        mDialog.setMessage("Please wait...");
        mDialog.show();
        if (isOnline()){
            String token = "Bearer "+common.currentUser.getToken();
            String id = common.currentUser.getId().replaceAll(" ","");
            compositeDisposable.add(iMyService.profile(token,id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object s) throws Exception {
                            JSONObject jsonObject = new JSONObject(JSON.serialize(s));
                            if(jsonObject.getBoolean("AdsNumber")){
                                number_in_ads_switch.setChecked(true);
                            }
                            mDialog.dismiss();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(privacy.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }
        else{
            Toast.makeText(privacy.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        number_in_ads_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()){
                    saveUnsave su = new saveUnsave();
                    su.setUserId(common.currentUser.getId().replace(" ", ""));
                    compositeDisposable.add(iMyService.userAdsNoSetting(su)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object s) throws Exception {

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(privacy.this, "Server Error!", Toast.LENGTH_SHORT).show();
                                }
                            }));
                }
                else{
                    Toast.makeText(privacy.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        change_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()){
                    if(TextUtils.isEmpty(new_password_field.getText().toString())){
                        Toast.makeText(privacy.this, "Password cannot be null",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!new_password_field.getText().toString().contentEquals(confirm_new_password_field.getText().toString())){
                        Toast.makeText(privacy.this, "Passwords Mismatched",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(new_password_field.getText().toString().length() < 8){
                        Toast.makeText(privacy.this, "Password must be atleast 8 characters",Toast.LENGTH_SHORT).show();
                        return;
                    }

//                    user USER = new user();
//                    USER.setPassword(new_password_field.getText().toString());

                    String token = "Bearer "+common.currentUser.getToken();
                    String id = common.currentUser.getId().replaceAll(" ","");
                    compositeDisposable.add(iMyService.updatePassword(token,id,new_password_field.getText().toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object s) throws Exception {
                                    String response = "";
                                    if(s.toString().contains("error")){
                                        response = s.toString().replace("{error=","");
                                        response=response.replace("}","");
                                        Toast.makeText(privacy.this,""+response, Toast.LENGTH_SHORT).show();
                                    }
                                    if(s.toString().contains("message")){
                                        response = s.toString().replace("{message=","");
                                        response=response.replace("}","");
                                        Toast.makeText(privacy.this,""+response, Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(privacy.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                    common.currentUser = null;
                                    File file = new File(getFilesDir(), "my_sensitive_data.txt");
                                    if(file.exists()){
                                        file.delete();
                                    }
                                    finish();
                                    Intent intent = new Intent(privacy.this, signin.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(privacy.this, "Server Error: "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }));
                }
                else{
                    Toast.makeText(privacy.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
