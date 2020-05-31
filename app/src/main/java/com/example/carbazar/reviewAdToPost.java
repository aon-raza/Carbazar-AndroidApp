package com.example.carbazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.carbazar.Models.common;
import com.example.carbazar.Models.contactSellerModel;
import com.example.carbazar.Models.saveUnsave;
import com.mongodb.util.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class reviewAdToPost extends AppCompatActivity {

    private String postID;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;
    private AppCompatTextView price;
    private AppCompatTextView title;
    private AppCompatTextView location;
    private AppCompatTextView postDate;

    private AppCompatTextView make;
    private AppCompatTextView model;
    private AppCompatTextView year;
    private AppCompatTextView kmDriven;
    private AppCompatTextView engineType;
    private AppCompatTextView regesteredIn;
    private AppCompatTextView trasnmission;
    private AppCompatTextView engineCapacity;
    private AppCompatTextView assembly;
    private AppCompatTextView condition;

    private AppCompatTextView description;

    private AppCompatTextView user_name;

    private VideoView videoView;
    private List<String> imagesList;

    private AppCompatButton edit_ad_btn;
    private AppCompatButton publish_ad_btn;

    private String posterEmail;
    private String posterID;
    private String posterRole;

    private AppCompatTextView view_profile_label;
    private CircleImageView user_image;

    private boolean isFavorite = false;

    private String phoneNumberOfPoster = null;


    private ArrayList<String> listForUpdate;
    private ArrayList<String> imagesForUpdate;
    private ArrayList<String> videoForUpdate;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_ad_to_post);

        mDialog = new ProgressDialog(reviewAdToPost.this);
        listForUpdate = new ArrayList<String>();
        imagesForUpdate = new ArrayList<String>();
        videoForUpdate = new ArrayList<String>();

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        price = findViewById(R.id.car_price);
        title = findViewById(R.id.post_title);
        location = findViewById(R.id.location);
        postDate = findViewById(R.id.post_date);

        make = findViewById(R.id.make);
        model = findViewById(R.id.model);
        year = findViewById(R.id.year);
        kmDriven = findViewById(R.id.KMDriven);
        engineType = findViewById(R.id.engine_type);
        regesteredIn = findViewById(R.id.registered_in);
        trasnmission = findViewById(R.id.transmission);
        engineCapacity = findViewById(R.id.engine_capacity);
        assembly = findViewById(R.id.assembly);
        condition = findViewById(R.id.condition);

        description = findViewById(R.id.description);

        videoView = findViewById(R.id.videoView);
        videoView.setVisibility(View.GONE);


        edit_ad_btn = findViewById(R.id.edit_ad_btn);
        publish_ad_btn = findViewById(R.id.publish_ad_btn);

        user_image = findViewById(R.id.user_image);
        user_name = findViewById(R.id.user_name);
        view_profile_label = findViewById(R.id.view_profile_label);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if (getSupportActionBar() != null) {
//            //getSupportActionBar().hide();
//        }

        Bundle extras = getIntent().getExtras();
        if(extras!= null){
            postID = extras.getString("postID");

            if(isOnline()) {
                getPost(postID);
            }
            else{
                Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
        }

        edit_ad_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        publish_ad_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.setMessage("Please wait...");
                mDialog.show();

                String token = "Bearer "+common.currentUser.getToken();
                compositeDisposable.add(iMyService.publishPost(token, postID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object response) throws Exception {
                                mDialog.dismiss();
                                Toast.makeText(reviewAdToPost.this, "Ad Published.", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(reviewAdToPost.this, MainActivity.class);
                                startActivity(intent1);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(reviewAdToPost.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }));
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void getPost(String postID){
        compositeDisposable.add(iMyService.getSinglePost(postID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //my own
                        JSONObject jsonObject = new JSONObject(JSON.serialize(response));
                        displayPost(jsonObject);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(reviewAdToPost.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void displayPost(JSONObject jsonObject){
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("photo");
            int i=0;
            imagesList = new ArrayList<String>();
            while (i<jsonArray.length()){
                imagesList.add(
//                        common.ip +
                        jsonArray.getString(i).replaceAll("\\\\", "/"));
                imagesForUpdate.add(
//                        common.ip +
                        jsonArray.getString(i).replaceAll("\\\\", "/"));
                i++;
            }
            ViewPager viewPager = findViewById(R.id.view_pager);
            reviewAdToPost.ImagePagerAdapter adapter = new reviewAdToPost.ImagePagerAdapter(this, imagesList);
            viewPager.setAdapter(adapter);

            listForUpdate.add(jsonObject.getString("title"));
            listForUpdate.add(jsonObject.getString("city"));
            listForUpdate.add(jsonObject.getString("make"));
            listForUpdate.add(jsonObject.getString("model"));
            listForUpdate.add(jsonObject.getString("registration_city"));
            listForUpdate.add(jsonObject.getString("registration_year"));
            listForUpdate.add(jsonObject.getString("mileage"));
            listForUpdate.add(jsonObject.getString("exterior_color"));
            listForUpdate.add(jsonObject.getString("price"));
            listForUpdate.add(jsonObject.getString("body"));
            listForUpdate.add(jsonObject.getString("engine_type"));
            listForUpdate.add(jsonObject.getString("transmission"));
            listForUpdate.add(jsonObject.getString("assembly"));
            listForUpdate.add(jsonObject.getString("engine_capacity"));

            price.setText("Rs " + jsonObject.getString("price"));
            title.setText(jsonObject.getString("title"));
            location.setText(jsonObject.getString("city"));
            postDate.setText(jsonObject.getString("created").substring(0,10));

            make.setText(jsonObject.getString("make"));
            model.setText(jsonObject.getString("model"));
            year.setText(jsonObject.getString("registration_year"));
            kmDriven.setText(jsonObject.getString("mileage") + " kms");
            engineType.setText(jsonObject.getString("engine_type"));
            regesteredIn.setText(jsonObject.getString("registration_city"));
            trasnmission.setText(jsonObject.getString("transmission"));
            engineCapacity.setText(jsonObject.getString("engine_capacity") +" (cc)");
            assembly.setText(jsonObject.getString("assembly"));
            condition.setText(jsonObject.getString("condition"));

            description.setText(jsonObject.getString("body"));

            user_name.setText(jsonObject.getJSONObject("postedBy").getString("name"));

            posterEmail = jsonObject.getJSONObject("postedBy").getString("email");
            posterID = jsonObject.getJSONObject("postedBy").getString("_id");
            posterRole = jsonObject.getJSONObject("postedBy").getString("role");

            Glide.with(this)
                    .asBitmap()
                    .load(common.ip + "/user/photo/" + posterID+"?"+new Date().getTime())
                    .into(user_image);


            if(jsonObject.getJSONArray("video").length()>0){
                videoForUpdate.add(jsonObject.getJSONArray("video").getString(0));
                videoView.setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(
//                        common.ip +
                        jsonObject.getJSONArray("video").getString(0)
                                .replaceAll("\\\\", "/"));
                videoView.setVideoURI(uri);
                MediaController mediaController = new MediaController(this);
//                mediaController.show(500);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                videoView.start();
            }



        } catch (JSONException e) {
            Toast.makeText(reviewAdToPost.this, "Error! \n"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

    private void sendMessageToSeller(String name, String email, String title, String message, String posterEmail) {
        contactSellerModel contactSellerModelObj = new contactSellerModel(name, email, title, message, posterEmail);

        compositeDisposable.add(iMyService.contactSeller(contactSellerModelObj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //my own
                        Toast.makeText(reviewAdToPost.this, "The message has been sent to the seller.", Toast.LENGTH_LONG).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(reviewAdToPost.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private List<String> mImages;
        private Context mContext;

        public ImagePagerAdapter(Context mContext, List<String> mImages) {
            this.mContext = mContext;
            this.mImages = mImages;
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = reviewAdToPost.this;
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(mContext)
                    .asBitmap()
                    .load(mImages.get(position))
                    .into(imageView);
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }
}
