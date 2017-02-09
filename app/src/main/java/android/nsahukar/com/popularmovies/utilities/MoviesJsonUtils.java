package android.nsahukar.com.popularmovies.utilities;

import android.content.Context;
import android.nsahukar.com.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Nikhil on 07/02/17.
 */

public final class MoviesJsonUtils {

    public static ArrayList<Movie> getPopularMoviesArrayFromJson(String popularMoviesJsonStr) throws JSONException {

        final String RESULTS = "results";
        final String MOVIE_ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String BACKDROP_PATH = "backdrop_path";


        JSONObject popularMoviesJsonObj = new JSONObject(popularMoviesJsonStr);
        JSONArray popularMoviesJsonArr = popularMoviesJsonObj.getJSONArray(RESULTS);

        ArrayList<Movie> popularMovies = new ArrayList<>();
        for (int i=0; i<popularMoviesJsonArr.length(); i++) {
            JSONObject movieJsonObj = popularMoviesJsonArr.getJSONObject(i);

            Movie movie = new Movie();
            movie.setId(movieJsonObj.getLong(MOVIE_ID));
            movie.setOriginalTitle(movieJsonObj.getString(ORIGINAL_TITLE));
            movie.setPosterPath(movieJsonObj.getString(POSTER_PATH));
            movie.setOverview(movieJsonObj.getString(OVERVIEW));
            movie.setVoteAverage(movieJsonObj.getDouble(VOTE_AVERAGE));
            movie.setReleaseDate(movieJsonObj.getString(RELEASE_DATE));
            movie.setBackdropPath(movieJsonObj.getString(BACKDROP_PATH));

            popularMovies.add(movie);
        }

        // sorting movie in descending order of vote_average
        if (popularMovies.size() > 0) {
            Collections.sort(popularMovies, new Comparator<Movie>() {
                @Override
                public int compare(Movie movie1, Movie movie2) {
                    return movie2.getVoteAverage() > movie1.getVoteAverage()? 1 : -1;
                }
            });
        }

        return popularMovies;
    }

}
