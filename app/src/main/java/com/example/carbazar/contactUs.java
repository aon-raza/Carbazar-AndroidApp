package com.example.carbazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.carbazar.Models.common;
import com.example.carbazar.Models.contactModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class contactUs extends AppCompatActivity {

    private Toolbar toolbar;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    ProgressDialog mDialog;

    private AppCompatEditText CU_name_field;
    private AppCompatEditText CU_email_field;
    private AppCompatEditText CU_subject_field;
    private AppCompatEditText CU_message_field;
    private AppCompatButton send_message_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Contact Us");

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        mDialog = new ProgressDialog(contactUs.this);

        CU_name_field = findViewById(R.id.CU_name_field);
        CU_email_field = findViewById(R.id.CU_email_field);
        CU_subject_field = findViewById(R.id.CU_subject_field);
        CU_message_field = findViewById(R.id.CU_message_field);
        send_message_btn = findViewById(R.id.send_message_btn);

        if(isOnline()) {
            CU_name_field.setText(common.currentUser.getName());
            CU_name_field.setFocusable(false);
            CU_email_field.setText(common.currentUser.getEmail());
            CU_email_field.setFocusable(false);
        }
        else {
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }

        send_message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(CU_subject_field.getText().toString()) || CU_subject_field.getText().toString().contentEquals(" ")){
                    Toast.makeText(contactUs.this, "Subject cannot be null",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(CU_message_field.getText().toString()) || CU_message_field.getText().toString().contentEquals("\n\n")){
                    Toast.makeText(contactUs.this, "Message cannot be null",Toast.LENGTH_SHORT).show();
                    return;
                }

                contactModel CM = new contactModel();
                CM.setName(common.currentUser.getName());
                CM.setEmail(common.currentUser.getEmail());
                CM.setSubject(CU_subject_field.getText().toString());
                CM.setMessage(CU_message_field.getText().toString());

                compositeDisposable.add(iMyService.contactMessage(CM)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object s) throws Exception {

                                Toast.makeText(contactUs.this,"Message sent successfully. " +
                                        "\nWe'll get back to you as soon as reasonably possible.", Toast.LENGTH_LONG).show();

                                CU_subject_field.setText(" ");
                                CU_message_field.setText("\n\n");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(contactUs.this, "Server Error!", Toast.LENGTH_SHORT).show();
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
