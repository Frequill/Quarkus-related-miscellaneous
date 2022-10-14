/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.list.KeyValue;
import io.quarkus.redis.datasource.list.ReactiveListCommands;
import io.quarkus.redis.datasource.string.ReactiveStringCommands;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.logging.Logger;

/**
 *
 * @author flax
 */
@ApplicationScoped
public class EventBusListener {

     /* Mina frågor:
    1. Vad exakt är det denna metod begär för argument? Det verkar som att den gör en rpush på en lista vid namn "tobackend"
    inuti redis men eftersom att (det verkar som att) den tas bort så kan jag inte vara 100% säker.
    Så skall argumentet bara vara en datatyp som man temporärt kastar in i redis? Alltså blir redis inmatningen
    "rpush tobackend x" och 'x' är det jag skriver in? Så om jag skickar en integer TEX 1 så blir det "rpush tobackend 1"?

    2. Hur, rent praktiskt, skall min backend "avlyssna" redis? Är tanken att man via Rest skickar en begäran att
    backend skall lyssna och sedan har man en metod som avgör vilken typ av request/anrop man försöker göra FRÅN
    frontenden TILL backenden? Jag begriper att frontenden kan "lyssna" på redis en stund efter att det kastat iväg
    ett anrop men vad gör jag i BE?

    Det lättaste är väl om backend "läser" redis hela tiden från och med att man kör programmet... men hur skall man
    få det att fungera? Går det ens att göra så? (Jag menar, det klart det går men jag är förvirrad)

    3. Hur exakt kommer formatet se ut här? Alltså hur skall strängarna i listan "toBackend" i redis se ut/inehålla?
    Jag antar att man kan knuffa in inmatningar i två listor som helt enkelt
    heter "toBackend" och "toFrontend" men sedan då? Är tanken att man skickar ett CURL anrop mot backendens REST client?
    För det låter svindumt... Jag antar att man får bygga en metod i BE som läser listan "toBackend" i redis och sedan
    kan tyda vilken typ av payload som den tagit emot och till vilken metod den skall skickas... men detta är svårt
    att visualisera/hitta på, på helt egen hand när man är ovan med Quarkus, docker och redis.

    Det kan hända att jag kommer behöva en smula parkodning/handledning för att komma någon vart med detta
    */

    public static final Logger LOG = Logger.getLogger(EventBusListener.class);

    @Inject
    ReactiveRedisDataSource redis;

    ReactiveListCommands<String, String> lists = null;
    ReactiveKeyCommands<String> keys = null;

    @ConsumeEvent("toRedis")
    public void handleToRedis(Message<String> msg) {
        if (lists == null) {
            lists = redis.list(String.class);
        }
        if (keys == null) {
            keys = redis.key();
        }
        LOG.info("Got message with body " + msg.body() + " and replyAddress" + msg.replyAddress());
        final String redisPayload = msg.replyAddress(); // For now just send reply address
        // Clear replyAddress from redis in case we have old stuff there
        keys.del(redisPayload);

        LOG.info("Pushing payload to redis: " + redisPayload);
        lists.rpush("tobackend", redisPayload).subscribe().with(response -> {
            LOG.info("Got response from redis: " + response + ", starting response listener");
            lists.blpop(Duration.ofSeconds(10), redisPayload).subscribe().with(readresp -> {
                // For now cheat, should read msg replyAddress from readresp
                if (readresp != null) {
                    LOG.info("Read from list " + readresp.key + ", value " + readresp.value + " replying and removing message");
                    msg.reply(readresp.value);
                } else {
                    LOG.info("blpop from list " + redisPayload + " timed out!");
                    msg.reply("FAILED TO GET RESULT FROM REDIS!!!");
                }
            }, fail -> {
                LOG.info("blpop from list " + redisPayload + " failed!");
                msg.reply("FAILED TO GET RESULT FROM REDIS!!!");
            });
        });

    }
}
