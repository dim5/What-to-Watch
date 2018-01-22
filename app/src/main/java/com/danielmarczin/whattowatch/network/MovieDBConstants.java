package com.danielmarczin.whattowatch.network;


final class MovieDBConstants {
    static final String API_KEY = "---- YOUR API KEY GOES HERE ----";
    static final String BASE_URL = "https://api.themoviedb.org/";
    static final String[] SORT_TYPES = {
            "popularity.asc", "popularity.desc", "release_date.asc", "release_date.desc", "revenue.asc",
            "revenue.desc", "primary_release_date.asc", "primary_release_date.desc", "original_title.asc",
            "original_title.desc", "vote_average.asc", "vote_average.desc", "vote_count.asc",
            "vote_count.desc"};

    static final int MAX_PAGES = 1000;
    static final int MIN_VOTES = 10;
    static final String IMG_URL = "https://image.tmdb.org/t/p/w500";
}
