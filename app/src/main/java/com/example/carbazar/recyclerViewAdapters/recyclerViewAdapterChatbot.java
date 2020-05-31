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
import com.example.carbazar.Models.chatMessages;
import com.example.carbazar.R;

import java.util.List;

public class recyclerViewAdapterChatbot extends RecyclerView.Adapter<recyclerViewAdapterChatbot.ViewHolder>  {

    private static final String TAG = "recycViewAdaptHome";

    private List<chatMessages> list;
    private Context mContext;

    public recyclerViewAdapterChatbot(Context mContext, List<chatMessages> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_items_chatbot, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder : called.");
        final chatMessages chatMessagesObj = list.get(i);

        if (!chatMessagesObj.getUserMsg().contentEquals("")) {
            viewHolder.userText.setText(chatMessagesObj.getUserMsg());
            viewHolder.userText.setVisibility(View.VISIBLE);
            viewHolder.botText.setVisibility(View.GONE);
            viewHolder.cardview_image_chatbot.setVisibility(View.GONE);

        } else if (!chatMessagesObj.getChatbotMsg().contentEquals("")){
            viewHolder.botText.setText(chatMessagesObj.getChatbotMsg());
            viewHolder.botText.setVisibility(View.VISIBLE);
            viewHolder.userText.setVisibility(View.GONE);
            viewHolder.cardview_image_chatbot.setVisibility(View.GONE);
            if (!chatMessagesObj.getImage().contentEquals("")){
                viewHolder.cardview_image_chatbot.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .asBitmap()
                        .load(chatMessagesObj.getImage())
                        .into(viewHolder.car_image_chatbot);
            }
        }

//        viewHolder.image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    JSONObject jsonObject2 = new JSONObject(post);
//                    String link = jsonObject2.getString("Link");
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
//                    mContext.startActivity(browserIntent);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

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
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        AppCompatTextView botText;
        AppCompatTextView userText;
        AppCompatImageView car_image_chatbot;
        CardView cardview_image_chatbot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            botText = itemView.findViewById(R.id.botText);
            userText = itemView.findViewById(R.id.userText);
            car_image_chatbot = itemView.findViewById(R.id.car_image_chatbot);
            cardview_image_chatbot= itemView.findViewById(R.id.cardview_image_chatbot);
        }
    }
}
