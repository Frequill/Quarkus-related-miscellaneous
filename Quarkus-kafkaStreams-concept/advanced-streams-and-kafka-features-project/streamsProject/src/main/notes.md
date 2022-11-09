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


# What even is a "kafka table?"
When using a database you can easily overwrite old data with new data.
Say you have a table named "Employees" which holds people and their email addresses as well as their salaries
and one employee needs to update his email and also receives a pay raise... The old data
can be overwritten with the new data and now the old column is STILL THERE, but it holds DIFFERENT values
than before.

When using a message engine like Kafka, a topic can store data by keys. So if we have an
"employees" topic holding all our staff where each one has a unique "staff-ID" all seems fine...
but what if you want to change the value of one index?  
  
You would then "get" the specific employee, modify the data programmatically and then 
send this new record back to the topic with the updated data. The problem is that now
we have two different "employees" in our topic with the SAME KEY(!) 
One holds the old info, the newer one logically holds the new info where the data was updated.

By creating a Kafka table (KTable or GlobalKTable) we can retrieve all information from a broker 
but the table will by default ONLY retrieve ONE version of key and make sure that
the latest version is taken and the outdated ones are ignored. This way we 
always know what the relevant data is using Kafka streams!