package com.scheffer.erik.popularmovies.MovieDatabaseApi;

import android.net.Uri;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.DataClasses.Movie;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.DataClasses.Review;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.DataClasses.Trailer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.scheffer.erik.popularmovies.MovieDatabaseApi.ApiConstants.MOVIES_DATABASE_API_KEY;
import static com.scheffer.erik.popularmovies.MovieDatabaseApi.ApiConstants.MOVIES_DATABASE_BASE_URL;

public class ApiConnection {

    public static List<Movie> getMovies(SearchCriteria criteria) throws IOException {
        Request request = new Request.Builder()
                .url(buildMovieListURL(criteria))
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        try {
            ResponseBody r = response.body();
            if (r != null) {
                return extractMoviesList(r.string());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<Trailer> getMovieTrailers(Long movieId) throws IOException {
        Request request = new Request.Builder()
                .url(buildTrailersURL(movieId))
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        try {
            ResponseBody r = response.body();
            if (r != null) {
                return extractTrailersList(r.string());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<Review> getMovieReviews(Long movieId) throws IOException {
        Request request = new Request.Builder()
                .url(buildRewviwsURL(movieId))
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        try {
            ResponseBody r = response.body();
            if (r != null) {
                return extractReviewsList(r.string());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static URL buildMovieListURL(SearchCriteria criteria) {
        Uri builtUri = Uri.parse(MOVIES_DATABASE_BASE_URL + criteria.getCriteria()).buildUpon()
                          .appendQueryParameter("api_key", MOVIES_DATABASE_API_KEY)
                          .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static List<Movie> extractMoviesList(String result) throws JSONException {
        Type type = new TypeToken<ArrayList<Movie>>() {}.getType();

        return (List<Movie>) extractList(result, type);
    }

    private static URL buildTrailersURL(Long movieId) {
        Uri builtUri = Uri.parse(MOVIES_DATABASE_BASE_URL + movieId + "/videos").buildUpon()
                          .appendQueryParameter("api_key", MOVIES_DATABASE_API_KEY)
                          .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static List<Trailer> extractTrailersList(String result) throws JSONException {
        Type type = new TypeToken<ArrayList<Trailer>>() {}.getType();
        return (List<Trailer>) extractList(result, type);
    }

    private static URL buildRewviwsURL(Long movieId) {
        Uri builtUri = Uri.parse(MOVIES_DATABASE_BASE_URL + movieId + "/reviews").buildUpon()
                          .appendQueryParameter("api_key", MOVIES_DATABASE_API_KEY)
                          .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static List<Review> extractReviewsList(String result) throws JSONException {
        Type type = new TypeToken<ArrayList<Review>>() {}.getType();
        return (List<Review>) extractList(result, type);
    }

    private static List extractList(String result, Type type) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        Gson gson = buildGson();
        return gson.fromJson(jsonObject.getJSONArray("results").toString(), type);
    }

    private static Gson buildGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }
}
