/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.flygare;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 *
 * @author jonfly
 */
@ApplicationScoped
public class BackendService {

    public static final Logger LOG = Logger.getLogger("BackendService");
    
    @Inject
    EventBus eventbus;

    @RestClient
    BackendClient backend;

    @ConsumeEvent("EB_backend")
    public void handleBackendStuff(Message<String> data) {
        try {
            Thread.sleep(500L);
        } catch (InterruptedException ex) {
            LOG.warn("Sleep interrupted", ex);
        }
        
        Uni<String> result = backend.getHello();
        result.subscribe().with(
                success -> {
                    LOG.info("Got response from backend");
                    data.reply(success);
                },
                failure -> {
                    LOG.warn("Call to backend REST service failed", failure);
                    data.reply("Failed call to backend");
                });

       
        
        
    }
}
