package com.scheffer.erik.popularmovies.moviedatabaseapi

enum class SearchCriteria {
    POPULAR, TOP_RATED, FAVORITE;

    fun isOnlineResource() = this == POPULAR || this == TOP_RATED
}