package com.example.carbazar.recyclerViewAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carbazar.Interfaces.OnOptionClickListener;
import com.example.carbazar.Models.chatOptions;
import com.example.carbazar.R;

import java.util.List;

public class recyclerViewAdapterChatbotOptions extends RecyclerView.Adapter<recyclerViewAdapterChatbotOptions.ViewHolder>  {

    private static final String TAG = "recycViewAdaptHome";

    private OnOptionClickListener onOptionClickListener;
    private List<chatOptions> list;
    private Context mContext;

    public recyclerViewAdapterChatbotOptions(Context mContext, List<chatOptions> list) {
        this.list = list;
        this.mContext = mContext;
        this.onOptionClickListener = ((OnOptionClickListener) mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_items_chatbot_options, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder : called.");
        final chatOptions chatOptionsObj = list.get(i);

        viewHolder.botOptions.setText(chatOptionsObj.getLabel());

        viewHolder.botOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionClickListener.onOptionClick(chatOptionsObj.getText(), chatOptionsObj.getLabel());
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
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        AppCompatButton botOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            botOptions = itemView.findViewById(R.id.option);
        }
    }
}
