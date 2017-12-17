package com.scheffer.erik.popularmovies.moviedatabaseapi;

public enum SearchCriteria {
    POPULAR("popular"), TOP_RATED("top_rated"), FAVORITE("favorite");

    private final String criteriaValue;

    SearchCriteria(String criteriaValue) {
        this.criteriaValue = criteriaValue;
    }

    public String getCriteria() {
        return this.criteriaValue;
    }
}
