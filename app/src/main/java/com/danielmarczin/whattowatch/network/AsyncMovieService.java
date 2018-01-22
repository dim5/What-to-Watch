package com.danielmarczin.whattowatch.network;

import com.danielmarczin.whattowatch.model.Movie;

import io.reactivex.Single;

public interface AsyncMovieService {
    Single<Movie> getRandomMovie();
}
