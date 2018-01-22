package com.danielmarczin.whattowatch.network;


import com.danielmarczin.whattowatch.model.Movie;
import com.danielmarczin.whattowatch.model.MoviePage;
import com.danielmarczin.whattowatch.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RandomMovieServiceAsync implements AsyncMovieService {

    private final Random random = Util.random;
    private final MovieDBClient client;

    public RandomMovieServiceAsync() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder = new Retrofit.Builder().
                baseUrl(MovieDBConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync()); //use OkHttp's internal threadpool

        Retrofit retrofit = builder.client(httpClient.build()).build();
        client = retrofit.create(MovieDBClient.class);
    }

    @Override
    public Single<Movie> getRandomMovie() {
        return client.getMoviePage(MovieDBConstants.API_KEY, getOptions())
                .map(new Function<MoviePage, Movie>() {
                    @Override
                    public Movie apply(MoviePage moviePage) throws Exception {
                        Movie movie = Util.getRandomElement(moviePage.getMovies());
                        if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
                            movie.setPosterPath(MovieDBConstants.IMG_URL + movie.getPosterPath());
                        }
                        return movie;
                    }
                });
    }

    private Map<String, String> getOptions() {
        Map<String, String> uriOptions = new HashMap<>();
        uriOptions.put("sort_by", Util.getRandomElement(MovieDBConstants.SORT_TYPES));
        uriOptions.put("include_adult", "false");
        uriOptions.put("include_video", "false");
        uriOptions.put("language", "en-US");
        uriOptions.put("vote_count.gte", String.valueOf(MovieDBConstants.MIN_VOTES));
        uriOptions.put("page", String.valueOf(getPage()));
        //https://developers.themoviedb.org/3/discover

        return uriOptions;
    }

    private int getPage() {
        return random.nextInt(MovieDBConstants.MAX_PAGES) + 1;
    }

}
