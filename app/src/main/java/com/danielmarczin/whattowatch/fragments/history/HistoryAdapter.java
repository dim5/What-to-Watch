package com.danielmarczin.whattowatch.fragments.history;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielmarczin.whattowatch.MovieDetailActivity;
import com.danielmarczin.whattowatch.R;
import com.danielmarczin.whattowatch.db.AppDatabase;
import com.danielmarczin.whattowatch.model.Movie;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private static final String TAG = "HistoryAdapter";
    private final AppDatabase db;
    private final Context context;
    private final Disposable movieListLoadingSubscription;
    private List<Movie> movies = new ArrayList<>();
    private RecyclerView recyclerView;

    HistoryAdapter(Context context) {
        this.context = context;
        this.db = AppDatabase.getAppDatabase(context.getApplicationContext());

        movieListLoadingSubscription = db.getMovieDao().loadAllDecidedMoviesAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> aqMovies) {
                        movies = aqMovies;
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "DB error occurred.", e);
                        displayError();
                    }

                    @Override
                    public void onComplete() {
                        movies = new ArrayList<>();
                    }
                });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.row_history, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Movie movie = movies.get(position);

        holder.tvTitle.setText(movie.getTitle());

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.MOVIE_ID, movie.getId());
                context.startActivity(intent);
            }
        });

        if (movie.isRejected())
            holder.ivAccepted.setImageResource(R.drawable.close_box);
        else
            holder.ivAccepted.setImageResource(R.drawable.checkbox_marked);

        holder.itemView.setTag(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    void removeMovie(int position) {
        final Movie toDelete = movies.remove(position);

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.getMovieDao().deleteMovies(toDelete);
            }
        }).subscribeOn(Schedulers.io()).subscribe();

        notifyItemRemoved(position);
    }

    private void displayError() {
        Snackbar.make(recyclerView, R.string.database_error, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (movieListLoadingSubscription != null && !movieListLoadingSubscription.isDisposed())
            movieListLoadingSubscription.dispose();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final Button btnDetails;
        private final ImageView ivAccepted;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvListTitle);
            btnDetails = itemView.findViewById(R.id.btnHistoryDetails);
            ivAccepted = itemView.findViewById(R.id.ivIsAccepted);
        }
    }
}
