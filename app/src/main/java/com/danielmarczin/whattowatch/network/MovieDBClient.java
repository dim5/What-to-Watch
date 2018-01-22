package com.danielmarczin.whattowatch.network;


import com.danielmarczin.whattowatch.model.MoviePage;

import java.util.Map;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

interface MovieDBClient {
    @GET("/3/discover/movie")
    Single<MoviePage> getMoviePage(@Query("api_key") String api_key, @QueryMap Map<String, String> options);
}
