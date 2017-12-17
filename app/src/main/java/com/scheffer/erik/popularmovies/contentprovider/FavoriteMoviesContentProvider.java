package com.scheffer.erik.popularmovies.contentprovider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.scheffer.erik.popularmovies.database.FavoriteMovieContract;
import com.scheffer.erik.popularmovies.database.FavoriteMovieDbHelper;

public class FavoriteMoviesContentProvider extends ContentProvider {

    public static final int FAVORITES = 100;
    public static final int FAVORITE_WITH_ID = 101;
    public static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY,
                          FavoriteMovieContract.PATH_FAVORIES,
                          FAVORITES);
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY,
                          FavoriteMovieContract.PATH_FAVORIES + "/#",
                          FAVORITE_WITH_ID);

        return uriMatcher;
    }

    private FavoriteMovieDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new FavoriteMovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor returnCursor;
        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                returnCursor = database.query(FavoriteMovieContract.MovieEntry.TABLE_NAME,
                                              null,
                                              null,
                                              null,
                                              null,
                                              null,
                                              null);
                break;
            case FAVORITE_WITH_ID:
                returnCursor = database.query(FavoriteMovieContract.MovieEntry.TABLE_NAME,
                                              null,
                                              FavoriteMovieContract.MovieEntry.COLUMN_EXTERNAL_ID + "=?",
                                              new String[]{uri.getPathSegments().get(1)},
                                              null,
                                              null,
                                              null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if (context != null) {
            returnCursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        Uri returnUri;
        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                long id = database.insert(FavoriteMovieContract.MovieEntry.TABLE_NAME,
                                          null,
                                          values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMovieContract.MovieEntry.CONTENT_URI,
                                                           id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int favoritesDeleted;
        switch (match) {
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                favoritesDeleted = database.delete(FavoriteMovieContract.MovieEntry.TABLE_NAME,
                                                   FavoriteMovieContract.MovieEntry._ID + "=?",
                                                   new String[]{String.valueOf(id)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (favoritesDeleted != 0) {
            Context context = getContext();
            if (context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return favoritesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}