package com.danielmarczin.whattowatch;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielmarczin.whattowatch.db.AppDatabase;
import com.danielmarczin.whattowatch.model.Movie;

import java.util.InputMismatchException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_ID = "MOVIE_ID";
    private Movie movie;

    private ImageView ivPoster;

    private TextView tvOriginalTitle,
            tvReleaseDate,
            tvOriginalLanguage,
            tvVoteAverage,
            tvOverView;

    private CollapsingToolbarLayout toolbarLayout;

    private Disposable loadMovieSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();

        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        Intent incomingIntent = this.getIntent();
        final int movieID = incomingIntent.getIntExtra(MOVIE_ID, -1);

        if (movieID == -1) {
            if (BuildConfig.DEBUG)
                throw new InputMismatchException("No movie id found.");
            finish();
        }


        ivPoster = findViewById(R.id.ivDetailPoster);
        tvOriginalTitle = findViewById(R.id.tvOriginalTitle);
        tvReleaseDate = findViewById(R.id.tvReleaseDate);
        tvOriginalLanguage = findViewById(R.id.tvOriginalLanguage);
        tvVoteAverage = findViewById(R.id.tvVoteAverage);
        tvOverView = findViewById(R.id.tvOverView);

        AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
        loadMovie(movieID, db);

        toolbarLayout = findViewById(R.id.toolbar_layout);


        FloatingActionButton fabSearch = findViewById(R.id.fabSearchMovie);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movie == null)
                    return;
                Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
                searchIntent.putExtra(SearchManager.QUERY, movie.getTitle() + " " + movie.getReleaseDate());
                startActivity(searchIntent);
            }
        });
    }

    private void loadMovie(final int id, final AppDatabase db) {
        loadMovieSubscription = db.getMovieDao().findMovieByIdAsync(id).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<Movie>() {
                    @Override
                    public void onSuccess(Movie aqMovie) {
                        movie = aqMovie;
                        initFields();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Snackbar.make(ivPoster, R.string.database_error, Snackbar.LENGTH_LONG)
                                .show();
                    }

                    @Override
                    public void onComplete() {
                        finish();
                    } // movie not found
                });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { //return to the same fragment
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initFields() {
        fetchPoster();
        toolbarLayout.setTitle(movie.getTitle());

        tvOriginalTitle.setText(movie.getOriginalTitle());
        tvReleaseDate.setText(movie.getReleaseDate());
        tvOriginalLanguage.setText(movie.getOriginalLanguage());
        tvVoteAverage.setText(String.valueOf(movie.getVoteAverage()));

        String overview = movie.getOverview();
        if (overview == null || overview.isEmpty())
            tvOverView.setText("-");
        else
            tvOverView.setText(overview);

    }

    private void fetchPoster() {
        GlideApp.with(this)
                .load(movie.getPosterPath())
                .centerCrop()
                .error(R.drawable.movie)
                .into(ivPoster);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadMovieSubscription != null && !loadMovieSubscription.isDisposed())
            loadMovieSubscription.dispose();
    }
}
