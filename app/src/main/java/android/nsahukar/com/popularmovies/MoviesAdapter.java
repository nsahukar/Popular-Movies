package android.nsahukar.com.popularmovies;

import android.content.Context;
import android.nsahukar.com.popularmovies.data.Movie;
import android.nsahukar.com.popularmovies.utilities.MoviesUrlUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nikhil on 07/02/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private static final String TAG = "MoviesAdapter";
    private ArrayList<Movie> mPopularMovies;
    private Context mContext;

    public MoviesAdapter(Context context) {
        mContext = context;
    }


    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mMoviePosterImageView;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            mMoviePosterImageView = (ImageView) view.findViewById(R.id.iv_movie_poster);
        }
    }

    public void setPopularMovies(ArrayList<Movie> popularMovies) {
        mPopularMovies = popularMovies;
        notifyDataSetChanged();
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForGridItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForGridItem, parent, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        Movie popularMovie = mPopularMovies.get(position);
        final String popularMoviePosterUrl = MoviesUrlUtils.getPopularMoviePosterUrl(MoviesUrlUtils.Image.IMAGE_SIZE_DEFAULT, popularMovie.getPosterPath());
        Log.d(TAG, "poster url: " + popularMoviePosterUrl);
        Picasso.with(mContext).load(popularMoviePosterUrl).into(holder.mMoviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (mPopularMovies != null) {
            return mPopularMovies.size();
        }
        return 0;
    }

}
