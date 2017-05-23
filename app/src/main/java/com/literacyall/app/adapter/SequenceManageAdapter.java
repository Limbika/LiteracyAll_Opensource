package com.literacyall.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.literacyall.app.R;
import com.literacyall.app.listener.RecyclerViewClickListener;
import com.literacyall.app.utilities.CustomList;
import com.literacyall.app.utilities.ImageProcessing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ibrar on 11/7/2016.
 */

public class SequenceManageAdapter extends RecyclerView.Adapter<SequenceManageAdapter.ViewHolder> {

    Context context;
    private List<CustomList> sequenceList;
    private RecyclerViewClickListener itemListener;
    ImageProcessing imageProcessing;

    public SequenceManageAdapter(Context context, List<CustomList> sequenceList, RecyclerViewClickListener itemListener) {
        this.context = context;
        this.sequenceList = sequenceList;
        this.itemListener = itemListener;
        imageProcessing = new ImageProcessing(context);
    }


    @Override
    public SequenceManageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_row, null);
        SequenceManageAdapter.ViewHolder viewHolder = new SequenceManageAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SequenceManageAdapter.ViewHolder holder, int position) {
        imageProcessing.setImageWith_loader(holder.ivGridItem, sequenceList.get(position).imageName);
    }

    @Override
    public int getItemCount() {
        return sequenceList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivGridItem;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            ivGridItem = (ImageView) itemLayoutView.findViewById(R.id.ivGridItem);
            ivGridItem.setOnClickListener(this);
        }

        /**
         * implement Click Listener for ItemClick
         */
        @Override
        public void onClick(View view) {
            if (itemListener != null) {
                itemListener.recyclerViewListClicked(view, this.getAdapterPosition());
            }
        }
    }
}
