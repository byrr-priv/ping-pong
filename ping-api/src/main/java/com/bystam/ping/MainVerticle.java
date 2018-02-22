package com.bystam.ping;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.deployVerticle(ServiceDatabaseVerticle.class.getName());
        vertx.deployVerticle(RestApiVerticle.class.getName());
        vertx.deployVerticle(UrlPingerVerticle.class.getName());
    }
}
