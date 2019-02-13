package com.amused.joey.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Project: JoeyTools
 * Create : joey
 * Date   : 2019/01/31 09:33
 * Description:
 */
class MailAuthenticator extends Authenticator {
    private String userName;
    private String password;

    MailAuthenticator() { }

    MailAuthenticator(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}
