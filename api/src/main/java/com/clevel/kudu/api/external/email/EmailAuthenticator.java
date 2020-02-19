package com.clevel.kudu.api.external.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class EmailAuthenticator extends Authenticator {
    private String userName;
    private String password;

    public EmailAuthenticator() {
        super();
    }

    public EmailAuthenticator(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }

}
