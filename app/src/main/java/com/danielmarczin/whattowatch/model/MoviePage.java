package com.danielmarczin.whattowatch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class MoviePage {
    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("results")
    @Expose
    private List<Movie> movies = null;

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<Movie> getMovies() {
        return movies;
    }

}
