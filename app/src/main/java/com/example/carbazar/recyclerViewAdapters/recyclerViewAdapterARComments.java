package com.example.carbazar.recyclerViewAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carbazar.Models.ARComments;
import com.example.carbazar.R;

import java.util.List;

public class recyclerViewAdapterARComments extends RecyclerView.Adapter<recyclerViewAdapterARComments.ViewHolder>  {

    private static final String TAG = "recycViewAdaptHome";

    private List<ARComments> list;
    private Context mContext;

    public recyclerViewAdapterARComments(Context mContext, List<ARComments> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_items_ar_comments, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder : called.");
        final ARComments ARCommentsObj = list.get(i);

        viewHolder.comment.setText(ARCommentsObj.getComment());
        viewHolder.commenterName.setText(ARCommentsObj.getCommenterName());

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

        AppCompatTextView commenterName;
        AppCompatTextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            commenterName = itemView.findViewById(R.id.commenterName);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}
