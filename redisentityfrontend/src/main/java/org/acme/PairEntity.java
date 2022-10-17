/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import javax.json.bind.annotation.JsonbProperty;

/**
 *
 * @author flax
 */
public class PairEntity<K,V> {
    @JsonbProperty("key")
    public K key;
    @JsonbProperty("value")
    public V value;
    public PairEntity() {}
    public PairEntity(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
