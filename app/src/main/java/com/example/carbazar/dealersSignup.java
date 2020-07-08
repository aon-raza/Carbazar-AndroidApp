package com.example.carbazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.carbazar.Models.user;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class dealersSignup extends AppCompatActivity {

    private AppCompatEditText firstName;
    private AppCompatEditText lastName;
    private AppCompatEditText email;
    private AppCompatEditText phone;
    private AppCompatEditText password;
    private AppCompatEditText confirmPssword;
    private AppCompatEditText companyName;
    private AppCompatEditText companyPhone;
    private AppCompatEditText address;
    private Spinner city;
    private AppCompatButton signup;
    private RelativeLayout RLSignup;
    private Toolbar toolbar;
    private AppCompatTextView alreadyHaveAcc;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;


    List<String> citySpinnerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealers_signup);
        firstName = findViewById(R.id.first_name_field);
        lastName = findViewById(R.id.last_name_field);
        email = findViewById(R.id.email_field);
        phone = findViewById(R.id.phone_field);
        password = findViewById(R.id.password_field);
        confirmPssword = findViewById(R.id.confirm_password_field);
        signup = findViewById(R.id.sign_up_btn);
        RLSignup = findViewById(R.id.RL_signup);
        toolbar = findViewById(R.id.toolbar);
        alreadyHaveAcc = findViewById(R.id.already_have_acc);

        companyName = findViewById(R.id.company_name_field);
        companyPhone = findViewById(R.id.company_phone_field);
        address = findViewById(R.id.address_field);
        city = findViewById(R.id.citySpinnerSelection);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dealers Sign up");

        citySpinnerItems = new ArrayList<String>();

        citySpinnerItems.add("Select your City *");
        citySpinnerItems.add(".....");
        citySpinnerItems.add("Abbottabad");
        citySpinnerItems.add("Dera Ghazi Khan");
        citySpinnerItems.add("Dera Ismail Khan");
        citySpinnerItems.add("Faisalabad");
        citySpinnerItems.add("Gilgit");
        citySpinnerItems.add("Gujranwala");
        citySpinnerItems.add("Gujrat");
        citySpinnerItems.add("Hyderabad");
        citySpinnerItems.add("Islamabad");
        citySpinnerItems.add("Karachi");
        citySpinnerItems.add("Kohat");
        citySpinnerItems.add("Lahore");
        citySpinnerItems.add("Mianwali");
        citySpinnerItems.add("Multan");
        citySpinnerItems.add("Murree");
        citySpinnerItems.add("Muzaffarabad");
        citySpinnerItems.add("Peshawar");
        citySpinnerItems.add("Quetta");
        citySpinnerItems.add("Rawalpindi");
        citySpinnerItems.add("Sargodha");
        citySpinnerItems.add("Sialkot");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_layout, citySpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);
        
        RLSignup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        
        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        alreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dealersSignup.this, com.example.carbazar.signin.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(firstName.getText().toString())){
                    Toast.makeText(dealersSignup.this, "First Name cannot be null",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(lastName.getText().toString())){
                    Toast.makeText(dealersSignup.this, "Last Name cannot be null",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email.getText().toString())){
                    Toast.makeText(dealersSignup.this, "Email cannot be null",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(phone.getText().toString())){
                    Toast.makeText(dealersSignup.this, "Phone Number cannot be null",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(dealersSignup.this, "Password cannot be null",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.getText().toString().contentEquals(confirmPssword.getText().toString())){
                    Toast.makeText(dealersSignup.this, "Passwords Mismatched",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(companyName.getText().toString())){
                    Toast.makeText(dealersSignup.this, "Company Name cannot be null",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(companyPhone.getText().toString())){
                    Toast.makeText(dealersSignup.this, "Company Phone cannot be null",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(address.getText().toString())){
                    Toast.makeText(dealersSignup.this, "Company address cannot be null",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(city.getSelectedItemPosition() == 0 || city.getSelectedItemPosition() == 1){
                    Toast.makeText(dealersSignup.this, "Select your city",Toast.LENGTH_SHORT).show();
                    city.setFocusable(true);
                    city.setFocusableInTouchMode(true);
                    city.requestFocus();
                    city.performClick();
                    return;
                }


                if(isOnline()) {
                    String name = firstName.getText().toString() +" "+lastName.getText().toString();
                    registerUser(name, email.getText().toString(),password.getText().toString(),
                            phone.getText().toString(), "dealer", address.getText().toString(),
                            companyName.getText().toString(), companyPhone.getText().toString(),
                            city.getSelectedItem().toString());
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
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void registerUser(String name, String email, String password, String phone,
                              String role, String address, String comName, String comPhone, String comCity) {
        user USER = new user(name,email,password,phone,role,address,comName,comPhone,comCity);

        compositeDisposable.add(iMyService.signupUser(USER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Object>() {
                            @Override
                            public void accept(Object s) throws Exception{
                                String response = "";
                                if(s.toString().contains("error")){
                                    response = s.toString().replace("{error=","");
                                    response=response.replace("}","");
                                }
                                if(s.toString().contains("message")){
                                    response = s.toString().replace("{message=","");
                                    response=response.replace("}","");
                                }
                                Toast.makeText(dealersSignup.this,""+response,Toast.LENGTH_SHORT).show();
                                if(response.contentEquals("Signup is succesfull")){
                                    Intent intent = new Intent(dealersSignup.this, com.example.carbazar.signin.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(dealersSignup.this, "Server Error!", Toast.LENGTH_SHORT).show();
                            }
                        }
                ));



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
}