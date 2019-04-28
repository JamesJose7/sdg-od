package com.jeeps.ckan_extractor.model.stomp;

public class WebSocketResult {
    private String content;

    public WebSocketResult() {}

    public WebSocketResult(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
