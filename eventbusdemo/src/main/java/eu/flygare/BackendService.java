/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.flygare;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author jonfly
 */
@ApplicationScoped
public class BackendService {

    @Inject
    EventBus eventbus;

    @RestClient
    BackendClient backend;

    @ConsumeEvent("EB_backend")
    public void handleBackendStuff(Message<String> data) {

        Uni<String> result = backend.getHello();
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException ex) {
            Logger.getLogger(BackendService.class.getName()).log(Level.SEVERE, null, ex);
        }

        result.subscribe().with(success -> {
            data.reply(success);
        }, failure -> {
            data.reply("Failed call to backend");
        });

    }
}
