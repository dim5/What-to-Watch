package com.danielmarczin.whattowatch.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.danielmarczin.whattowatch.model.Movie;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(Movie... movies);

    @Update
    void updateMovies(Movie... movies);

    @Delete
    void deleteMovies(Movie... movies);

    @Query("SELECT * FROM movie")
    List<Movie> loadAllMovies();

    @Query("SELECT * FROM movie")
    Maybe<List<Movie>> loadAllMoviesAsync();

    @Query("SELECT * FROM movie WHERE isRejected is not null ORDER BY title")
    List<Movie> loadAllDecidedMovies();

    @Query("SELECT * FROM movie WHERE isRejected is not null ORDER BY title")
    Maybe<List<Movie>> loadAllDecidedMoviesAsync();

    @Query("SELECT * FROM movie WHERE id=:id")
    Movie findMovieById(int id);

    @Query("SELECT * FROM movie WHERE id=:id")
    Maybe<Movie> findMovieByIdAsync(int id);

    @Query("SELECT COUNT(*) FROM movie")
    int countMovies();

    @Query("SELECT COUNT(*) FROM movie")
    Single<Integer> countMoviesAsync();

    @Query("SELECT COUNT(*) FROM movie WHERE isRejected=:rejected")
    int countMovies(boolean rejected);

    @Query("SELECT COUNT(*) FROM movie WHERE isRejected=:rejected")
    Single<Integer> countMoviesAsync(boolean rejected);

    @Query("SELECT * FROM movie WHERE isRejected is null LIMIT 1")
    Movie loadUndecidedMovie();

    @Query("SELECT * FROM movie WHERE isRejected is null LIMIT 1")
    Maybe<Movie> loadUndecidedMovieAsync();
}
