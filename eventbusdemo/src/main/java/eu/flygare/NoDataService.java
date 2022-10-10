/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.flygare;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.MultiMap;
import io.vertx.mutiny.core.eventbus.Message;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author flax
 */
@ApplicationScoped
public class NoDataService {

    @ConsumeEvent("nodata")
    public void doNoData(Message<Object> msg) {
        if (msg.body() == null) {
            System.out.println("Got null data in body");

        }
        System.out.println("replyAddress: " + msg.replyAddress() + "; Address: " + msg.address() + "; isSend=" + msg.isSend());
        MultiMap mmap = msg.headers();
        Set<String> names = mmap.names();
        for (String name : names) {
            System.out.println("Header: " + name + "=" + mmap.get(name));
        }
        if ("true".equals(msg.headers().get("NORESPONSE"))) {
            System.out.println("Not sending reply!");
        } else {
            System.out.println("Sending NODATA1 back via reply");
            msg.reply("NODATA1");
        }
    }

    @ConsumeEvent("nodata")
    public String doNoData2(String data) {
        if (data == null) {
            System.out.println("Got null string as data");
        }
        return "NODATA2";
    }
}
