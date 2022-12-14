package Topology;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import Entities.Movie;
import Entities.MoviePlayed;
import Logic.MoviePlayCount;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

@ApplicationScoped
public class TopologyProducer {

    private static final String MOVIES_TOPIC = "movies";
    private static final String PLAY_MOVIES_TOPIC = "play-time-movies";
    public static final String COUNT_MOVIE_STORE = "countMovieStore";
    KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(COUNT_MOVIE_STORE);

    @Produces
    public Topology getTopCharts() {

        final StreamsBuilder builder = new StreamsBuilder();


        // SerDes for Movie, PlayedMovie and moviePlayCount
        final ObjectMapperSerde<Movie> movieSerde = new ObjectMapperSerde<>(Movie.class);
        final ObjectMapperSerde<MoviePlayed> moviePlayedSerde = new ObjectMapperSerde<>(MoviePlayed.class);
        final ObjectMapperSerde<MoviePlayCount> moviePlayCountSerde = new ObjectMapperSerde<>(MoviePlayCount.class);


        // Creation of a Global Kafka Table for Movies topic
        final GlobalKTable<Integer, Movie> moviesTable = builder.globalTable(MOVIES_TOPIC, Consumed.with(Serdes.Integer(), movieSerde));


        // Stream connected to play-time-movies topic, every event produced there is consumed by this stream
        final KStream<String, MoviePlayed> playEvents = builder.stream(PLAY_MOVIES_TOPIC, Consumed.with(Serdes.String(), moviePlayedSerde));


        //  PlayedMovies has the region as key, and the object as value.
        //  Let’s map the content so the key is the movie id (to do the join) and leave the object as value
        //  Moreover, we do the join using the keys of the movies table (movieId) and the keys of the stream
        //  (we changed it to be the movieId too in the map method).

        //  Finally, the result is streamed to console

        playEvents
                /*
                Now this is pretty cool, we can use ".filter" to get rid of redundant information that we don't want
                This file filters by duration - only shows movies played for more than 10 minutes

                .filter((region, event) -> event.duration >= 10)
                */

                .map((key, value) -> KeyValue.pair(value.id, value)) // Now key is the id field

                // This is the join call seen before, where key is the movie id and value is the movie
                .join(moviesTable, (movieId, moviePlayedId) -> movieId, (moviePlayed, movie) -> movie)
                // Group events per key, in this case movie id
                .groupByKey(Grouped.with(Serdes.Integer(), movieSerde))
                // Aggregate method gets the MoviePlayCount object if already created (if not it creates it) and calls its aggregate method to increment by one the viwer counter
                .aggregate(MoviePlayCount::new,
                        (movieId, movie, moviePlayCounter) -> moviePlayCounter.aggregate(movie.name),
                        Materialized.<Integer, MoviePlayCount> as(storeSupplier)
                                .withKeySerde(Serdes.Integer())
                                .withValueSerde(moviePlayCountSerde)
                )
                .toStream()
                .print(Printed.toSysOut()); // Hopefully this will help print the result!
        return builder.build();

        // Had some issues with serialization and de-serialization so, I chickened out and made it print individual attributes.
        // Does Jackson suck or do I suck? Should use JsonB instead...
    }


}