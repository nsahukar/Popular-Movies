package android.nsahukar.com.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nsahukar.com.popularmovies.data.Movie;
import android.nsahukar.com.popularmovies.network.DownloadCallback;
import android.nsahukar.com.popularmovies.utilities.MoviesJsonUtils;
import android.nsahukar.com.popularmovies.utilities.MoviesUrlUtils;
import android.nsahukar.com.popularmovies.network.NetworkFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

public class MoviesActivity extends AppCompatActivity implements NetworkFragment.OnCreatedListener, DownloadCallback<String> {

    private static final String TAG = "MoviesActivity";
    private static final String STATE_POPULAR_MOVIES = "popularMovies";

    private Toolbar mMoviesToolbar;
    private Toast mToast;

    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment mNetworkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean mDownloading = false;

    private ArrayList<Movie> mPopularMovies;
    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;


    private void loadPopularMovies() {
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment.startDownload();
            mDownloading = true;
        }
    }

    private void showToastWithMessage(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(MoviesActivity.this, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    private void showPopularMoviesPosters() {
        mMoviesAdapter.setPopularMovies(mPopularMovies);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        // set up toolbar
        mMoviesToolbar = (Toolbar) findViewById(R.id.tb_movies);
        setSupportActionBar(mMoviesToolbar);

        // set up recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_popular_movies);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MoviesActivity.this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        // set adapter to recycler view
        mMoviesAdapter = new MoviesAdapter(MoviesActivity.this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        // get data from savedInstanceState, if any, otherwise get data over the network
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_POPULAR_MOVIES)) {
            mPopularMovies = savedInstanceState.getParcelableArrayList(STATE_POPULAR_MOVIES);
            Log.d(TAG, "Popular movies count: " + mPopularMovies.size());
            showPopularMoviesPosters();
        } else {
            Log.d(TAG, "Getting popular movies from network");
            mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), MoviesUrlUtils.getPopularMoviesUrl());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMoviesToolbar = null;
        mToast = null;
        mNetworkFragment = null;
        mDownloading = false;
        mPopularMovies = null;
        mRecyclerView = null;
        mMoviesAdapter = null;
    }

    @Override
    public void onNetworkFragmentCreated() {
        loadPopularMovies();
    }

    @Override
    public void updateFromDownload(String result) {
        // Update your UI here based on result of download.
        try {
            mPopularMovies = MoviesJsonUtils.getPopularMoviesArrayFromJson(result);
            showPopularMoviesPosters();
        } catch (NullPointerException e) {
            showToastWithMessage("Please make sure you are connected to the internet");
            e.printStackTrace();
        } catch (JSONException e) {
            showToastWithMessage("Could not retrieve information, please try after some time");
            Log.e(TAG, "Invalid JSON Data for request - " + MoviesUrlUtils.getPopularMoviesUrl());
            Log.e(TAG, "Received response - " + result);
            e.printStackTrace();
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            case Progress.ERROR:
                break;
            case Progress.CONNECT_SUCCESS:
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save the current state
        if (mPopularMovies != null) {
            outState.putParcelableArrayList(STATE_POPULAR_MOVIES, mPopularMovies);
        }
        super.onSaveInstanceState(outState);
    }
}
