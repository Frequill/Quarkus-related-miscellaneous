# Issues and Problems
1. I did not manage to get the "generateRequest" method inside the
"KafkaRequestsGenerator" klass to work using a random time interval. 
I wanted it to spit out a random request every x seconds with "x" being a random integer.
But Quarkus interpreted that to mean that I want it randomized once and then for every request
to be sent out every "THAT" seconds... I'll be using a hardcoded time interval now instead
as it's not that big of a problem, but I should look into that at a later date.

