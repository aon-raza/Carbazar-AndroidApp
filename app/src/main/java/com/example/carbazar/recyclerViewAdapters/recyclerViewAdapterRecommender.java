package com.example.carbazar.recyclerViewAdapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carbazar.Interfaces.OnOptionClickListener;
import com.example.carbazar.R;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class recyclerViewAdapterRecommender extends RecyclerView.Adapter<recyclerViewAdapterRecommender.ViewHolder>  {

    private static final String TAG = "recycViewAdaptRec";

    private List<JSONObject> list;
    private Context mContext;

    public recyclerViewAdapterRecommender(Context mContext, List<JSONObject> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_items_recommended_cars, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder : called.");
//        int positionInlist = i % list.size();
        final JSONObject post = list.get(i);
        try {
            if (post.has("car")){
                JSONObject car = post.getJSONArray("car").getJSONObject(0);
                String carMMV = car.getString("make")
                        + " " + car.getString("model")
                        + " " + car.getString("version");

                viewHolder.car_mmv.setText(carMMV);

                int likes = car.getJSONArray("likes").length();
                viewHolder.car_likes.setText("Likes: "+likes);

                int dislikes = car.getJSONArray("dislikes").length();
                viewHolder.car_dislikes.setText("Dislikes: "+dislikes);

                int totalReviews = car.getJSONArray("comments").length();
                viewHolder.car_total_reviews.setText("Total Reviews: "+totalReviews);

                double priceRating = post.getDouble("price_avg");
                viewHolder.price_value_rating.setRating((float) priceRating);
                double brandRating = post.getDouble("brand_avg");
                viewHolder.brand_rating.setRating((float) brandRating);
                double modelRating = post.getDouble("model_avg");
                viewHolder.model_rating.setRating((float) modelRating);
                double mileageRating = post.getDouble("mileage_avg");
                viewHolder.mileage_rating.setRating((float) mileageRating);
            }
            else {
                viewHolder.car_mmv.setText(post.getString("title"));

                if(!post.getString("familiarity").contentEquals("")){
                    viewHolder.car_likes.setText("Familiarity : "+post.getString("familiarity"));
                }
                else {
                    viewHolder.car_likes.setText("");
                }

                if (!post.getString("description").contentEquals("")){
                    viewHolder.car_dislikes.setText("\""+post.getString("description")+"\"");
                }
                else{
                    viewHolder.car_dislikes.setText("");
                }

                viewHolder.car_total_reviews.setText("Review by: "+post.getJSONObject("author").getString("username"));
                viewHolder.car_total_reviews.setTypeface(viewHolder.car_total_reviews.getTypeface(), Typeface.BOLD_ITALIC);

                double priceRating = post.getDouble("priceReview");
                viewHolder.price_value_rating.setRating((float) priceRating);
                double brandRating = post.getDouble("brandReview");
                viewHolder.brand_rating.setRating((float) brandRating);
                double modelRating = post.getDouble("modelReview");
                viewHolder.model_rating.setRating((float) modelRating);
                double mileageRating = post.getDouble("mileageReview");
                viewHolder.mileage_rating.setRating((float) mileageRating);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        AppCompatTextView car_mmv;
        AppCompatTextView car_likes;
        AppCompatTextView car_dislikes;
        AppCompatTextView car_total_reviews;
        CardView cardViewHome;

        AppCompatRatingBar brand_rating;
        AppCompatRatingBar price_value_rating;
        AppCompatRatingBar mileage_rating;
        AppCompatRatingBar model_rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            car_mmv = itemView.findViewById(R.id.car_mmv);
            car_likes = itemView.findViewById(R.id.car_likes);
            car_dislikes = itemView.findViewById(R.id.car_dislikes);
            car_total_reviews = itemView.findViewById(R.id.car_total_reviews);
            cardViewHome = itemView.findViewById(R.id.cardview_home);

            brand_rating = itemView.findViewById(R.id.brand_rating);;
            price_value_rating = itemView.findViewById(R.id.price_value_rating);;
            mileage_rating = itemView.findViewById(R.id.mileage_rating);;
            model_rating = itemView.findViewById(R.id.model_rating);;
        }
    }
}
