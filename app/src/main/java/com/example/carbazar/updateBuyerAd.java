package com.example.carbazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carbazar.Models.common;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mongodb.util.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class updateBuyerAd extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private BottomNavigationView bottomNavigationView;
    private RelativeLayout RLMain;
    private Toolbar toolbar;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    ProgressDialog mDialog;

    private AppCompatEditText title;
    private Spinner city;
    private Spinner selectMake;
    private Spinner selectModel;
    private Spinner selectYear;
    private Spinner selectPriceRange;
    private Spinner KMDriven;
    private AppCompatImageView uploadPhotos;
    private Spinner engineCapacity;
    private AppCompatButton postBtn;

    List<String> citySpinnerItems;
    List<String> makeSpinnerItems;
    List<String> modelSpinnerItems;
    List<String> yearSpinnerItems;
    List<String> priceRangeSpinnerItems;
    List<String> KMDrivenSpinnerItems;
    List<String> engineCapacitySpinnerItems;

    int PICK_IMAGE_MULTIPLE = 1;
    Uri imageEncoded;
    List<Uri> imagesEncodedList;

    private String postID;
    private ArrayList<String> listForUpdate;
    private ArrayList<String> imagesForUpdate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_buyer_ad);

        Bundle extras = getIntent().getExtras();
        if(extras!= null){
            postID = extras.getString("postID");

            listForUpdate = extras.getStringArrayList("listForUpdate");
            imagesForUpdate = extras.getStringArrayList("imagesForUpdate");
        }


        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        android.view.Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Buyer Ad");

        RLMain = findViewById(R.id.RL_Main);

        RLMain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        RLMain.requestFocus();

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
        mDialog = new ProgressDialog(updateBuyerAd.this);

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        title = findViewById(R.id.title_field);
        title.setText(listForUpdate.get(0));
        city = findViewById(R.id.citySpinnerSelection);
        selectMake = findViewById(R.id.makeSpinnerSelection);
        selectModel = findViewById(R.id.modelSpinnerSelection);
        selectYear = findViewById(R.id.yearSpinnerSelection);
        selectPriceRange = findViewById(R.id.priceRangeSpinnerSelection);
        KMDriven = findViewById(R.id.KMDrivenSpinnerSelection);
        uploadPhotos = findViewById(R.id.upload_photos);
        Glide.with(this)
                .asBitmap()
                .load(imagesForUpdate.get(0))
                .into(uploadPhotos);
        engineCapacity = findViewById(R.id.engineCapacitySpinnerSelection);
        postBtn = findViewById(R.id.post_btn);
        postBtn.setText("Update");


        citySpinnerItems = new ArrayList<String>();
        makeSpinnerItems = new ArrayList<String>();
        modelSpinnerItems = new ArrayList<String>();
        yearSpinnerItems = new ArrayList<String>();
        priceRangeSpinnerItems = new ArrayList<String>();
        KMDrivenSpinnerItems = new ArrayList<String>();
        engineCapacitySpinnerItems = new ArrayList<String>();
        getDataInMakeSpinner();
        getDataInOtherSpinners();

        getDataInModelSpinner(listForUpdate.get(2));

        selectMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0 && position !=1){
                    getDataInModelSpinner(selectMake.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    if(common.currentUser == null){
                        Toast.makeText(updateBuyerAd.this, "Sign in first", Toast.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(updateBuyerAd.this, signin.class);
                        startActivity(intent3);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    else{
                        sendData();
                    }
                }else{
                    Toast.makeText(updateBuyerAd.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
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

    private void getDataInMakeSpinner(){
        makeSpinnerItems.clear();
        makeSpinnerItems.add("Select Make *");
        makeSpinnerItems.add(".....");
        makeSpinnerItems.add(listForUpdate.get(2));
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
                            selectMake.setAdapter(adapter0);
                            selectMake.setSelection(2);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(updateBuyerAd.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }else{
            Toast.makeText(updateBuyerAd.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataInModelSpinner(String selectedMake){
        modelSpinnerItems.clear();
        modelSpinnerItems.add("Select Model *");
        modelSpinnerItems.add(".....");
        modelSpinnerItems.add(listForUpdate.get(3));
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
                            selectModel.setAdapter(adapter);
                            selectModel.setSelection(2);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(updateBuyerAd.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }else{
            Toast.makeText(updateBuyerAd.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataInOtherSpinners(){
        modelSpinnerItems.add("Select \"Make\" first");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_layout, modelSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //selectModel.setAdapter(adapter);

        citySpinnerItems.add("Select your City *");
        citySpinnerItems.add(".....");
        citySpinnerItems.add(listForUpdate.get(1));
        citySpinnerItems.add("Abbottabad");
        citySpinnerItems.add("Adilpur");
        citySpinnerItems.add("Ahmadpur East");
        citySpinnerItems.add("Ahmadpur Sial");
        citySpinnerItems.add("Akora");
        citySpinnerItems.add("Alik Ghund");
        citySpinnerItems.add("Alipur");
        citySpinnerItems.add("Aman Garh");
        citySpinnerItems.add("Arifwala");
        citySpinnerItems.add("Attock City");
        citySpinnerItems.add("Baddomalhi");
        citySpinnerItems.add("Badin");
        citySpinnerItems.add("Baffa");
        citySpinnerItems.add("Bagarji");
        citySpinnerItems.add("Bahawalnagar");
        citySpinnerItems.add("Bahawalnagar");
        citySpinnerItems.add("Bahawalpur");
        citySpinnerItems.add("Bandhi");
        citySpinnerItems.add("Bannu");
        citySpinnerItems.add("Barkhan");
        citySpinnerItems.add("Basirpur");
        citySpinnerItems.add("Bat Khela");
        citySpinnerItems.add("Battagram");
        citySpinnerItems.add("Begowala");
        citySpinnerItems.add("Bela");
        citySpinnerItems.add("Berani");
        citySpinnerItems.add("Bhag");
        citySpinnerItems.add("Bhakkar");
        citySpinnerItems.add("Bhalwal");
        citySpinnerItems.add("Bhan");
        citySpinnerItems.add("Bhawana");
        citySpinnerItems.add("Bhera");
        citySpinnerItems.add("Bhimbar");
        citySpinnerItems.add("Bhiria");
        citySpinnerItems.add("Bhit Shah");
        citySpinnerItems.add("Bhopalwala");
        citySpinnerItems.add("Bozdar Wada");
        citySpinnerItems.add("Burewala");
        citySpinnerItems.add("Chak");
        citySpinnerItems.add("Chak Azam Sahu");
        citySpinnerItems.add("Chak Five Hundred Seventy-five");
        citySpinnerItems.add("Chak Jhumra");
        citySpinnerItems.add("Chak One Hundred Twenty Nine Left");
        citySpinnerItems.add("Chak Thirty-one -Eleven Left");
        citySpinnerItems.add("Chak Two Hundred Forty-nine Thal Development Authority");
        citySpinnerItems.add("Chakwal");
        citySpinnerItems.add("Chaman");
        citySpinnerItems.add("Chamber");
        citySpinnerItems.add("Charsadda");
        citySpinnerItems.add("Chawinda");
        citySpinnerItems.add("Chenab Nagar");
        citySpinnerItems.add("Chhor");
        citySpinnerItems.add("Chichawatni");
        citySpinnerItems.add("Chiniot");
        citySpinnerItems.add("Chishtian");
        citySpinnerItems.add("Choa Saidan Shah");
        citySpinnerItems.add("Chuchar-kana Mandi");
        citySpinnerItems.add("Chuhar Jamali");
        citySpinnerItems.add("Chunian");
        citySpinnerItems.add("Dadhar");
        citySpinnerItems.add("Dadu");
        citySpinnerItems.add("Daira Din Panah");
        citySpinnerItems.add("Dajal");
        citySpinnerItems.add("Dalbandin");
        citySpinnerItems.add("Daromehar");
        citySpinnerItems.add("Darya Khan");
        citySpinnerItems.add("Darya Khan Marri");
        citySpinnerItems.add("Daska Kalan");
        citySpinnerItems.add("Daud Khel");
        citySpinnerItems.add("Daulatpur");
        citySpinnerItems.add("Daultala");
        citySpinnerItems.add("Daur");
        citySpinnerItems.add("Dera Bugti");
        citySpinnerItems.add("Dera Ghazi Khan");
        citySpinnerItems.add("Dera Ismail Khan");
        citySpinnerItems.add("Dhanot");
        citySpinnerItems.add("Dhaunkal");
        citySpinnerItems.add("Dhoro Naro");
        citySpinnerItems.add("Digri");
        citySpinnerItems.add("Dijkot");
        citySpinnerItems.add("Dinan Bashnoian Wala");
        citySpinnerItems.add("Dinga");
        citySpinnerItems.add("Dipalpur");
        citySpinnerItems.add("Diplo");
        citySpinnerItems.add("Doaba");
        citySpinnerItems.add("Dokri");
        citySpinnerItems.add("Duki");
        citySpinnerItems.add("Dullewala");
        citySpinnerItems.add("Dunga Bunga");
        citySpinnerItems.add("Dunyapur");
        citySpinnerItems.add("Eminabad");
        citySpinnerItems.add("Faisalabad");
        citySpinnerItems.add("Faqirwali");
        citySpinnerItems.add("Faruka");
        citySpinnerItems.add("Fazilpur");
        citySpinnerItems.add("Fort Abbas");
        citySpinnerItems.add("Gadani");
        citySpinnerItems.add("Gambat");
        citySpinnerItems.add("Garh Maharaja");
        citySpinnerItems.add("Garhi Khairo");
        citySpinnerItems.add("Garhiyasin");
        citySpinnerItems.add("Gharo");
        citySpinnerItems.add("Ghauspur");
        citySpinnerItems.add("Ghotki");
        citySpinnerItems.add("Gilgit");
        citySpinnerItems.add("Gojra");
        citySpinnerItems.add("Goth Garelo");
        citySpinnerItems.add("Goth Phulji");
        citySpinnerItems.add("Goth Radhan");
        citySpinnerItems.add("Gujar Khan");
        citySpinnerItems.add("Gujranwala");
        citySpinnerItems.add("Gujrat");
        citySpinnerItems.add("Gwadar");
        citySpinnerItems.add("Hadali");
        citySpinnerItems.add("Hafizabad");
        citySpinnerItems.add("Hala");
        citySpinnerItems.add("Hangu");
        citySpinnerItems.add("Haripur");
        citySpinnerItems.add("Harnai");
        citySpinnerItems.add("Harnoli");
        citySpinnerItems.add("Harunabad");
        citySpinnerItems.add("Hasilpur");
        citySpinnerItems.add("Haveli Lakha");
        citySpinnerItems.add("Havelian");
        citySpinnerItems.add("Hazro City");
        citySpinnerItems.add("Hingorja");
        citySpinnerItems.add("Hujra Shah Muqim");
        citySpinnerItems.add("Hyderabad");
        citySpinnerItems.add("Islamabad");
        citySpinnerItems.add("Islamkot");
        citySpinnerItems.add("Jacobabad");
        citySpinnerItems.add("Jahanian Shah");
        citySpinnerItems.add("Jalalpur Jattan");
        citySpinnerItems.add("Jalalpur Pirwala");
        citySpinnerItems.add("Jam Sahib");
        citySpinnerItems.add("Jampur");
        citySpinnerItems.add("Jand");
        citySpinnerItems.add("Jandiala Sher Khan");
        citySpinnerItems.add("Jaranwala");
        citySpinnerItems.add("Jati");
        citySpinnerItems.add("Jatoi Shimali");
        citySpinnerItems.add("Jauharabad");
        citySpinnerItems.add("Jhang Sadr");
        citySpinnerItems.add("Jhawarian");
        citySpinnerItems.add("Jhelum");
        citySpinnerItems.add("Jhol");
        citySpinnerItems.add("Jiwani");
        citySpinnerItems.add("Johi");
        citySpinnerItems.add("Kabirwala");
        citySpinnerItems.add("Kadhan");
        citySpinnerItems.add("Kahna Nau");
        citySpinnerItems.add("Kahror Pakka");
        citySpinnerItems.add("Kahuta");
        citySpinnerItems.add("Kalabagh");
        citySpinnerItems.add("Kalaswala");
        citySpinnerItems.add("Kalat");
        citySpinnerItems.add("Kaleke Mandi");
        citySpinnerItems.add("Kallar Kahar");
        citySpinnerItems.add("Kalur Kot");
        citySpinnerItems.add("Kamalia");
        citySpinnerItems.add("Kamar Mushani");
        citySpinnerItems.add("Kambar");
        citySpinnerItems.add("Kamoke");
        citySpinnerItems.add("Kamra");
        citySpinnerItems.add("Kandhkot");
        citySpinnerItems.add("Kandiari");
        citySpinnerItems.add("Kandiaro");
        citySpinnerItems.add("Kanganpur");
        citySpinnerItems.add("Karachi");
        citySpinnerItems.add("Karak");
        citySpinnerItems.add("Karaundi");
        citySpinnerItems.add("Kario Ghanwar");
        citySpinnerItems.add("Karor");
        citySpinnerItems.add("Kashmor");
        citySpinnerItems.add("Kasur");
        citySpinnerItems.add("Khadan Khak");
        citySpinnerItems.add("Khadro");
        citySpinnerItems.add("Khairpur");
        citySpinnerItems.add("Khairpur Mir's");
        citySpinnerItems.add("Khairpur Nathan Shah");
        citySpinnerItems.add("Khairpur Tamewah");
        citySpinnerItems.add("Khalabat");
        citySpinnerItems.add("Khangah Dogran");
        citySpinnerItems.add("Khangarh");
        citySpinnerItems.add("Khanpur");
        citySpinnerItems.add("Khanpur Mahar");
        citySpinnerItems.add("Kharan");
        citySpinnerItems.add("Kharian");
        citySpinnerItems.add("Khewra");
        citySpinnerItems.add("Khurrianwala");
        citySpinnerItems.add("Khushab");
        citySpinnerItems.add("Khuzdar");
        citySpinnerItems.add("Kohat");
        citySpinnerItems.add("Kohlu");
        citySpinnerItems.add("Kot Addu");
        citySpinnerItems.add("Kot Diji");
        citySpinnerItems.add("Kot Ghulam Muhammad");
        citySpinnerItems.add("Kot Malik Barkhurdar");
        citySpinnerItems.add("Kot Mumin");
        citySpinnerItems.add("Kot Radha Kishan");
        citySpinnerItems.add("Kot Samaba");
        citySpinnerItems.add("Kot Sultan");
        citySpinnerItems.add("Kotli");
        citySpinnerItems.add("Kotli Loharan");
        citySpinnerItems.add("Kotri");
        citySpinnerItems.add("Kulachi");
        citySpinnerItems.add("Kundian");
        citySpinnerItems.add("Kunjah");
        citySpinnerItems.add("Kunri");
        citySpinnerItems.add("Lachi");
        citySpinnerItems.add("Ladhewala Waraich");
        citySpinnerItems.add("Lahore");
        citySpinnerItems.add("Lakhi");
        citySpinnerItems.add("Lakki");
        citySpinnerItems.add("Lala Musa");
        citySpinnerItems.add("Lalian");
        citySpinnerItems.add("Larkana");
        citySpinnerItems.add("Layyah");
        citySpinnerItems.add("Liliani");
        citySpinnerItems.add("Lodhran");
        citySpinnerItems.add("Loralai");
        citySpinnerItems.add("Mach");
        citySpinnerItems.add("Madeji");
        citySpinnerItems.add("Mailsi");
        citySpinnerItems.add("Malakwal");
        citySpinnerItems.add("Malakwal City");
        citySpinnerItems.add("Malir Cantonment");
        citySpinnerItems.add("Mamu Kanjan");
        citySpinnerItems.add("Mananwala");
        citySpinnerItems.add("Mandi Bahauddin");
        citySpinnerItems.add("Mangla");
        citySpinnerItems.add("Mankera");
        citySpinnerItems.add("Mansehra");
        citySpinnerItems.add("Mardan");
        citySpinnerItems.add("Mastung");
        citySpinnerItems.add("Matiari");
        citySpinnerItems.add("Matli");
        citySpinnerItems.add("Mehar");
        citySpinnerItems.add("Mehrabpur");
        citySpinnerItems.add("Mian Channun");
        citySpinnerItems.add("Mianke Mor");
        citySpinnerItems.add("Mianwali");
        citySpinnerItems.add("Minchianabad");
        citySpinnerItems.add("Mingora");
        citySpinnerItems.add("Miro Khan");
        citySpinnerItems.add("Mirpur Bhtoro");
        citySpinnerItems.add("Mirpur Khas");
        citySpinnerItems.add("Mirpur Mathelo");
        citySpinnerItems.add("Mirpur Sakro");
        citySpinnerItems.add("Mirwah Gorchani");
        citySpinnerItems.add("Mitha Tiwana");
        citySpinnerItems.add("Mithi");
        citySpinnerItems.add("Moro");
        citySpinnerItems.add("Multan");
        citySpinnerItems.add("Muridke");
        citySpinnerItems.add("Murree");
        citySpinnerItems.add("Mustafabad");
        citySpinnerItems.add("Muzaffarabad");
        citySpinnerItems.add("Muzaffargarh");
        citySpinnerItems.add("Nabisar");
        citySpinnerItems.add("Nankana Sahib");
        citySpinnerItems.add("Narang Mandi");
        citySpinnerItems.add("Narowal");
        citySpinnerItems.add("Nasirabad");
        citySpinnerItems.add("Naudero");
        citySpinnerItems.add("Naukot");
        citySpinnerItems.add("Naushahra Virkan");
        citySpinnerItems.add("Naushahro Firoz");
        citySpinnerItems.add("Nawabshah");
        citySpinnerItems.add("Nazir Town");
        citySpinnerItems.add("New Badah");
        citySpinnerItems.add("Nowshera Cantonment");
        citySpinnerItems.add("Nushki");
        citySpinnerItems.add("Okara");
        citySpinnerItems.add("Ormara");
        citySpinnerItems.add("Pabbi");
        citySpinnerItems.add("Pad Idan");
        citySpinnerItems.add("Paharpur");
        citySpinnerItems.add("Pakpattan");
        citySpinnerItems.add("Pano Aqil");
        citySpinnerItems.add("Parachinar");
        citySpinnerItems.add("Pasni");
        citySpinnerItems.add("Pasrur");
        citySpinnerItems.add("Pattoki");
        citySpinnerItems.add("Peshawar");
        citySpinnerItems.add("Phalia");
        citySpinnerItems.add("Pind Dadan Khan");
        citySpinnerItems.add("Pindi Bhattian");
        citySpinnerItems.add("Pindi Gheb");
        citySpinnerItems.add("Pir Jo Goth");
        citySpinnerItems.add("Pir Mahal");
        citySpinnerItems.add("Pishin");
        citySpinnerItems.add("Pithoro");
        citySpinnerItems.add("Qadirpur Ran");
        citySpinnerItems.add("Quetta");
        citySpinnerItems.add("Rahim Yar Khan");
        citySpinnerItems.add("Raiwind");
        citySpinnerItems.add("Raja Jang");
        citySpinnerItems.add("Rajanpur");
        citySpinnerItems.add("Rajo Khanani");
        citySpinnerItems.add("Ranipur");
        citySpinnerItems.add("Rasulnagar");
        citySpinnerItems.add("Ratodero");
        citySpinnerItems.add("Rawala Kot");
        citySpinnerItems.add("Rawalpindi");
        citySpinnerItems.add("Renala Khurd");
        citySpinnerItems.add("Risalpur Cantonment");
        citySpinnerItems.add("Rohri");
        citySpinnerItems.add("Rojhan");
        citySpinnerItems.add("Rustam");
        citySpinnerItems.add("Saddiqabad");
        citySpinnerItems.add("Sahiwal");
        citySpinnerItems.add("Sahiwal");
        citySpinnerItems.add("Sakrand");
        citySpinnerItems.add("Samaro");
        citySpinnerItems.add("Sambrial");
        citySpinnerItems.add("Sanghar");
        citySpinnerItems.add("Sangla Hill");
        citySpinnerItems.add("Sanjwal");
        citySpinnerItems.add("Sann");
        citySpinnerItems.add("Sarai Alamgir");
        citySpinnerItems.add("Sarai Naurang");
        citySpinnerItems.add("Sarai Sidhu");
        citySpinnerItems.add("Sargodha");
        citySpinnerItems.add("Sehwan");
        citySpinnerItems.add("Setharja Old");
        citySpinnerItems.add("Shabqadar");
        citySpinnerItems.add("Shahdad Kot");
        citySpinnerItems.add("Shahdadpur");
        citySpinnerItems.add("Shahkot");
        citySpinnerItems.add("Shahpur");
        citySpinnerItems.add("Shahpur Chakar");
        citySpinnerItems.add("Shahr Sultan");
        citySpinnerItems.add("Shakargarh");
        citySpinnerItems.add("Sharqpur Sharif");
        citySpinnerItems.add("Shekhupura");
        citySpinnerItems.add("Shikarpur");
        citySpinnerItems.add("Shorkot");
        citySpinnerItems.add("Shujaabad");
        citySpinnerItems.add("Sialkot");
        citySpinnerItems.add("Sibi");
        citySpinnerItems.add("Sillanwali");
        citySpinnerItems.add("Sinjhoro");
        citySpinnerItems.add("Sita Road");
        citySpinnerItems.add("Sobhodero");
        citySpinnerItems.add("Sodhri");
        citySpinnerItems.add("Sohbatpur");
        citySpinnerItems.add("Sukheke Mandi");
        citySpinnerItems.add("Sukkur");
        citySpinnerItems.add("Surab");
        citySpinnerItems.add("Surkhpur");
        citySpinnerItems.add("Swabi");
        citySpinnerItems.add("Talagang");
        citySpinnerItems.add("Talamba");
        citySpinnerItems.add("Talhar");
        citySpinnerItems.add("Tandlianwala");
        citySpinnerItems.add("Tando Adam");
        citySpinnerItems.add("Tando Allahyar");
        citySpinnerItems.add("Tando Bago");
        citySpinnerItems.add("Tando Jam");
        citySpinnerItems.add("Tando Muhammad Khan");
        citySpinnerItems.add("Tangi");
        citySpinnerItems.add("Tangwani");
        citySpinnerItems.add("Tank");
        citySpinnerItems.add("Taunsa");
        citySpinnerItems.add("Thal");
        citySpinnerItems.add("Tharu Shah");
        citySpinnerItems.add("Thatta");
        citySpinnerItems.add("Thul");
        citySpinnerItems.add("Toba Tek Singh");
        citySpinnerItems.add("Topi");
        citySpinnerItems.add("Turbat");
        citySpinnerItems.add("Ubauro");
        citySpinnerItems.add("Umarkot");
        citySpinnerItems.add("Upper Dir");
        citySpinnerItems.add("Usta Muhammad");
        citySpinnerItems.add("Uthal");
        citySpinnerItems.add("Utmanzai");
        citySpinnerItems.add("Vihari");
        citySpinnerItems.add("Warah");
        citySpinnerItems.add("Wazirabad");
        citySpinnerItems.add("Yazman");
        citySpinnerItems.add("Zafarwal");
        citySpinnerItems.add("Zahir Pir");
        citySpinnerItems.add("Zaida");
        citySpinnerItems.add("Zhob");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_layout, citySpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);
        city.setSelection(2);


        yearSpinnerItems.add("Select Year *");
        yearSpinnerItems.add(".....");
        yearSpinnerItems.add(listForUpdate.get(4));
        yearSpinnerItems.add("2020");
        yearSpinnerItems.add("2019");
        yearSpinnerItems.add("2018");
        yearSpinnerItems.add("2017");
        yearSpinnerItems.add("2016");
        yearSpinnerItems.add("2015");
        yearSpinnerItems.add("2014");
        yearSpinnerItems.add("2013");
        yearSpinnerItems.add("2012");
        yearSpinnerItems.add("2011");
        yearSpinnerItems.add("2010");
        yearSpinnerItems.add("2009");
        yearSpinnerItems.add("2008");
        yearSpinnerItems.add("2007");
        yearSpinnerItems.add("2006");
        yearSpinnerItems.add("2005");
        yearSpinnerItems.add("2004");
        yearSpinnerItems.add("2003");
        yearSpinnerItems.add("2002");
        yearSpinnerItems.add("2001");
        yearSpinnerItems.add("2000");
        yearSpinnerItems.add("1999");
        yearSpinnerItems.add("1998");
        yearSpinnerItems.add("1997");
        yearSpinnerItems.add("1996");
        yearSpinnerItems.add("1995");
        yearSpinnerItems.add("1994");
        yearSpinnerItems.add("1993");
        yearSpinnerItems.add("1992");
        yearSpinnerItems.add("1991");
        yearSpinnerItems.add("1990");
        yearSpinnerItems.add("1989");
        yearSpinnerItems.add("1988");
        yearSpinnerItems.add("1987");
        yearSpinnerItems.add("1986");
        yearSpinnerItems.add("1985");
        yearSpinnerItems.add("1984");
        yearSpinnerItems.add("1983");
        yearSpinnerItems.add("1982");
        yearSpinnerItems.add("1981");
        yearSpinnerItems.add("1980");
        yearSpinnerItems.add("1979");
        yearSpinnerItems.add("1978");
        yearSpinnerItems.add("1977");
        yearSpinnerItems.add("1976");
        yearSpinnerItems.add("1975");
        yearSpinnerItems.add("1974");
        yearSpinnerItems.add("1973");
        yearSpinnerItems.add("1972");
        yearSpinnerItems.add("1971");
        yearSpinnerItems.add("1970");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_layout, yearSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectYear.setAdapter(adapter);
        selectYear.setSelection(2);

        priceRangeSpinnerItems.add("Select Price Range *");
        priceRangeSpinnerItems.add(".....");
        priceRangeSpinnerItems.add(listForUpdate.get(5));
        priceRangeSpinnerItems.add("100,000 - 150,000");
        priceRangeSpinnerItems.add("150,000 - 200,000");
        priceRangeSpinnerItems.add("200,000 - 250,000");
        priceRangeSpinnerItems.add("250,000 - 300,000");
        priceRangeSpinnerItems.add("300,000 - 350,000");
        priceRangeSpinnerItems.add("350,000 - 400,000");
        priceRangeSpinnerItems.add("400,000 - 450,000");
        priceRangeSpinnerItems.add("450,000 - 500,000");
        priceRangeSpinnerItems.add("500,000 - 600,000");
        priceRangeSpinnerItems.add("600,000 - 700,000");
        priceRangeSpinnerItems.add("700,000 - 800,000");
        priceRangeSpinnerItems.add("800,000 - 900,000");
        priceRangeSpinnerItems.add("900,000 - 1,000,000");
        priceRangeSpinnerItems.add("1,000,000 - 1,200,000");
        priceRangeSpinnerItems.add("1,200,000 - 1,400,000");
        priceRangeSpinnerItems.add("1,400,000 - 1,600,000");
        priceRangeSpinnerItems.add("1,600,000 - 1,800,000");
        priceRangeSpinnerItems.add("1,800,000 - 2,000,000");
        priceRangeSpinnerItems.add("2,000,000 - 2,500,000");
        priceRangeSpinnerItems.add("2,500,000 - 3,000,000");
        priceRangeSpinnerItems.add("3,000,000 - 3,500,000");
        priceRangeSpinnerItems.add("3,500,000 - 4,000,000");
        priceRangeSpinnerItems.add("4,000,000 - 5,000,000");
        priceRangeSpinnerItems.add("5,000,000 - 10,000,000");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_layout, priceRangeSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectPriceRange.setAdapter(adapter);
        selectPriceRange.setSelection(2);

        KMDrivenSpinnerItems.add("Select KM Driven *");
        KMDrivenSpinnerItems.add(".....");
        KMDrivenSpinnerItems.add(listForUpdate.get(6));
        KMDrivenSpinnerItems.add("0 - 25,000");
        KMDrivenSpinnerItems.add("25,000  - 50,000");
        KMDrivenSpinnerItems.add("50,000  - 100,000");
        KMDrivenSpinnerItems.add("100,000 - 150,000");
        KMDrivenSpinnerItems.add("150,000 - 200,000");
        KMDrivenSpinnerItems.add("200,000 - 250,000");
        KMDrivenSpinnerItems.add("250,000 - 300,000");
        KMDrivenSpinnerItems.add("300,000 - 350,000");
        KMDrivenSpinnerItems.add("350,000 - 400,000");
        KMDrivenSpinnerItems.add("400,000 - 450,000");
        KMDrivenSpinnerItems.add("450,000 - 500,000");
        KMDrivenSpinnerItems.add("500,000+");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_layout, KMDrivenSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        KMDriven.setAdapter(adapter);
        KMDriven.setSelection(2);


        engineCapacitySpinnerItems.add("Select Engine Capacity (CC) *");
        engineCapacitySpinnerItems.add(".....");
        engineCapacitySpinnerItems.add(listForUpdate.get(7));
        engineCapacitySpinnerItems.add("660");
        engineCapacitySpinnerItems.add("800");
        engineCapacitySpinnerItems.add("850");
        engineCapacitySpinnerItems.add("1000");
        engineCapacitySpinnerItems.add("1200");
        engineCapacitySpinnerItems.add("1250");
        engineCapacitySpinnerItems.add("1300");
        engineCapacitySpinnerItems.add("1500");
        engineCapacitySpinnerItems.add("1600");
        engineCapacitySpinnerItems.add("1700");
        engineCapacitySpinnerItems.add("1800");
        engineCapacitySpinnerItems.add("2000");
        engineCapacitySpinnerItems.add("2200");
        engineCapacitySpinnerItems.add("2400");
        engineCapacitySpinnerItems.add("2700");
        engineCapacitySpinnerItems.add("3000");
        engineCapacitySpinnerItems.add("3500");
        engineCapacitySpinnerItems.add("3800");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_layout, engineCapacitySpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        engineCapacity.setAdapter(adapter);
        engineCapacity.setSelection(2);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.home_nav:
                //
                Intent intent = new Intent(updateBuyerAd.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.post_nav:
                //
                return true;

            case R.id.chatbot_nav:
                //
                Intent intent1 = new Intent(updateBuyerAd.this, chatbot.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.ar_vr_nav:
                //
                Intent intent2 = new Intent(updateBuyerAd.this, ARHome.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.account_nav:
                //
                Intent intent3 = new Intent(updateBuyerAd.this, account.class);
                startActivity(intent3);
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

    private void sendData(){
        if(title.getText().toString().contentEquals("") || title.getText().toString().length() < 4){
            Toast.makeText(updateBuyerAd.this, "Enter a valid title",Toast.LENGTH_SHORT).show();
            title.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            return;
        }

        if(city.getSelectedItemPosition() == 0 || city.getSelectedItemPosition() == 1){
            Toast.makeText(updateBuyerAd.this, "Select your city",Toast.LENGTH_SHORT).show();
            city.setFocusable(true);
            city.setFocusableInTouchMode(true);
            city.requestFocus();
            city.performClick();
            return;
        }

        if(selectMake.getSelectedItem() == null){
            Toast.makeText(updateBuyerAd.this, "Select Make",Toast.LENGTH_SHORT).show();
            return;
        }

        if(selectMake.getSelectedItemPosition() == 0 || selectMake.getSelectedItemPosition() == 1){
            Toast.makeText(updateBuyerAd.this, "Select Make",Toast.LENGTH_SHORT).show();
            selectMake.setFocusable(true);
            selectMake.setFocusableInTouchMode(true);
            selectMake.requestFocus();
            selectMake.performClick();
            return;
        }

        if(selectModel.getSelectedItemPosition() == 0 || selectModel.getSelectedItemPosition() == 1){
            Toast.makeText(updateBuyerAd.this, "Select Model",Toast.LENGTH_SHORT).show();
            selectModel.setFocusable(true);
            selectModel.setFocusableInTouchMode(true);
            selectModel.requestFocus();
            selectModel.performClick();
            return;
        }

        if(selectYear.getSelectedItemPosition() == 0 || selectYear.getSelectedItemPosition() == 1){
            Toast.makeText(updateBuyerAd.this, "Select Year",Toast.LENGTH_SHORT).show();
            selectYear.setFocusable(true);
            selectYear.setFocusableInTouchMode(true);
            selectYear.requestFocus();
            selectYear.performClick();
            return;
        }

        if(selectPriceRange.getSelectedItemPosition() == 0 || selectPriceRange.getSelectedItemPosition() == 1){
            Toast.makeText(updateBuyerAd.this, "Select Price Range",Toast.LENGTH_SHORT).show();
            selectPriceRange.setFocusable(true);
            selectPriceRange.setFocusableInTouchMode(true);
            selectPriceRange.requestFocus();
            selectPriceRange.performClick();
            return;
        }

        if(KMDriven.getSelectedItemPosition() == 0 || KMDriven.getSelectedItemPosition() == 1){
            Toast.makeText(updateBuyerAd.this, "Select KMs Driven",Toast.LENGTH_SHORT).show();
            KMDriven.setFocusable(true);
            KMDriven.setFocusableInTouchMode(true);
            KMDriven.requestFocus();
            KMDriven.performClick();
            return;
        }

        if(engineCapacity.getSelectedItemPosition() == 0 || engineCapacity.getSelectedItemPosition() == 1){
            Toast.makeText(updateBuyerAd.this, "Select Engine Capacity",Toast.LENGTH_SHORT).show();
            engineCapacity.setFocusable(true);
            engineCapacity.setFocusableInTouchMode(true);
            engineCapacity.requestFocus();
            engineCapacity.performClick();
            return;
        }

        if(imagesEncodedList == null && imagesForUpdate.size() < 1){
            Toast.makeText(updateBuyerAd.this, "Please add a Photo.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(imagesEncodedList != null){
            if(imagesEncodedList.size() < 1 && imagesForUpdate.size() < 1){
                Toast.makeText(updateBuyerAd.this, "Please add a Photo.",Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
                return;
            }
        }

        mDialog.setMessage("Please wait...");
        mDialog.show();

        MultipartBody.Part photo1 = null;

        if(imagesEncodedList != null){
            File imageFile1 = new File(getImageFilePath(imagesEncodedList.get(0)));

            RequestBody requestFile1 =
                    RequestBody.create(MediaType.parse("multipart/form-data"), imageFile1);
            photo1 =
                    MultipartBody.Part.createFormData("photo-0", imageFile1.getName(), requestFile1);
        }

        RequestBody title1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), title.getText().toString());

        RequestBody city1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), city.getSelectedItem().toString());

        RequestBody make =
                RequestBody.create(MediaType.parse("multipart/form-data"), selectMake.getSelectedItem().toString());

        RequestBody model =
                RequestBody.create(MediaType.parse("multipart/form-data"), selectModel.getSelectedItem().toString());

        RequestBody year =
                RequestBody.create(MediaType.parse("multipart/form-data"), selectYear.getSelectedItem().toString());

        RequestBody priceRange =
                RequestBody.create(MediaType.parse("multipart/form-data"), selectPriceRange.getSelectedItem().toString());

        RequestBody kmDriven1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), KMDriven.getSelectedItem().toString());

//        RequestBody price1 =
//                RequestBody.create(MediaType.parse("multipart/form-data"), " ");

        RequestBody engineCapacity1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), engineCapacity.getSelectedItem().toString());

        RequestBody photos =
                RequestBody.create(MediaType.parse("multipart/form-data"), "1");

        RequestBody condition =
                RequestBody.create(MediaType.parse("multipart/form-data"), "Used");

        String token = "Bearer "+common.currentUser.getToken();
        String id = common.currentUser.getId().replaceAll(" ","");
        compositeDisposable.add(iMyService.updateBuyersPost(token,postID,photo1,
                title1,
                city1,
                make,
                model,
                year,
                kmDriven1,
                priceRange,
                engineCapacity1,
                photos,
                condition)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object s) throws Exception {
                        String response = "";
                        if(s.toString().contains("error")){
                            response = s.toString().replace("{error=","");
                            response=response.replace("}","");
                            Toast.makeText(updateBuyerAd.this,""+response, Toast.LENGTH_SHORT).show();
                        }
                        else if(s.toString().contains("message")){
                            response = s.toString().replace("{message=","");
                            response=response.replace("}","");
                            Toast.makeText(updateBuyerAd.this,""+response, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            mDialog.dismiss();
                            Toast.makeText(updateBuyerAd.this, "Ad Posted.", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(updateBuyerAd.this, MainActivity.class);
                            startActivity(intent1);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(updateBuyerAd.this, "Server Error: "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<Uri>();
                imagesEncodedList.clear();
                imageEncoded = null;
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();
                    imageEncoded = mImageUri;
                    imagesEncodedList.add(mImageUri);

                    InputStream imageStream = getContentResolver().openInputStream(mImageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    uploadPhotos.setImageBitmap(selectedImage);

                    // Get the cursor
//                    Cursor cursor = getContentResolver().query(mImageUri,
//                            filePathColumn, null, null, null);
//                    // Move to first row
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    imageEncoded  = cursor.getString(columnIndex);
//                    cursor.close();

                }
                else if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();

                        imagesEncodedList.add(uri);


//                            mArrayUri.add(uri);
//                            // Get the cursor
//                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
//                            // Move to first row
//                            cursor.moveToFirst();
//
//                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                            imageEncoded  = cursor.getString(columnIndex);
//                            imagesEncodedList.add(imageEncoded);
//                            cursor.close();

                    }
                    uploadPhotos.setImageURI(imagesEncodedList.get(0));
                    Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
//        Toast.makeText(this, "Something"+ imageEncoded, Toast.LENGTH_SHORT)
//                .show();
//
//        Toast.makeText(this, "Something..."+ imagesEncodedList, Toast.LENGTH_SHORT)
//                .show();
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getImageFilePath(Uri uri) {

        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];

        Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            cursor.close();
            return imagePath;
        }
        return null;
    }
}
