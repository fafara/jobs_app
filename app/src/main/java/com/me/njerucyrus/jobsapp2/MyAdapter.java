package com.me.njerucyrus.jobsapp2;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.me.njerucyrus.models.JobPost;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by njerucyrus on 1/25/18.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<JobPost> listItems;
    private Context mContext;
    private FirebaseFirestore db;

    public MyAdapter(List<JobPost> listItems, Context mContext, FirebaseFirestore db) {
        this.listItems = listItems;
        this.mContext = mContext;
        this.db = db;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final JobPost itemList = listItems.get(position);

        String desc = itemList.getDescription()+"\nCategory: "+itemList.getCategory()+"\nDeadline "+itemList.getDeadline()+
                "\nposted by "+itemList.getPostedBy();
        holder.txtTitle.setText(itemList.getTitle()+" @"+itemList.getLocation());
        holder.txtItemDescription.setText(desc);
        holder.timePosted.setReferenceTime(itemList.getPostedOn().getTime());


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public TextView txtItemDescription;
        public TextView txtOptionDigit;
        public RelativeTimeTextView timePosted;

        ProgressDialog progressDialog;
        RequestQueue requestQueue;

        public ViewHolder(final View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtItemDescription = (TextView) itemView.findViewById(R.id.txtItemDescription);
            txtOptionDigit = (TextView) itemView.findViewById(R.id.txtOptionDigit);
            timePosted = (RelativeTimeTextView) itemView.findViewById(R.id.timestamp);

        }

    }
}
