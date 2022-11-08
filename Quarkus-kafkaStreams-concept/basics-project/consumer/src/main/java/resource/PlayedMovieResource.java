package resource;

import entities.Movie;
import entities.PlayedMovie;
import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/*
WHEN the /movies endpoint is invoked, this application WILL START STREAMING received in the @channel.
(You can make better endpoints later if you hate this)
*/
@Path("/movies")
public class PlayedMovieResource {

    @Inject
    Logger logger;

    /*
    Now THIS is cool. Every time some something is sent to this "played-movies" topic, it is automagically added
    to this multi! I can see a billion uses for this in the future
    */
    @Channel("played-movies")
    Multi<PlayedMovie> playedMovies;


    /*
    curl -N localhost:9090/movies

    We can observe the data as it is streamed automatically from the Kafka topic and sent as HTTP requests
    */
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS) // What even is a server sent event??
    public Multi<PlayedMovie> stream() {
        return playedMovies;
    }

    /*
    Every time a new movie is added to the movies topic in kafka, this method is called. The parameter "movie"
    is the events payload from the kafka topic!
    */
    @Incoming("movies")
    public void newMovie(Movie movie) {
        logger.infov("New movie: {0}", "Id: " + movie.id + " name: " + movie.name +
                " genre: " + movie.genre + " director: " + movie.director);
    }


}