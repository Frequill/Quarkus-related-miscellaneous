package RequestEntities;

import javax.json.bind.annotation.JsonbProperty;

public class LoginRequest{

    @JsonbProperty("requestId")
    private String requestId;
    @JsonbProperty("username")
    private String username;
    @JsonbProperty("password")
    private String password;


    public LoginRequest(String requestId, String username, String password) {
        this.requestId = requestId;
        this.username = username;
        this.password = password;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
