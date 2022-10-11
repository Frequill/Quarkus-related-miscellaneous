package eu.flygare;

import javax.json.bind.annotation.JsonbProperty;

/**
 * Namngiven LoginEntity eftersom en entitet kan ha flera översättningar, 
 * JSON, XML, databas m.m.
 * @author julius
 */
public class LoginEntity {
    

    // Detta räcker för att jsonb ska kunna hantera den json du definierat
    @JsonbProperty("result")
    public String data;
    
    @JsonbProperty("username")
    public String user=null; // Ensure null if not set in incoming json
    
    @JsonbProperty("password")
    public String password=null;

    public LoginEntity(){
        data = "NO DATA";
    }

    public LoginEntity(String data){
        this.data = data;
    }

    public LoginEntity(String data, String username, String password){
        this.data = data;
        this.user = username;
        this.password = password;
    }


}
