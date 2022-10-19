import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.logging.Logger;

@ApplicationScoped
public class LifeBean {
    private static final Logger logger = Logger.getLogger("ListenerBean");

    void onStart(@Observes StartupEvent event) {
        logger.info("Starting the app...");
    }

    void onStop(@Observes ShutdownEvent event) {
        logger.info("Killing the app...");
    }





}