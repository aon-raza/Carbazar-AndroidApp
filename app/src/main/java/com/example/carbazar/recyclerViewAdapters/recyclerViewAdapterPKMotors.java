package com.example.carbazar.recyclerViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carbazar.IMyService;
import com.example.carbazar.Models.siteVisitModel;
import com.example.carbazar.R;
import com.example.carbazar.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class recyclerViewAdapterPKMotors extends RecyclerView.Adapter<recyclerViewAdapterPKMotors.ViewHolder>  {

    private static final String TAG = "recycViewAdaptHome";

    private List<String> list;
    private Context mContext;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    Retrofit retrofit = RetrofitClient.getInstance();
    IMyService iMyService = retrofit.create(IMyService.class);

    public recyclerViewAdapterPKMotors(Context mContext, List<String> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_items_home, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder : called.");

        if(list.size() != 0) {
            int positionInlist = i % list.size();
            final String post = list.get(positionInlist);
            try {
                JSONObject jsonObject = new JSONObject(post);


                Glide.with(mContext)
                        .asBitmap()
                        .load(jsonObject.getString("image"))
                        .into(viewHolder.image);

                viewHolder.price.setText(jsonObject.getString("price"));
                viewHolder.year.setText(jsonObject.getString("Year") + "  -  " +
                        jsonObject.getString("Mileage") + "  -  " +
                        jsonObject.getString("Transmission"));
                viewHolder.details.setText(jsonObject.getString("Make") + "  -  " +
                        jsonObject.getString("Model") + "  -  " +
                        jsonObject.getString("title"));
                viewHolder.location.setText(jsonObject.getString("RegistrationCity"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            viewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        siteVisitModel siteVisitModel1 = new siteVisitModel("pkm");
                        compositeDisposable.add(iMyService.visit(siteVisitModel1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Object>() {
                                    @Override
                                    public void accept(Object s) throws Exception {
                                        //my own
//                                                        JSONArray jsonArray = new JSONArray(JSON.serialize(s));
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
//                                        Toast.makeText(writeReview.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }));

                        JSONObject jsonObject2 = new JSONObject(post);
                        String link = jsonObject2.getString("link");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        mContext.startActivity(browserIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            viewHolder.cardViewHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        siteVisitModel siteVisitModel1 = new siteVisitModel("pkm");
                        compositeDisposable.add(iMyService.visit(siteVisitModel1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Object>() {
                                    @Override
                                    public void accept(Object s) throws Exception {
                                        //my own
//                                                        JSONArray jsonArray = new JSONArray(JSON.serialize(s));
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
//                                        Toast.makeText(writeReview.this, "Server Error!" +throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }));

                        JSONObject jsonObject2 = new JSONObject(post);
                        String link = jsonObject2.getString("link");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        mContext.startActivity(browserIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
//        int arr = 0;
//        try{
//            if(list.size()==0){
//                arr = 0;
//            }
//            else{
//
//                arr=list.size();
//            }
//        }catch (Exception e){
//        }
//
//        return arr;
        if(list.size()==0){
            return  0;
        }
        return Integer.MAX_VALUE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        AppCompatImageView image;
        AppCompatTextView price;
        AppCompatTextView year;
        AppCompatTextView details;
        AppCompatTextView location;
        CardView cardViewHome;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.car_image);
            price = itemView.findViewById(R.id.car_price);
            year = itemView.findViewById(R.id.car_year);
            details = itemView.findViewById(R.id.car_details);
            location = itemView.findViewById(R.id.car_location);
            cardViewHome = itemView.findViewById(R.id.cardview_home);
        }
    }
}
