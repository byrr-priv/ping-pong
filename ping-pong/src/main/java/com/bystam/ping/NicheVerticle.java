package com.bystam.ping;

import io.vertx.core.AbstractVerticle;

public class NicheVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        super.start();

        vertx.createHttpServer().requestHandler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("This is a niche server running at the same time!");
        }).listen(7070);
    }
}
