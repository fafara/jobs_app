package com.me.njerucyrus.jobsapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.me.njerucyrus.models.JobPost;
import com.me.njerucyrus.models.User;

import java.util.List;

/**
 * Created by njerucyrus on 1/25/18.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<JobPost> listItems;
    private Context mContext;


    public MyAdapter(List<JobPost> listItems, Context mContext) {
        this.listItems = listItems;
        this.mContext = mContext;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JobPost itemList = listItems.get(position);

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtTitle;
        public TextView txtItemDescription;
        public TextView txtOptionDigit;
        public RelativeTimeTextView timePosted;

        View mView;
        public ViewHolder(final View itemView) {
            super(itemView);


            itemView.setOnClickListener(this);


            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtItemDescription = (TextView) itemView.findViewById(R.id.txtItemDescription);
            txtOptionDigit = (TextView) itemView.findViewById(R.id.txtOptionDigit);
            timePosted = (RelativeTimeTextView) itemView.findViewById(R.id.timestamp);


        }

        @Override
        public void onClick(View view) {
            JobPost post  = listItems.get(getAdapterPosition());
            Intent intent = new Intent(mContext, JobPostDetailActivity.class);

            intent.putExtra("category", post.getCategory());
            intent.putExtra("description", post.getDescription());
            intent.putExtra("title", post.getTitle());
            intent.putExtra("location", post.getLocation());
            intent.putExtra("postedOn", post.getPostedOn());
            intent.putExtra("postedBy", post.getPostedBy());
            intent.putExtra("postedByUid", post.getPostedByUid());
            intent.putExtra("deadline", post.getDeadline());
            mContext.startActivity(intent);
        }
    }
}
