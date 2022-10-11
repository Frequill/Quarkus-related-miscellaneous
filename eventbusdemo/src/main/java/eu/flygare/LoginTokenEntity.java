package eu.flygare;

import java.time.LocalTime;
import javax.json.bind.annotation.JsonbProperty;

public class LoginTokenEntity {

    // Resulting json should be {"token":"julius:876876876876876"}
    @JsonbProperty("token")
    public String tokendata;

    public LoginTokenEntity() {
        tokendata = "??User unset??";
    }

    public LoginTokenEntity(String user) {
        Long timestamp = System.currentTimeMillis();
        if (user == null || user.isBlank()) {
            tokendata = "%INVALID%";
        } else {
            tokendata = user + ":" + timestamp;
        }
        // Osäker om jag specat timestamp+namn, i så fall tokendata = "" + timestamp + user, så typningen blir rätt
        // Var inte fel att använda localtime, men timestamp brukar vara (milli)sekunder inom epoch och då funkar System.currentTimeMillis() bra
    }

}
