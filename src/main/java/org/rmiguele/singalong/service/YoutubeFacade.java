package org.rmiguele.singalong.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.rmiguele.singalong.domain.Page;
import org.rmiguele.singalong.domain.Video;
import org.rmiguele.singalong.domain.youtube.SearchListResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Collectors;

@ApplicationScoped
public class YoutubeFacade {

    @ConfigProperty(name = "google.api.key")
    String key;

    @ConfigProperty(name = "max.items.per.page")
    Integer maxItemsPerPage;

    @Inject
    @RestClient
    YoutubeService youtubeService;

    public Page searchVideos(String query, String pageToken) {
        SearchListResponse searchListResponse = doSearch(query, pageToken);
        Page page = new Page();
        page.setPage(pageToken);
        page.setNextPage(searchListResponse.getNextPageToken());
        page.setPreviousPage(searchListResponse.getPrevPageToken());
        page.setVideos(searchListResponse.getItems().stream().map(searchResult -> {
            Video video = new Video();
            video.setVideo(searchResult.getId().getVideoId());
            video.setThumbnailUrl(searchResult.getSnippet().getThumbnails().getMedium().getUrl());
            video.setTitle(searchResult.getSnippet().getTitle());
            return video;
        }).collect(Collectors.toList()));
        return page;
    }

    private SearchListResponse doSearch(String query, String pageToken) {
        return youtubeService
                .search("snippet",
                        maxItemsPerPage,
                        query.replace(" ", "+") + "+karaoke",
                        "video",
                        "true",
                        pageToken,
                        key);
    }
}
