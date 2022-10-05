package org.gs;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.gs.proxy.EpisodeProxy;
import org.gs.proxy.TvSeriesProxy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/tvseries")
public class TvSeriesResource {

    @RestClient
    TvSeriesProxy proxy;

    @RestClient
    EpisodeProxy episodeProxy;

    private List<Show> tvSeries = new ArrayList<>();

    /**
     User inputs string and has a television shows episodes returned as list.

     In terminal spaces are removed so call like so:
     curl -G "http://localhost:8080/tvseries" --data-urlencode "title=game of thrones" <-- The spaces will be kept!
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@QueryParam("title") String title) {
        Show show = proxy.get(title);
        List<Episode> episodes = episodeProxy.get(show.getId());
        tvSeries.add(show);
        return Response.ok(episodes).build();
    }
}