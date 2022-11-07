package org.acme.kafka.streams.aggregator.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

// The @RegisterForReflection annotation instructs Quarkus to keep the class and its members during the native compilation
@RegisterForReflection
public class WeatherStation {

    public int id;
    public String name;
}