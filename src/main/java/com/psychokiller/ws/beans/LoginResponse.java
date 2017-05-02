package com.psychokiller.ws.beans;

public class LoginResponse {
    private String authToken;

    public LoginResponse(){

    }
    public LoginResponse(String token){
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
