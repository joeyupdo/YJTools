package com.amused.joey.email;

class EmailContent {
    public final boolean isHtml;
    public final String content;

    EmailContent(boolean isHtml, String content) {
        this.isHtml = isHtml;
        this.content = content;
    }
}
