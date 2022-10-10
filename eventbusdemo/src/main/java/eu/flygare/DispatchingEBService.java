/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.flygare;

import io.quarkus.vertx.ConsumeEvent;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author jonfly
 */
@ApplicationScoped
public class DispatchingEBService {
    
    @ConsumeEvent("EB_dispatch")
    public ResponseEntity doDispatch(RequestEntity input) {
        System.out.println("Request type : " + input.requestType);
        
        switch (input.requestType) {
            case "login" -> {
                return doLogin(input.data);
            } 
            case "logout" -> {
                return doLogout(input.data);
            }
            default -> {
                ResponseEntity resp = new ResponseEntity();
                resp.resultStatus = "FAIL";
                resp.responsetype = "ERROR";
                resp.data = "NULL";
                return resp;
            }
        }
        
        
    }

    private ResponseEntity doLogin(String data) {
        ResponseEntity resp = new ResponseEntity();
        resp.resultStatus = "OK";
        resp.responsetype = "loginresponse";
        resp.data = "MYTOKEN";
        return resp;
    }

    private ResponseEntity doLogout(String data) {
        ResponseEntity resp = new ResponseEntity();
        resp.resultStatus = "OK";
        resp.responsetype = "logoutresponse";
        resp.data = "MYTOKEN NOW INVALID";
        return resp;
    }
    
}
