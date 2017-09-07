package com.example.mansi.movies;

import java.util.ArrayList;

public class MovieSpecificInfo {
    private String mTrailerUrl;
    private ArrayList<String> mReviews;
    private int mDuration;

    public MovieSpecificInfo(String trailerUrl, ArrayList<String> reviews, int duration) {
        mTrailerUrl = trailerUrl;
        mReviews = reviews;
        mDuration = duration;
    }

    public ArrayList<String> getReviews() {
        return mReviews;
    }

    public int getDuration() {
        return mDuration;
    }

    public String getTrailerUrl() {
        return mTrailerUrl;
    }
}

