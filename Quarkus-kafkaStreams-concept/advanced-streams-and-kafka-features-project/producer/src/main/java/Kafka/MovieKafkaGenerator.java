package Kafka;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import entities.Movie;
import entities.MoviePlayed;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MovieKafkaGenerator {

    private Random random = new Random();

    @Inject
    Logger logger;

    // Made this final because why not?
    private final List<Movie> movies = List.of(
            new Movie(1, "The Hobbit", "Peter Jackson", "Fantasy"),
            new Movie(2, "Star Wars", "George Lucas", "Sci-fy/Fantasy"),
            new Movie(3, "The Godfather", "Francis Copula", "Crime/drama"),
            new Movie(4, "Spider-man 2", "Sam Rami", "Action-adventure"),
            new Movie(5, "Gone with the Wind", "Some Southern guy idk", "Historical-fiction")
    );

    // Populates movies into Kafka topic "movies"
    @Outgoing("movies")
    public Multi<Record<Integer, Movie>> movies() {
        return Multi.createFrom().items(movies.stream()
                .map(m -> Record.of(m.id, m))
        );
    }

    // Events generated to the "play-time-movies" channel(topic)
    @Outgoing("play-time-movies")
    public Multi<Record<String, MoviePlayed>> generate() {
        return Multi.createFrom().ticks().every(Duration.ofMillis(1000)) // A new event triggers every second
                .onOverflow().drop()
                .map(tick -> {
                    Movie movie = movies.get(random.nextInt(movies.size())); // Selects random movie and random time played
                    int time = random.nextInt(300);
                    logger.info("movie: " + movie.name + " played for: " + time + " minutes");

                    // A record (event/message) is returned as multi with region as key and a "PlayedMovie" Object as value
                    return Record.of("eu", new MoviePlayed(movie.id, time));
                });
    }



}