/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

/**
 *
 * @author flax
 */
public class ResponseEntity {
    public String requestId;
    public Integer status;
    public String message;
    public String response;
    public ResponseEntity() {}
    public ResponseEntity(String requestId, Integer status, String message, String response) {
        this.requestId=requestId;
        this.status = status;
        this.message=message;
        this.response = response;
    }
}
