/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import java.util.List;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 * @author flax
 */
public class CommandEntity {
    @JsonbProperty("rqueue")
    public String responseQueue;  // We must always supply where we listen for response
    @JsonbProperty("command")
    public String command;
    @JsonbProperty("arglist")
    public List<PairEntity<String,String>> arguments;
    
    public CommandEntity() {}
    public CommandEntity(String rqueue, String command, List<PairEntity<String,String>> args) {
        this.responseQueue=rqueue;
        this.command = command;
        this.arguments = args; // TODO: copy?
    }
    
}
