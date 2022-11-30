Brought these notes with me from earlier project

# Guides used for this training project:

https://quarkus.io/guides/kafka-streams

https://www.infoq.com/articles/data-with-quarkus-kafka/

https://www.infoq.com/articles/quarkus-with-kafka-streams/

# Useful information/Good sources:
More detailed info and examples about JOINS:
https://blog.codecentric.de/crossing-streams-joins-apache-kafka


# Fun things I discovered:

 1. Annotating a Multi containing an entity with @Channel(*nameOfTopic*) will add every
single record added to the topic into this Multi and turn it into a type of list.  
This way, you can easily save the contents of a kafka topic programmatically without  
having to tell the application to update it as that is done automagically!

2. I could be doing something wrong here so take this with a grain of salt...
but applications running logic for Kafka Streams seems to dislike the idea of hot coding.
In my experience it's smart to kill the application before editing sourcecode.

3. Not only can a stream record be printed with "print(Printed.toSysOut())",
but you can also send the result to another Kafka Topic with ".to("topicName", logic)"  

4. Remember: You will want to add default constructors for your JSONB-entities
otherwise streams will refuse to join streams containing them!

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

# How exactly does a "ValueJoiner" from Kafka streams work?
The idea of a ValueJoiner is that you specify two things that "comes in" and one thing
that "comes out" in the parameters of the instance, like so:

ValueJoiner<String, Integer, String> valueJoiner = (myString, myInteger) -> {  
return myString + myInteger;  
}  

This means that a String and Integer will be joined and produce a new String using the "+" operator.  
Always keep in mind that the third argument IS the return value!

# Why isn't my joins working???  
By personal experience I've come to find that the most common cause for dysfunctional joins
stem from lack of understanding different types of joins and where they are meant to be implemented.  
  
Similar to various SQL languages, Kafka supports three types of joins:  
1. Inner join - Will produce some type of output when two topics hold one record each
that both share the same ID(!)
2. Outer join - "Checks" two topics for a record, if a record shows up in one topic it will 
attempt to join it with a record on the other topic if said record has the same ID, BUT 
if no record with the same ID exists on the other topic it will STILL produce some form of output,
but one of the two joined values/records will be set to NULL by default.
3. A left join will attempt to join EACH input in the first/primary topic with *something* on the other
source, noo need for a condition. If a separate record does NOT exist in the secondary topic, it will produce a value of NULL.

For more detailed information and some good examples of various joins as well as the
dos and don't's of joining different types of topics and producing different kinds out outputs
(to stream, to KTables ETC) see first link in topmost "sources" category