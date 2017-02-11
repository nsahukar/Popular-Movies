package android.nsahukar.com.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nsahukar.com.popularmovies.data.Movie;
import android.nsahukar.com.popularmovies.network.DownloadCallback;
import android.nsahukar.com.popularmovies.utilities.MoviesJsonUtils;
import android.nsahukar.com.popularmovies.utilities.MoviesUrlUtils;
import android.nsahukar.com.popularmovies.network.NetworkFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

public class MoviesActivity extends AppCompatActivity implements DownloadCallback<String>, MoviesFragment.OnFragmentInteractionListener {

    private static final String TAG = "MoviesActivity";
    private static final int SECTION_POPULAR_MOVIES = 0;
    private static final int SECTION_TOP_RATED_MOVIES = 1;
    private static final String STATE_POPULAR_MOVIES = "popularMovies";
    private static final String STATE_TOP_RATED_MOVIES = "topRatedMovies";

    private ViewPager mMoviesViewPager;
    private Toast mToast;

    private NetworkFragment mNetworkFragment;
    private MoviesFragment mPopularMoviesFragment;
    private MoviesFragment mTopRatedMoviesFragment;

    private ArrayList<Movie> mPopularMovies;
    private ArrayList<Movie> mTopRatedMovies;



    private void getMoviesForSection(int section) {
        switch (section) {
            case SECTION_POPULAR_MOVIES:
                if (mPopularMovies != null) {
                    showMoviePostersForSection(section);
                } else {
                    mNetworkFragment.addRequestUrl(MoviesUrlUtils.getPopularMoviesUrl());
                }
                break;

            case SECTION_TOP_RATED_MOVIES:
                if (mTopRatedMovies != null) {
                    showMoviePostersForSection(section);
                } else {
                    mNetworkFragment.addRequestUrl(MoviesUrlUtils.getTopRatedMoviesUrl());
                }
                break;
        }
    }

    private void showMoviePostersForSection(int section) {
        switch (section) {
            case SECTION_POPULAR_MOVIES:
                mPopularMoviesFragment.setMovies(mPopularMovies);
                break;

            case SECTION_TOP_RATED_MOVIES:
                mTopRatedMoviesFragment.setMovies(mTopRatedMovies);
                break;
        }
    }

    private void showToastWithMessage(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(MoviesActivity.this, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    private class MovieSectionsPagerAdapter extends FragmentPagerAdapter {

        public MovieSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case SECTION_POPULAR_MOVIES:
                    if (mPopularMoviesFragment == null) {
                        Log.d(TAG, "creating popular movies fragment");
                        mPopularMoviesFragment = MoviesFragment.getInstance(SECTION_POPULAR_MOVIES);
                    }
                    return mPopularMoviesFragment;

                case SECTION_TOP_RATED_MOVIES:
                    if (mTopRatedMoviesFragment == null) {
                        Log.d(TAG, "creating top rated movies fragment");
                        mTopRatedMoviesFragment = MoviesFragment.getInstance(SECTION_TOP_RATED_MOVIES);
                    }
                    return mTopRatedMoviesFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position) {
                case SECTION_POPULAR_MOVIES:
                    title = getString(R.string.section_popular_movies_title);
                    break;

                case SECTION_TOP_RATED_MOVIES:
                    title = getString(R.string.section_top_rated_movies_title);
                    break;
            }
            return title;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        Log.d(TAG, "onCreate called");

        mNetworkFragment = (NetworkFragment) getSupportFragmentManager().findFragmentByTag(NetworkFragment.TAG);
        if (mNetworkFragment == null) {
            mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager());
        }
        mPopularMoviesFragment = (MoviesFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vp_movies + ":" + SECTION_POPULAR_MOVIES);
        mTopRatedMoviesFragment = (MoviesFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vp_movies + ":" + SECTION_TOP_RATED_MOVIES);

        // get data from savedInstanceState
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_POPULAR_MOVIES)) {
                mPopularMovies = savedInstanceState.getParcelableArrayList(STATE_POPULAR_MOVIES);
            }
            if (savedInstanceState.containsKey(STATE_TOP_RATED_MOVIES)) {
                mTopRatedMovies = savedInstanceState.getParcelableArrayList(STATE_TOP_RATED_MOVIES);
            }
        }

        // set up toolbar
        Toolbar moviesToolbar = (Toolbar) findViewById(R.id.tb_movies);
        setSupportActionBar(moviesToolbar);

        // set up view pager
        mMoviesViewPager = (ViewPager) findViewById(R.id.vp_movies);
        MovieSectionsPagerAdapter movieSectionsPagerAdapter = new MovieSectionsPagerAdapter(getSupportFragmentManager());
        mMoviesViewPager.setAdapter(movieSectionsPagerAdapter);

        // set up tab layout
        TabLayout moviesTabLayout = (TabLayout) findViewById(R.id.tl_movies);
        moviesTabLayout.setupWithViewPager(mMoviesViewPager);

    }

    @Override
    protected void onPause() {
        super.onPause();

        // this means that this activity will not be recreated now, user is leaving it
        // or the activity is otherwise finishing
        if (isFinishing()) {
            Log.d(TAG, "activity finishing!!!");
            FragmentManager fragmentManager = getSupportFragmentManager();
            for (Fragment fragment : fragmentManager.getFragments()) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState called");
        // save the current state
        if (mPopularMovies != null) {
            outState.putParcelableArrayList(STATE_POPULAR_MOVIES, mPopularMovies);
            outState.putParcelableArrayList(STATE_TOP_RATED_MOVIES, mTopRatedMovies);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "on stop called");
        Log.d(TAG, "Number of fragments attached: " + getSupportFragmentManager().getFragments().size());
    }

    /**
     *  Network Fragment Callbacks
     */

    @Override
    public void updateFromDownload(String result, String url) {
        // Update your UI here based on result of download.
        try {
            if (url.equals(MoviesUrlUtils.getPopularMoviesUrl())) {
                mPopularMovies = MoviesJsonUtils.getPopularMoviesArrayFromJson(result);
                showMoviePostersForSection(SECTION_POPULAR_MOVIES);
            }
            else if (url.equals(MoviesUrlUtils.getTopRatedMoviesUrl())) {
                mTopRatedMovies = MoviesJsonUtils.getTopRatedMoviesArrayFromJson(result);
                showMoviePostersForSection(SECTION_TOP_RATED_MOVIES);
            }
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
    public void onProgressUpdate(int progressCode, int percentComplete, String url) {
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
    public void finishDownloading(String url) {

    }


    /**
     *  Movies Fragment Callbacks
     */

    @Override
    public void getMoviesForFragmentAtSection(int section) {
        Log.d(TAG, "movie fragment created for section: " + section);
        getMoviesForSection(section);
    }

    @Override
    public void showMovieDetails(Movie movie) {

    }

}
