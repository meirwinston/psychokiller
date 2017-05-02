package com.psychokiller.ws.beans;

public class SignupResponse {
    private long accountId;

    public SignupResponse(long accountId){
        this.accountId = accountId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
}
