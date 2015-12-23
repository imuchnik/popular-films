package imuchnik.android.com.nanoproject1;

/**
 * Created by imuchnik on 12/8/15.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by poornima-udacity on 6/26/15.
 */
public class Movie implements Parcelable {
    String movieTitle;
    String movieDescription;
    String posterPath; // drawable reference id
    private String voteAverage;

    public Movie(String movieTitle, String movieDescription, String posterPath, String voteAverage, String releaseDate) {
        this.movieTitle = movieTitle;
        this.movieDescription = movieDescription;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    private String releaseDate;

    public String getMovieTitle() {
        return movieTitle;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public String getPoster() {
        return posterPath;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieTitle);
        dest.writeString(movieDescription);
        dest.writeString(posterPath);
        dest.writeString(voteAverage);
        dest.writeString(releaseDate);

    }

    private Movie(Parcel in) {
        movieTitle = in.readString();
        movieDescription = in.readString();
        posterPath = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
