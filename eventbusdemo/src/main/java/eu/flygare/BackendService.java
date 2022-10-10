/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.flygare;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author jonfly
 */
@ApplicationScoped
public class BackendService {
    
    @Inject
    EventBus eventbus;
    
    @ConsumeEvent("EB_backend")
    public String handleBackendStuff(String data) {
        
        eventbus.publish("EB_mylogger", "Logging " + data);
        // Actually did a call to backend via restclient here, but fake for now.
        return "Backend handled " + data;
    }
}
