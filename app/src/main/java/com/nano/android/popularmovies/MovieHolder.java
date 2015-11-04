package com.nano.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

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
     long movieId;
     List<Trailer> trailers;
     List<Review> reviews;

    public MovieHolder( String posterPath, String originalTitle, String overview
                    , String releaseDate, int voteAverage, long movieId) {

        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.movieId = movieId;
        trailers = new ArrayList<Trailer>();
        reviews = new ArrayList<Review>();
    }

    private MovieHolder(Parcel in) {

        posterPath = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readInt();
        movieId = in.readLong();
        trailers = in.readArrayList(Trailer.class.getClassLoader());
        reviews = in.readArrayList(Review.class.getClassLoader());
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
        dest.writeLong(movieId);
        dest.writeTypedList(trailers);
        dest.writeTypedList(reviews);
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
                + movieId + "\n";
    }

    /**
     * Inner Trailer class.
      */

    public static class Trailer implements Parcelable{
        long movieId;
        String trailerName;
        String key;

        public Trailer(long movieId, String trailerName, String key) {
            this.movieId = movieId;
            this.trailerName = trailerName;
            this.key = key;
        }

        private Trailer(Parcel in) {
            movieId = in.readLong();
            trailerName = in.readString();
            key = in.readString();
        }

        @Override
        public int describeContents() {return 0;}

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(movieId);
            dest.writeString(trailerName);
            dest.writeString(key);
        }

        public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
            @Override
            public Trailer createFromParcel(Parcel src) {return new Trailer(src);}
            @Override
            public Trailer[] newArray(int size) {return new Trailer[size];}
        };

        // For test
        @Override
        public String toString() {
            return movieId + "\n" + trailerName + "\n" + key;
        }
    }

    /**
     * Inner Review class.
     */
    public static class Review implements Parcelable{
        long movieId;
        String author;
        String content;

        public Review(long movieId, String author, String content) {
            this.movieId = movieId;
            this.author = author;
            this.content = content;
        }

        private Review(Parcel in) {
            movieId = in.readLong();
            author = in.readString();
            content = in.readString();
        }
        @Override
        public int describeContents() {return 0;}

        @Override
        public void writeToParcel(Parcel dest, int flag) {
            dest.writeLong(movieId);
            dest.writeString(author);
            dest.writeString(content);
        }

        public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {

            @Override
            public Review createFromParcel(Parcel src) {return new Review(src);}
            @Override
            public Review[] newArray(int size) {return new Review[size];}
        };

        // For test
        @Override
        public String toString() {
            return movieId + "\n" + author + "\n" + content;
        }
    }
}
