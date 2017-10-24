package com.example.infispace.ui;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infispace.R;
import com.example.infispace.data.Story;
import com.example.infispace.util.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private ArrayList<Story> mDataset;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView thumbnail;
        public TextView thumbtv, titletv, urltv;
        public CardView cardView;

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            thumbtv = (TextView) v.findViewById(R.id.thumb_tv);
            titletv = (TextView) v.findViewById(R.id.title_tv);
            urltv = (TextView) v.findViewById(R.id.url_tv);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FeedAdapter(ArrayList<Story> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_feed, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.mTextView.setText(mDataset[position]);
        final Story story = mDataset.get(position);
        holder.titletv.setText(story.getStoryTitle());
        holder.urltv.setText(story.getStoryUrl());
        holder.thumbtv.setText(story.getSharedByName());
        Picasso.with(mContext)
                .load(story.getSharedByPicUrl())
                .transform(new CircleTransform())
                .into(holder.thumbnail);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (URLUtil.isValidUrl(story.getStoryUrl())) {
                    Uri uri = Uri.parse(story.getStoryUrl()); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "Not a valid url", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
