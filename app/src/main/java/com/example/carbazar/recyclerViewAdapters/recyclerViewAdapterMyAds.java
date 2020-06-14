package com.example.carbazar.recyclerViewAdapters;

import android.content.Context;
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
import com.example.carbazar.Interfaces.OnOptionClickListener;
import com.example.carbazar.Models.common;
import com.example.carbazar.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class recyclerViewAdapterMyAds extends RecyclerView.Adapter<recyclerViewAdapterMyAds.ViewHolder>  {

    private OnOptionClickListener onOptionClickListener;
    private static final String TAG = "recycViewAdaptHome";

    private List<JSONObject> list;
    private Context mContext;

    public recyclerViewAdapterMyAds(Context mContext, List<JSONObject> list) {
        this.list = list;
        this.mContext = mContext;
        this.onOptionClickListener = ((OnOptionClickListener) mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_items_my_ads, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder : called.");
//        int positionInlist = i % list.size();
        final JSONObject post = list.get(i);
        try {

            Glide.with(mContext)
                    .asBitmap()
                    .load(
//                            common.ip +
                                    post.getJSONArray("photo").getString(0).replaceAll("\\\\", "/"))
                    .into(viewHolder.image);

            viewHolder.price.setText("RS "+post.getString("price") +"  -  "+
                    post.getString("make") +"  -  "+
                    post.getString("model"));
            if (post.getString("price").contentEquals(" ")){
                viewHolder.price.setText(post.getString("make") +"  -  "+
                        post.getString("model"));
            }

            viewHolder.year.setText(post.getString("registration_year") +"  -  ("+
                    post.getString("mileage")  +")KMs");
            if(post.has("engine_type")){
                viewHolder.year.setText(post.getString("registration_year") +"  -  ("+
                        post.getString("mileage")  +")KMs  -  "+
                        post.getString("engine_type"));
            }

            viewHolder.details.setText(post.getString("title")  +"  -  "+
                    post.getString("engine_capacity") + "(cc)");
            viewHolder.location.setText("Location: " + post.getString("city"));
            viewHolder.postedBy.setText(post.getJSONObject("postedBy").getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(post.has("engine_type")){
                    try {
                        onOptionClickListener.onpostClick(post.getString("_id"), "Seller");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(!post.has("engine_type")){
                    try {
                        onOptionClickListener.onpostClick(post.getString("_id"), "Buyer");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        viewHolder.cardViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(post.has("engine_type")){
                    try {
                        onOptionClickListener.onpostClick(post.getString("_id"), "Seller");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(!post.has("engine_type")){
                    try {
                        onOptionClickListener.onpostClick(post.getString("_id"), "Buyer");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
                int arr = 0;
        try{
            if(list.size()==0){
                arr = 0;
            }
            else{
                arr=list.size();
            }
        }catch (Exception e){
        }

        return arr;
//        if(list.size()==0){
//            return  0;
//        }
//        return Integer.MAX_VALUE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        AppCompatImageView image;
        AppCompatTextView price;
        AppCompatTextView year;
        AppCompatTextView details;
        AppCompatTextView location;
        AppCompatTextView postedBy;
        CardView cardViewHome;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.car_image);
            price = itemView.findViewById(R.id.car_price);
            year = itemView.findViewById(R.id.car_year);
            details = itemView.findViewById(R.id.car_details);
            location = itemView.findViewById(R.id.car_location);
            postedBy = itemView.findViewById(R.id.posted_by);
            cardViewHome = itemView.findViewById(R.id.cardview_home);
        }
    }
}
