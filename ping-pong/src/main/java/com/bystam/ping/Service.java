package com.bystam.ping;

public class Service {

    private final String url;
    private final long lastCheckedTimestamp;

    public Service(String url, long lastCheckedTimestamp) {
        this.url = url;
        this.lastCheckedTimestamp = lastCheckedTimestamp;
    }

    public String getUrl() {
        return url;
    }

    public long getLastCheckedTimestamp() {
        return lastCheckedTimestamp;
    }
}
