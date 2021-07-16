package org.rmiguele.singalong.service;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.rmiguele.singalong.domain.youtube.SearchListResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/v3")
@RegisterRestClient
public interface YoutubeService {

    @GET
    @Path("/search")
    SearchListResponse search(
            @QueryParam("part") String part,
            @QueryParam("maxResults") Integer maxResults,
            @QueryParam("q") String query,
            @QueryParam("type") String type,
            @QueryParam("videoEmbeddable") String videoEmbeddable,
            @QueryParam("pageToken") String pageToken,
            @QueryParam("key") String key);

}
