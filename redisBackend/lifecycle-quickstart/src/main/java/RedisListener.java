import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@ApplicationScoped
public class RedisListener {

    private static final Logger logger = Logger.getLogger("ListenerBean");

    @Inject
    ReactiveRedisDataSource redis;

    public void listenToRedis(@Observes StartupEvent event) {
        logger.info("Listening to redis... ");

        Multi.createBy().repeating().uni(AtomicInteger::new, (x) -> redis.list())
    }




}
