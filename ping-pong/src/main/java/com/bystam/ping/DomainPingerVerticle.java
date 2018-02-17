package com.bystam.ping;

import io.vertx.core.AbstractVerticle;

public class DomainPingerVerticle extends AbstractVerticle {

    private static final long PERIOD = 60 * 1000; // 60 seconds

    @Override
    public void start() throws Exception {
        super.start();

        vertx.setPeriodic(PERIOD, this::pingAllServices);
    }

    private void pingAllServices(Long time) {

    }
}
