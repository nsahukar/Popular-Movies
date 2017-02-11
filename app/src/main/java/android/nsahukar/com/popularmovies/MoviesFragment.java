package android.nsahukar.com.popularmovies;

import android.content.Context;
import android.nsahukar.com.popularmovies.data.Movie;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoviesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MoviesFragment extends Fragment {

    public static final String TAG = "MoviesFragment";
    private static final String SECTION_KEY = "section";

    private int mMovieSection;
    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;

    private OnFragmentInteractionListener mListener;

    public MoviesFragment() {
        // Required empty public constructor
    }

    public static MoviesFragment getInstance(int section) {
        MoviesFragment moviesFragment = new MoviesFragment();
        Bundle args = new Bundle();
        args.putInt(SECTION_KEY, section);
        moviesFragment.setArguments(args);
        return moviesFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(SECTION_KEY)) {
            mMovieSection = args.getInt(SECTION_KEY);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.getMoviesForFragmentAtSection(mMovieSection);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_movies, container, false);

        // set up recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        // set adapter to recycler view
        mMoviesAdapter = new MoviesAdapter(getContext());
        mRecyclerView.setAdapter(mMoviesAdapter);

        return mRecyclerView;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mRecyclerView = null;
        mMoviesAdapter = null;
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void getMoviesForFragmentAtSection(int section);
        void showMovieDetails(Movie movie);
    }

    public void setMovies(ArrayList<Movie> movies) {
        mMoviesAdapter.setMovies(movies);
    }
}
