package com.scheffer.erik.popularmovies.MovieDatabaseApi;

public enum SearchCriteria {
    POPULAR("popular"), TOP_RATED("top_rated");

    private final String criteriaValue;

    SearchCriteria(String criteriaValue) {
        this.criteriaValue = criteriaValue;
    }

    public String getCriteria() {
        return this.criteriaValue;
    }
}
