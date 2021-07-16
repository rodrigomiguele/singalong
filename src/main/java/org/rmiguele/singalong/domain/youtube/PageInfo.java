package org.rmiguele.singalong.domain.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PageInfo {

    private Long totalResults;

    private Long resultsPerPage;

    public Long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Long totalResults) {
        this.totalResults = totalResults;
    }

    public Long getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(Long resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }
}
