package android.nsahukar.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.nsahukar.com.popularmovies.data.Movie;
import android.nsahukar.com.popularmovies.utilities.MoviesUrlUtils;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public static final String TAG = "MovieDetailFragment";
    private Movie mMovie;


    public MovieDetailActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MovieDetailActivity movieDetailActivity = (MovieDetailActivity) getContext();
        Intent intent = movieDetailActivity.getIntent();
        mMovie = intent.getParcelableExtra(MovieDetailActivity.MOVIE_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        final ImageView movieBackdropImageView = (ImageView) view.findViewById(R.id.iv_movie_backdrop);
        Picasso.with(getContext())
                .load(MoviesUrlUtils.getMoviePosterUrl(MoviesUrlUtils.Image.IMAGE_SIZE_342, mMovie.getBackdropPath()))
                .into(movieBackdropImageView);

        final ImageView moviePosterImageView = (ImageView) view.findViewById(R.id.iv_movie_poster);
        Picasso.with(getContext())
                .load(MoviesUrlUtils.getMoviePosterUrl(MoviesUrlUtils.Image.IMAGE_SIZE_DEFAULT, mMovie.getPosterPath()))
                .into(moviePosterImageView);

        final TextView movieTitleTextView = (TextView) view.findViewById(R.id.tv_movie_title);
        movieTitleTextView.setText(mMovie.getOriginalTitle());

        final TextView movieReleaseDateTextView = (TextView) view.findViewById(R.id.tv_movie_release_date);
        movieReleaseDateTextView.setText(mMovie.getReleaseDate());

        final RatingBar movieRatingBar = (RatingBar) view.findViewById(R.id.rb_movie_rating);
        movieRatingBar.setRating((float) mMovie.getVoteAverage() / 2);

        final TextView moviePlotTextView = (TextView) view.findViewById(R.id.tv_movie_plot);
        moviePlotTextView.setText(mMovie.getOverview());

        return view;
    }
}
