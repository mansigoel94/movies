package com.example.mansi.movies;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    int id;
    private String mTitle = null;
    private String mPoster = null;
    private String mSynopsis = null;
    private float mRatings = -1.0f;
    private String mReleaseDate = null;

    public Movie(int id, String title, String poster, String synopsis,
                 float ratings, String releaseDate) {
        this.id = id;
        mTitle = title;
        mPoster = poster;
        mSynopsis = synopsis;
        mRatings = ratings;
        mReleaseDate = releaseDate;
    }

    public Movie(Parcel in) {
        id = in.readInt();
        mTitle = in.readString();
        mPoster = in.readString();
        mSynopsis = in.readString();
        mRatings = in.readFloat();
        mReleaseDate = in.readString();
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPoster() {
        return mPoster;
    }

    public float getRatings() {
        return mRatings;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(mTitle);
        parcel.writeString(mPoster);
        parcel.writeString(mSynopsis);
        parcel.writeFloat(mRatings);
        parcel.writeString(mReleaseDate);
    }

    @Override
    public String toString() {
        return "Title: " + mTitle + "\n" +
                "Plot: " + mSynopsis + "\n" +
                "Ratings: " + mRatings + "\n" +
                "Release year: " + Utility.formatDate(mReleaseDate) + "\n";
    }
}
