package com.example.carbazar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class editProfile extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static final int GALLERY_REQUEST_CODE = 100;
    static final Integer READ_STORAGE_PERMISSION_REQUEST_CODE=0x3;
    private AppCompatEditText name;
    private AppCompatEditText email;
    private AppCompatEditText phone;
    private AppCompatEditText password;
    private AppCompatEditText joined;
    private AppCompatButton signOut;
    private AppCompatButton saveChanges;
    private RelativeLayout RLProfile;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private CircleImageView profileImage;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;
    private String previousPassword;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        android.view.Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        toolbar = findViewById(R.id.toolbar);

        RLProfile = findViewById(R.id.RL_profile);

        name = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        phone = findViewById(R.id.profilePhone);
        password = findViewById(R.id.profilePassword);
        joined = findViewById(R.id.profileJoined);

        signOut = findViewById(R.id.signout_btn);
        saveChanges = findViewById(R.id.savechanges_btn);

        profileImage = findViewById(R.id.profileImage);

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

        mDialog = new ProgressDialog(editProfile.this);

        if(common.currentUser == null){
            Intent intent = new Intent(editProfile.this, com.example.carbazar.signin.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }else {

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");

            RLProfile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });
            Retrofit retrofit = RetrofitClient.getInstance();
            iMyService = retrofit.create(IMyService.class);

            saveChanges.setVisibility(View.INVISIBLE);
            name.setEnabled(false);
            email.setEnabled(false);
            phone.setEnabled(false);
            password.setEnabled(false);
            joined.setEnabled(false);

            if(isOnline()){
                mDialog.setMessage("Please wait...");
                mDialog.show();

//                Glide.with(this)
//                        .asBitmap()
//                        .load(common.currentUser.getPhoto())
//                        .into(profileImage);
                String token = "Bearer "+common.currentUser.getToken();
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
                                    Toast.makeText(editProfile.this,""+response, Toast.LENGTH_SHORT).show();
                                }
                                if(s.toString().contains("message")){
                                    response = s.toString().replace("{message=","");
                                    response=response.replace("}","");
                                    Toast.makeText(editProfile.this,""+response, Toast.LENGTH_SHORT).show();
                                }

                                //my own
                                JSONObject jsonObject = new JSONObject(JSON.serialize(s));
                                //String saint = jsonObject.getString("email");

                                common.currentUser.setName(jsonObject.getString("name"));
                                common.currentUser.setEmail(jsonObject.getString("email"));
                                common.currentUser.setPhone(jsonObject.getString("phone"));
                                if (jsonObject.has("photo")){
                                    common.currentUser.setPhoto(jsonObject.getString("photo"));

                                    Glide.with(getApplicationContext())
                                            .asBitmap()
                                            .load(jsonObject.getString("photo"))
                                            .into(profileImage);
                                }

                                name.setText(jsonObject.getString("name"));
                                email.setText(jsonObject.getString("email"));
                                phone.setText(jsonObject.getString("phone"));
                                password.setText(common.currentUser.getPassword());
                                joined.setText(jsonObject.getString("created").substring(0,10));
                                mDialog.dismiss();

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(editProfile.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }));
            }else{
                Toast.makeText(editProfile.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }




//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url ="http://192.168.1.101:8080/user/:"+ common.currentUser.getId();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Toast.makeText(profile.this, response,Toast.LENGTH_SHORT).show();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(profile.this, "That didn't work!" +error.toString(),Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        queue.add(stringRequest);
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    common.currentUser = null;
                    File file = new File(getFilesDir(), "my_sensitive_data.txt");
                    if(file.exists()){
                        file.delete();
                    }
                    Intent intent = new Intent(editProfile.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    Toast.makeText(editProfile.this, "Signed out." ,Toast.LENGTH_SHORT).show();
                }
            });
        }

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    if(TextUtils.isEmpty(name.getText().toString())){
                        Toast.makeText(editProfile.this, "Name cannot be null",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(TextUtils.isEmpty(phone.getText().toString())){
                        Toast.makeText(editProfile.this, "Phone cannot be null",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(TextUtils.isEmpty(password.getText().toString())){
                        Toast.makeText(editProfile.this, "Password cannot be null",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    previousPassword = common.currentUser.getPassword();
                    String token = "Bearer "+common.currentUser.getToken();
                    String id = common.currentUser.getId().replaceAll(" ","");
                    compositeDisposable.add(iMyService.updateProfile(token,id,name.getText().toString(),
                            password.getText().toString(), phone.getText().toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object s) throws Exception {
                                    String response = "";
                                    if(s.toString().contains("error")){
                                        response = s.toString().replace("{error=","");
                                        response=response.replace("}","");
                                        Toast.makeText(editProfile.this,""+response, Toast.LENGTH_SHORT).show();
                                    }
                                    else if(s.toString().contains("message")){
                                        response = s.toString().replace("{message=","");
                                        response=response.replace("}","");
                                        Toast.makeText(editProfile.this,""+response, Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        common.currentUser.setName(name.getText().toString());
                                        common.currentUser.setPhone(phone.getText().toString());
                                        common.currentUser.setPassword(password.getText().toString());

                                        name.setEnabled(false);
                                        phone.setEnabled(false);
                                        password.setEnabled(false);

                                        saveChanges.setVisibility(View.INVISIBLE);
                                        Toast.makeText(editProfile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                        if(!previousPassword.contentEquals(password.getText().toString())){
                                            common.currentUser = null;
                                            File file = new File(getFilesDir(), "my_sensitive_data.txt");
                                            if(file.exists()){
                                                file.delete();
                                            }
                                            finish();
                                            Intent intent = new Intent(editProfile.this, signin.class);
                                            startActivity(intent);
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            Toast.makeText(editProfile.this, "Signin with new password", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(editProfile.this, "Server Error: "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }));
                }else{
                    Toast.makeText(editProfile.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermissionForReadExtertalStorage()){
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                requestPermissionForReadExtertalStorage();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                pickFromGallery();
            }
        });
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
                Intent intent = new Intent(editProfile.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.post_nav:
                Intent intent1 = new Intent(editProfile.this, postAd.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.chatbot_nav:
                //
                Intent intent3 = new Intent(editProfile.this, chatbot.class);
                startActivity(intent3);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.ar_vr_nav:
                //
                Intent intent2 = new Intent(editProfile.this, ARHome.class);
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
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_profile,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.editBTN_actionBar:
                //
                name.setEnabled(true);
                phone.setEnabled(true);
                password.setEnabled(true);

                name.requestFocus();
                name.setSelection(name.getText().length());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);

                saveChanges.setVisibility(View.VISIBLE);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    profileImage.setImageURI(selectedImage);
                    File imageFile = new File(getRealPathFromURI(selectedImage));

                    RequestBody requestFile =
                            RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("photo", imageFile.getName(), requestFile);

                    String token = "Bearer "+common.currentUser.getToken();
                    String id = common.currentUser.getId().replaceAll(" ","");
                    compositeDisposable.add(iMyService.updatePhoto(token,id,body)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object s) throws Exception {
                                    Toast.makeText(editProfile.this, "Profile picture changed.", Toast.LENGTH_SHORT).show();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(editProfile.this, "Server Error: "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }));
                    break;
            }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
