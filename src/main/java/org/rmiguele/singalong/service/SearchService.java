package org.rmiguele.singalong.service;

import org.apache.commons.lang3.StringUtils;
import org.rmiguele.singalong.domain.Page;
import org.rmiguele.singalong.domain.Video;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
@Path("/search")
public class SearchService {

    @Inject
    YoutubeFacade youtubeFacade;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Page searchMusic(@QueryParam("query") String query, @QueryParam("page") String page) {
        return youtubeFacade.searchVideos(StringUtils.defaultString(query, ""), page);
    }
}
