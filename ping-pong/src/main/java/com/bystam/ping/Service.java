package com.bystam.ping;

import io.vertx.core.json.JsonObject;

public class Service {

    private final String url;

    public Service(String url) {
        this.url = url;
    }

    public Service(JsonObject json) {
        this.url = json.getString("url");
    }

    public String getUrl() {
        return url;
    }

}
