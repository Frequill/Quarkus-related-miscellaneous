# redisbackend Project



Having a local redis on localhost:6379, test using:

curl -X POST http://localhost:8080/redis/setkey/foobar -d "12334" 
curl http://localhost:8080/redis/getkey/foobar

curl -v -X POST -H "Content-Type: text/plain" http://localhost:8080/redis/push/queue1 -d "dohello" 
curl -v -H "Accept: text/plain" http://localhost:8080/redis/bpop/queue1

Never mind the vertx redis client, powerful smoke in that one.

