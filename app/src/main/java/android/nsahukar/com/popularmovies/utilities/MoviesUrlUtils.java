package android.nsahukar.com.popularmovies.utilities;

import android.net.Uri;

/**
 * Created by Nikhil on 03/02/17.
 */

public final class MoviesUrlUtils {

    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String POPULAR_MOVIES_EXTENSION = "/movie/popular";
    private static final String TOP_RATED_MOVIES_EXTENSION = "/movie/top_rated";

    /*
        Enter your API key from the themoviedb.org site
     */
    private static final String apiKey = "08b58d6533f2853d6cca45e68b601a40";
    private static final String language = "en-US";

    private final static String API_KEY_PARAM = "api_key";
    private final static String LANGUAGE_PARAM = "language";


    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    public interface Image {
        String IMAGE_SIZE_92 = "w92";
        String IMAGE_SIZE_154 = "w154";
        String IMAGE_SIZE_185 = "w185";
        String IMAGE_SIZE_342 = "w342";
        String IMAGE_SIZE_500 = "w500";
        String IMAGE_SIZE_780 = "w780";
        String IMAGE_SIZE_ORIGINAL = "original";
        String IMAGE_SIZE_DEFAULT = IMAGE_SIZE_185;
    }

    private static String buildUrlWithExtension(String extension) {
        String extendedUrl = BASE_URL + extension;
        Uri builtUri = Uri.parse(extendedUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .build();
        return builtUri.toString();
    }

    private static String buildImageUrlWithExtension(String extension) {
        String extendedUrl = IMAGE_BASE_URL + extension;
        Uri builtUri = Uri.parse(extendedUrl).buildUpon()
                .build();
        return builtUri.toString();
    }

    public static String getPopularMoviesUrl() {
        return buildUrlWithExtension(POPULAR_MOVIES_EXTENSION);
    }

    public static String getTopRatedMoviesUrl() {
        return buildUrlWithExtension(TOP_RATED_MOVIES_EXTENSION);
    }

    public static String getMoviePosterUrl(String imageSize, String posterPath) {
        final String extension = imageSize + posterPath;
        return buildImageUrlWithExtension(extension);
    }

}
