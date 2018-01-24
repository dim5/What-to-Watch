package com.danielmarczin.whattowatch.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielmarczin.whattowatch.GlideApp;
import com.danielmarczin.whattowatch.MovieDetailActivity;
import com.danielmarczin.whattowatch.R;
import com.danielmarczin.whattowatch.db.AppDatabase;
import com.danielmarczin.whattowatch.model.Movie;
import com.danielmarczin.whattowatch.network.AsyncMovieService;
import com.danielmarczin.whattowatch.network.RandomMovieServiceAsync;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.SerialDisposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ChooseFragment extends Fragment {

    private static final String TAG = "CHOOSEFRAG_TAG";
    private static AsyncMovieService asyncMovieService;
    private TextView tvTitle;
    private ImageView ivPoster;
    private View rootView;
    private Movie currentMovie;
    private Context appContext;
    private AppDatabase db;
    private SerialDisposable lastFetchSubscription;
    private CompositeDisposable disposables;


    public ChooseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getAppDatabase(appContext);

        if (asyncMovieService == null)
            asyncMovieService = new RandomMovieServiceAsync();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_choose, container, false);

        tvTitle = rootView.findViewById(R.id.tvTitle);
        ivPoster = rootView.findViewById(R.id.ivPoster);

        if (lastFetchSubscription == null || lastFetchSubscription.isDisposed())
            lastFetchSubscription = new SerialDisposable();
        if (disposables == null || disposables.isDisposed())
            disposables = new CompositeDisposable(lastFetchSubscription);

        setFabListeners();

        return rootView;
    }

    private void setFabListeners() {
        FloatingActionButton fabRejectMovie = rootView.findViewById(R.id.fabFetchMovie);
        fabRejectMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMovie != null) {
                    currentMovie.setRejected(true);
                    storeCurrentMovie();
                }
                fetchNewMovie();

            }
        });

        FloatingActionButton fabAcceptMovie = rootView.findViewById(R.id.fabHeart);
        fabAcceptMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMovie != null) {
                    currentMovie.setRejected(false);
                    storeCurrentMovie();

                    Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                    intent.putExtra(MovieDetailActivity.MOVIE_ID, currentMovie.getId());
                    startActivity(intent);
                }
                fetchNewMovie();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentMovie == null || currentMovie.isRejected() != null)
            loadMovie();
        else
            setMovie(currentMovie);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (currentMovie != null && currentMovie.isRejected() == null) {
            storeCurrentMovie();
        }
    }

    private void fetchNewMovie() {
        lastFetchSubscription.set(asyncMovieService.getRandomMovie()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Movie>() {
                    @Override
                    public void onSuccess(Movie movie) {
                        setMovie(movie);
                    }

                    @Override
                    public void onError(Throwable e) {
                        networkError();
                    }
                }));
    }

    private void networkError() {
        // Log.e(TAG, "Network error", e);
        displayError(R.string.network_error);
        if (currentMovie != null) {
            currentMovie = null;
            tvTitle.setText("");
            GlideApp.with(this)
                    .load(R.drawable.placeholder)
                    .into(ivPoster);
        }
    }

    private void fetchPoster() {
        GlideApp.with(this)
                .load(currentMovie.getPosterPath())
                .centerCrop()
                .transition(withCrossFade())
                .error(R.drawable.movie)
                .into(ivPoster);
    }

    private void storeCurrentMovie() {
        if (currentMovie != null) {
            Completable.fromAction(new Action() {
                @Override
                public void run() throws Exception {
                    db.getMovieDao().insertMovies(currentMovie);
                }
            }).subscribeOn(Schedulers.io()).subscribe();
        }
    }

    private void loadMovie() {
        disposables.add(db.getMovieDao().loadUndecidedMovieAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<Movie>() {
                    @Override
                    public void onSuccess(Movie movie) {
                        setMovie(movie);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "DB error", e);
                        displayError(R.string.database_error);
                    }

                    @Override
                    public void onComplete() {
                        fetchNewMovie();
                    }
                }));
    }

    private void setMovie(Movie movie) {
        currentMovie = movie;
        tvTitle.setText(currentMovie.getTitle());
        fetchPoster();
    }

    private void displayError(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                .show();
    }

    private void displayError(@StringRes int stringId) {
        Snackbar.make(rootView, stringId, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        disposables.dispose();
    }
}
