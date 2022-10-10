/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.flygare;

import io.quarkus.vertx.ConsumeEvent;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

/**
 *
 * @author jonfly
 */
@ApplicationScoped
public class LoggerService {
    
    public static final Logger LOG = Logger.getLogger("EventbusLogger");
    
    @ConsumeEvent("EB_mylogger")
    public void ReadLogdata(String data) {
        LOG.info("LoggerService got data: " + data);
    }
}
