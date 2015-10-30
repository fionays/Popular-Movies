package com.nano.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * ImageHolder class which encapsulate object that is displayed in each entry of AdapterView.
 * It implements Parcelable interface.
 *
 * Created by YANG on 9/21/2015.
 */
public class MovieHolder implements Parcelable {
    //TODO: Still need drawableID??????????? If image load unsuccessfully
    //private final int drawableID;
    // Movie information extracted from completed JSON format.
     String posterPath;
     String originalTitle;
     String overview;
     String releaseDate;
     int voteAverage;
     int id;

    public MovieHolder( String posterPath, String originalTitle, String overview
                    , String releaseDate, int voteAverage, int id) {

        //this.drawableID = drawableID;
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.id = id;
    }

    private MovieHolder(Parcel in) {

        //drawableID = in.readInt();
        posterPath = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readInt();
        id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(posterPath);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeInt(voteAverage);
        dest.writeInt(id);
    }

    public static final Parcelable.Creator<MovieHolder> CREATOR = new Creator<MovieHolder>() {
        @Override
        public MovieHolder createFromParcel(Parcel src) {
            return new MovieHolder(src);
        }

        @Override
        public MovieHolder[] newArray(int size) {
            return new MovieHolder[size];
        }
    };

    // For test. Method getMovieDataFromJson in MovieFragment will print each MovieHolder object.
    @Override
    public String toString() {
        return posterPath + "\n"
                + originalTitle + "\n"
                + overview + "\n"
                + releaseDate + "\n"
                + voteAverage + "\n"
                + id + "\n";
    }
}
