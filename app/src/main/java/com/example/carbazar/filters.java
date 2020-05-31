package com.example.carbazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
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

public class filters extends AppCompatActivity {

    private Toolbar toolbar;
    private CrystalRangeSeekbar seekBarPrice;
    private AppCompatEditText price1;
    private AppCompatEditText price2;

    private CrystalRangeSeekbar seekBarYear;
    private AppCompatEditText year1;
    private AppCompatEditText year2;

    private CrystalRangeSeekbar seekBarKMDriven;
    private AppCompatEditText kmDriven1;
    private AppCompatEditText kmDriven2;

    private CrystalRangeSeekbar seekBarEngineCapacity;
    private AppCompatEditText engineCapacity1;
    private AppCompatEditText engineCapacity2;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyServiceFlask;
    IMyService iMyService;

    private Spinner city;
    private Spinner selectMake;
    private Spinner selectModel;
    private Spinner selectRegCity;
    private Spinner selectEngineType;
    private Spinner selectCondition;
    private Spinner selectTransmission;
    private Spinner selectcolor;
    private AppCompatButton applyBtn;

    List<String> citySpinnerItems;
    List<String> makeSpinnerItems;
    List<String> modelSpinnerItems;
    List<String> regCitySpinnerItems;
    List<String> engineTypeSpinnerItems;
    List<String> conditionSpinnerItems;
    List<String> transmissionSpinnerItems;
    List<String> colorSpinnerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("FILTERS");

        seekBarPrice = findViewById(R.id.seekBarPrice);
        price1 = findViewById(R.id.price1_field);
        price2 = findViewById(R.id.price2_field);

        seekBarYear = findViewById(R.id.seekBarYear);
        year1 = findViewById(R.id.year1_field);
        year2 = findViewById(R.id.year2_field);

        seekBarKMDriven = findViewById(R.id.seekBarKMDriven);
        kmDriven1 = findViewById(R.id.KMDriven1_field);
        kmDriven2 = findViewById(R.id.KMDriven2_field);

        seekBarEngineCapacity = findViewById(R.id.seekBarengineCapacity);
        engineCapacity1 = findViewById(R.id.engineCapacity1_field);
        engineCapacity2 = findViewById(R.id.engineCapacity2_field);


        Retrofit retrofit = RetrofitClientFlask.getInstance();
        iMyServiceFlask = retrofit.create(IMyService.class);

        Retrofit retrofit2 = RetrofitClient.getInstance();
        iMyService = retrofit2.create(IMyService.class);

        city = findViewById(R.id.locationSpinner);
        selectMake = findViewById(R.id.makeSpinner);
        selectModel = findViewById(R.id.modelSpinner);
        selectRegCity = findViewById(R.id.regCitySpinner);
        selectEngineType = findViewById(R.id.engTypeSpinner);
        selectCondition = findViewById(R.id.conditionSpinner);
        selectTransmission = findViewById(R.id.transmissionSpinner);
        selectcolor = findViewById(R.id.colorSpinner);
        applyBtn = findViewById(R.id.apply_btn);


        citySpinnerItems = new ArrayList<String>();
        makeSpinnerItems = new ArrayList<String>();
        modelSpinnerItems = new ArrayList<String>();
        regCitySpinnerItems = new ArrayList<String>();
        engineTypeSpinnerItems = new ArrayList<String>();
        conditionSpinnerItems = new ArrayList<String>();
        transmissionSpinnerItems = new ArrayList<String>();
        colorSpinnerItems = new ArrayList<String>();


        seekBarPrice.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                price1.setText("RS " + NumberFormat.getInstance().format(minValue));
                price2.setText("RS " + NumberFormat.getInstance().format(maxValue));
                if(maxValue.intValue() == 5000000){
                    price2.setText("RS " + NumberFormat.getInstance().format(maxValue) + "+");
                }
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


        seekBarYear.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                year1.setText(String.valueOf(minValue));
                year2.setText(String.valueOf(maxValue));
            }
        });


        seekBarKMDriven.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                kmDriven1.setText(String.valueOf(minValue));
                kmDriven2.setText(String.valueOf(maxValue));
                if(maxValue.intValue() == 500000){
                    kmDriven2.setText(maxValue + "+");
                }
            }
        });

        seekBarEngineCapacity.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                engineCapacity1.setText(String.valueOf(minValue));
                engineCapacity2.setText(String.valueOf(maxValue));
            }
        });


        getDataInMakeSpinner();
        getDataInOtherSpinners();

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


        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras!= null){
            if(extras.getStringArrayList("filtersValues") != null){
                ArrayList<String> filtersValues = extras.getStringArrayList("filtersValues");
                if(filtersValues.get(0).contains("-")){
                    String[] price = filtersValues.get(0).split("-");
                    price1.setText("RS "+ price[0]);
                    price2.setText("RS "+ price[1]);
                }

                if(filtersValues.get(1).contains("-")){
                    String[] year = filtersValues.get(1).split("-");
                    year1.setText(year[0]);
                    year2.setText(year[1]);
                }


                if(filtersValues.get(2).contains("-")){
                    String[] kmDriven = filtersValues.get(2).split("-");
                    kmDriven1.setText(kmDriven[0]);
                    kmDriven2.setText(kmDriven[1]);
                }

                if(filtersValues.get(3).contains("-")){
                    String[] engineCapacity = filtersValues.get(3).split("-");
                    engineCapacity1.setText(engineCapacity[0]);
                    engineCapacity2.setText(engineCapacity[1]);
                }
            }
        }

    }

    private void getDataInMakeSpinner(){
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
                                    android.R.layout.simple_spinner_item, makeSpinnerItems);
                            adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            selectMake.setAdapter(adapter0);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(filters.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }else{
            Toast.makeText(filters.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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
                                    android.R.layout.simple_spinner_item, modelSpinnerItems);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            selectModel.setAdapter(adapter);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(filters.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }else{
            Toast.makeText(filters.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendData(){
        ArrayList<String> selectedFilters = new ArrayList<String>();

        if(!price1.getText().toString().contentEquals("") && !price2.getText().toString().contentEquals("")){
            String p1 = price1.getText().toString();
            p1 = p1.replaceAll("RS","").replace(" ", "").replace(",", "");
            String p2 = price2.getText().toString();
            p2 = p2.replaceAll("RS","").replace(" ", "").replace(",", "");
            selectedFilters.add(p1 +"-"+ p2);
        }
        else{
            selectedFilters.add("");
        }

        if(!year1.getText().toString().contentEquals("") && !year2.getText().toString().contentEquals("")){
            selectedFilters.add(year1.getText().toString() +"-"+ year2.getText().toString());
        }
        else{
            selectedFilters.add("");
        }

        if(!kmDriven1.getText().toString().contentEquals("") && !kmDriven2.getText().toString().contentEquals("")){
            selectedFilters.add(kmDriven1.getText().toString() +" - "+ kmDriven2.getText().toString());
        }
        else{
            selectedFilters.add("");
        }

        if(!engineCapacity1.getText().toString().contentEquals("") && !engineCapacity2.getText().toString().contentEquals("")){
            selectedFilters.add(engineCapacity1.getText().toString() +"-"+ engineCapacity2.getText().toString());
        }
        else{
            selectedFilters.add("");
        }

        if(!city.getSelectedItem().toString().contentEquals("Select Location *") &&
                !city.getSelectedItem().toString().contentEquals(".....")){
            selectedFilters.add(city.getSelectedItem().toString());
        }
        else{
            selectedFilters.add("");
        }


        if(selectMake.getSelectedItem() != null){
            if(!selectMake.getSelectedItem().toString().contentEquals("Select Make *") &&
                    !selectMake.getSelectedItem().toString().contentEquals(".....")){
                selectedFilters.add(selectMake.getSelectedItem().toString());
            }
            else{
                selectedFilters.add("");
            }
        }
        else{
            selectedFilters.add("");
        }

        if(selectModel.getSelectedItem() != null){
            if(!selectModel.getSelectedItem().toString().contentEquals("Select Model *") &&
                    !selectModel.getSelectedItem().toString().contentEquals(".....")){
                selectedFilters.add(selectModel.getSelectedItem().toString());
            }
            else{
                selectedFilters.add("");
            }
        }
        else{
            selectedFilters.add("");
        }


        if(!selectRegCity.getSelectedItem().toString().contentEquals("Select Registration City *") &&
                !selectRegCity.getSelectedItem().toString().contentEquals(".....")){
            selectedFilters.add(selectRegCity.getSelectedItem().toString());
        }
        else{
            selectedFilters.add("");
        }

        if(!selectEngineType.getSelectedItem().toString().contentEquals("Select Engine Type *") &&
                !selectEngineType.getSelectedItem().toString().contentEquals(".....")){
            selectedFilters.add(selectEngineType.getSelectedItem().toString());
        }
        else{
            selectedFilters.add("");
        }

        if(!selectCondition.getSelectedItem().toString().contentEquals("Select Condition *") &&
                !selectCondition.getSelectedItem().toString().contentEquals(".....")){
            selectedFilters.add(selectCondition.getSelectedItem().toString());
        }
        else{
            selectedFilters.add("");
        }

        if(!selectTransmission.getSelectedItem().toString().contentEquals("Select Transmission *") &&
                !selectTransmission.getSelectedItem().toString().contentEquals(".....")){
            selectedFilters.add(selectTransmission.getSelectedItem().toString());
        }
        else{
            selectedFilters.add("");
        }

        if(!selectcolor.getSelectedItem().toString().contentEquals("Select Color *") &&
                !selectcolor.getSelectedItem().toString().contentEquals(".....")){
            selectedFilters.add(selectcolor.getSelectedItem().toString());
        }
        else{
            selectedFilters.add("");
        }

        Bundle extras = getIntent().getExtras();
        if(extras!= null){
            selectedFilters.add(extras.getString("queryFilter"));
        }
        else{
            selectedFilters.add("");
        }

        Intent intent = new Intent(filters.this, MainActivity.class);
        intent.putStringArrayListExtra("allFilters", selectedFilters);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);

    }

    private void getDataInOtherSpinners(){
        modelSpinnerItems.add("Select \"Make\" first");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, modelSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectModel.setAdapter(adapter);

        citySpinnerItems.add("Select Location *");
        citySpinnerItems.add(".....");
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
                android.R.layout.simple_spinner_item, citySpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);

        regCitySpinnerItems.add("Select Registration City *");
        regCitySpinnerItems.add(".....");
        regCitySpinnerItems.add("Abbottabad");
        regCitySpinnerItems.add("Dera Ghazi Khan");
        regCitySpinnerItems.add("Dera Ismail Khan");
        regCitySpinnerItems.add("Faisalabad");
        regCitySpinnerItems.add("Gilgit");
        regCitySpinnerItems.add("Gujranwala");
        regCitySpinnerItems.add("Gujrat");
        regCitySpinnerItems.add("Hyderabad");
        regCitySpinnerItems.add("Islamabad");
        regCitySpinnerItems.add("Karachi");
        regCitySpinnerItems.add("Kohat");
        regCitySpinnerItems.add("Lahore");
        regCitySpinnerItems.add("Mianwali");
        regCitySpinnerItems.add("Multan");
        regCitySpinnerItems.add("Murree");
        regCitySpinnerItems.add("Muzaffarabad");
        regCitySpinnerItems.add("Peshawar");
        regCitySpinnerItems.add("Quetta");
        regCitySpinnerItems.add("Rawalpindi");
        regCitySpinnerItems.add("Sargodha");
        regCitySpinnerItems.add("Sialkot");

        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, regCitySpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectRegCity.setAdapter(adapter);


        engineTypeSpinnerItems.add("Select Engine Type *");
        engineTypeSpinnerItems.add(".....");
        engineTypeSpinnerItems.add("Petrol");
        engineTypeSpinnerItems.add("CNG");
        engineTypeSpinnerItems.add("Diesel");
        engineTypeSpinnerItems.add("LPG");
        engineTypeSpinnerItems.add("Hybrid");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, engineTypeSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectEngineType.setAdapter(adapter);

        conditionSpinnerItems.add("Select Condition *");
        conditionSpinnerItems.add(".....");
        conditionSpinnerItems.add("Used");
        conditionSpinnerItems.add("New");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, conditionSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCondition.setAdapter(adapter);

        transmissionSpinnerItems.add("Select Transmission *");
        transmissionSpinnerItems.add(".....");
        transmissionSpinnerItems.add("Manual");
        transmissionSpinnerItems.add("Automatic");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, transmissionSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectTransmission.setAdapter(adapter);

        colorSpinnerItems.add("Select Color *");
        colorSpinnerItems.add(".....");
        colorSpinnerItems.add("White");
        colorSpinnerItems.add("Silver");
        colorSpinnerItems.add("Black");
        colorSpinnerItems.add("Grey");
        colorSpinnerItems.add("Blue");
        colorSpinnerItems.add("Gold");
        colorSpinnerItems.add("Navy");
        colorSpinnerItems.add("Bronze");
        colorSpinnerItems.add("Burgundy");
        colorSpinnerItems.add("Green");
        colorSpinnerItems.add("Indigo");
        colorSpinnerItems.add("Maroon");
        colorSpinnerItems.add("Pink");
        colorSpinnerItems.add("Red");

        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, colorSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectcolor.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
        finish();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
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
