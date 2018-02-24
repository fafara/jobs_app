package com.me.njerucyrus.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Comparator;

/**
 * Created by njerucyrus on 2/23/18.
 */

public class CustomJobPostComparator implements Comparator<JobPost> {

    @Override
    public int compare(JobPost post, JobPost t1) {
        if (post.getPostedOn().getTime() > t1.getPostedOn().getTime())
            return 1;
        else
            return 0;
    }
}
