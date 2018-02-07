package com.me.njerucyrus.jobsapp2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.me.njerucyrus.models.JobPost;

import org.json.JSONException;
import org.json.JSONObject;

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final JobPost itemList = listItems.get(position);


        holder.txtTitle.setText(itemList.getTitle());
        holder.txtItemDescription.setText(itemList.getDescription());
        holder.txtDatePosted.setText(itemList.getPostedOn());
        holder.txtDeadline.setText(itemList.getDeadline());


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public TextView txtItemDescription;
        public TextView txtOptionDigit;
        public TextView txtDatePosted;
        public TextView txtDeadline;

        ProgressDialog progressDialog;
        RequestQueue requestQueue;

        public ViewHolder(final View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtItemDescription = (TextView) itemView.findViewById(R.id.txtItemDescription);
            txtOptionDigit = (TextView) itemView.findViewById(R.id.txtOptionDigit);
            txtDatePosted = (TextView)itemView.findViewById(R.id.txtDatePosted);
            txtDeadline = (TextView)itemView.findViewById(R.id.txtDeadline);



        }

    }
}
