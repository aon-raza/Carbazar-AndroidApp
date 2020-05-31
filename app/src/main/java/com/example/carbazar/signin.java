package com.example.carbazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKeys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.carbazar.Models.common;
import com.example.carbazar.Models.user;
import com.google.gson.JsonObject;
import com.mongodb.util.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class signin extends AppCompatActivity {

    private AppCompatEditText email;
    private AppCompatEditText password;
    private AppCompatButton signin;
    private RelativeLayout RLSignin;
    private Toolbar toolbar;
    private AppCompatTextView dontHaveAccount;
    private AppCompatTextView forgotPassword;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        email = findViewById(R.id.i_email_field);
        password = findViewById(R.id.i_password_field);
        signin = findViewById(R.id.sign_in_btn);
        RLSignin = findViewById(R.id.RL_signin);
        toolbar = findViewById(R.id.toolbar);
        dontHaveAccount = findViewById(R.id.dont_have_acc);
        forgotPassword = findViewById(R.id.forgot_password);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign in");

        RLSignin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        dontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(com.example.carbazar.signin.this, com.example.carbazar.signup.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(email.getText().toString())){
                    Toast.makeText(signin.this, "Email cannot be null",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(signin.this, "Password cannot be null",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isOnline()) {
                    signinUser(email.getText().toString(),password.getText().toString());
                }
                else{
                    Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                }

            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()) {
                    LayoutInflater inflater = (LayoutInflater) com.example.carbazar.signin.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View formElementsView = inflater.inflate(R.layout.forget_password_alert,null, false);

                    final AppCompatEditText emailEditText = formElementsView.findViewById(R.id.emailEditText);

                    new AlertDialog.Builder(com.example.carbazar.signin.this).setView(formElementsView)
                            .setTitle("Enter Email")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    /*
                                     * Getting the value of selected RadioButton.
                                     */
                                    // get selected radio button from radioGroup

                                    //if ()
                                    if(emailEditText.getText().toString().contentEquals("")){
                                        Toast.makeText(getApplicationContext(),"Email cannot be null",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        forgetPassword(emailEditText.getText().toString());
                                    }
                                }

                            }).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(com.example.carbazar.signin.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(com.example.carbazar.signin.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void signinUser(String Email, String Password) {
        user USER = new user(Email,Password);

        compositeDisposable.add(iMyService.signinUser(USER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object s) throws Exception {
                        String response = "";
                        if (s.toString().contains("error")) {
                            response = s.toString().replace("{error=", "");
                            response = response.replace("}", "");
                            Toast.makeText(signin.this, response, Toast.LENGTH_SHORT).show();
                        } else {
                            response = s.toString().replace("{token=", "")
                                    .replace("user={_id=", "")
                                    .replace("name=", "")
                                    .replace("email=", "")
                                    .replace("}}", "");

                            String[] details = response.split(",");

                            user USER = new user();
                            USER.setToken(details[0]);
                            USER.setId(details[1]);
                            USER.setName(details[2]);
                            USER.setEmail(details[3]);
                            USER.setPassword(password.getText().toString());
                            common.currentUser = USER;

                            saveInfo(details[0], details[1], password.getText().toString());


                            Toast.makeText(signin.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(com.example.carbazar.signin.this, MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(signin.this, "Server Error!", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void forgetPassword(String Email){
        user USER = new user(Email);

        compositeDisposable.add(iMyService.forgotPassword(USER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object s) throws Exception {
                        String response = "";
                        if(s.toString().contains("error")){
                            response = s.toString().replace("{error=","");
                            response=response.replace("}","");
                        }
                        if(s.toString().contains("message")){
                            response = s.toString().replace("{message=","");
                            response=response.replace("}","");
                        }
                        Toast.makeText(signin.this,""+response, Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(signin.this, "Server Error!", Toast.LENGTH_SHORT).show();
                    }
                }));
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

    public void saveInfo(String token, String id, String pass){

        try {

            File file = new File(getFilesDir(), "my_sensitive_data.txt");
            file.createNewFile();

            String filename = "my_sensitive_data.txt";
            String fileContents = token +"_+_"+ id +"_+_"+ pass;
            FileOutputStream outputStream;

            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"File Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
//        try {
//            File file = new File(getFilesDir(), "my_sensitive_data.txt");
//            file.createNewFile();
//            KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
//            String masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
//
//            String fileToWrite = "my_sensitive_data.txt";
//
//            EncryptedFile encryptedFile = new EncryptedFile.Builder(
//                    new File(getFilesDir(), fileToWrite),
//                    this,
//                    masterKeyAlias,
//                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
//            ).build();
//
//            // Write to a file.
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
//                    encryptedFile.openFileOutput()));
//            writer.write(token +"_+_"+ id +"_+_"+ pass);
//        } catch (GeneralSecurityException gse) {
//            // Error occurred getting or creating keyset.
//            Toast.makeText(getApplicationContext(),"File Security Error",Toast.LENGTH_SHORT).show();
//        } catch (IOException ex) {
//            // Error occurred opening file for writing.
//            Toast.makeText(getApplicationContext(),"File Error",Toast.LENGTH_SHORT).show();
//        }
    }
}
