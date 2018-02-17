package com.bystam.ping;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.deployVerticle(PingRestVerticle.class.getName());
    }
}
