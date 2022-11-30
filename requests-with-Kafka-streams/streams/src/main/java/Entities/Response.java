package Entities;

import javax.json.bind.annotation.JsonbProperty;

public class Response {

    @JsonbProperty("responseId")
    private String responseId;
    @JsonbProperty("accountExists")
    private boolean accountExists;
    @JsonbProperty("loginSuccessful")
    private boolean loginSuccessful;

    public Response(String responseId, boolean accountExists, boolean loginSuccessful) {
        this.responseId = responseId;
        this.accountExists = accountExists;
        this.loginSuccessful = loginSuccessful;
    }

    public Response() {
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public boolean isAccountExists() {
        return accountExists;
    }

    public void setAccountExists(boolean accountExists) {
        this.accountExists = accountExists;
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public void setLoginSuccessful(boolean loginSuccessful) {
        this.loginSuccessful = loginSuccessful;
    }
}