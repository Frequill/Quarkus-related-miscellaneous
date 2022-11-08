Brought these notes with me from earlier project

# Guides used for this training project:

https://quarkus.io/guides/kafka-streams

https://www.infoq.com/articles/data-with-quarkus-kafka/

https://www.infoq.com/articles/quarkus-with-kafka-streams/


# Fun things I discovered:

 1. Annotating a Multi containing an entity with @Channel(*nameOfTopic*) will add every
single record added to the topic into this Multi and turn it into a type of list.  
This way, you can easily save the contents of a kafka topic programmatically without  
having to tell the application to update it as that is done automagically!

# Notes
1. A "Source processor" represents a Kafka topic. It sends the events
to other stream processors (one or multiple)
2. A "Stream processor" applies transformations or logic to input streams like joining,
grouping, counting, mapping and such. A stream processor can be connected to another
stream processor and/or a sink processor
3. A "Sink processor" represents the output data and is connected to a Kafka topic.
4. A topology is a graph with no cycles, composed of sources, processors, and sinks, and then passed 
into a Kafka Streams instance that will begin the execution of the topology.

The first thing to do when developing a Kafka Stream is to 
create the Topology instance and define the sources, 
processors, and sinks. In Quarkus, you only need to create a 
CDI class with a method returning a Topology instance.