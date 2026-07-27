package com.computrade.course.spring.ai.tools.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CompanyNews(
        @JsonProperty("headline") String headline,
        @JsonProperty("source") String source,
        @JsonProperty("url") String url,
        @JsonProperty("summary") String summary,
        @JsonProperty("datetime") long datetime
) implements MarketTimeAware {

    @Override
    @JsonIgnore
    public long getEpochSeconds() {
        return this.datetime;
    }

}