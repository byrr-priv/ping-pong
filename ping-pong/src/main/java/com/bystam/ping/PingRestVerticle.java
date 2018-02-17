package com.bystam.ping;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class PingRestVerticle extends AbstractVerticle {

    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int BAD_REQUEST = 400;

    @Override
    public void start() throws Exception {
        super.start();

        Router router = createPingApiRouter();

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
    }

    private Router createPingApiRouter() {
        Router router = Router.router(vertx);

        router.get("/service").handler(this::listServices);

        router.post("/service")
                .handler(BodyHandler.create())
                .handler(this::insertService);

        router.delete("/service/:service_id").handler(this::deleteService);

        return router;
    }

    private void listServices(RoutingContext context) {
        vertx.eventBus().send("services.get_all", null, (Handler<AsyncResult<Message<JsonObject>>>) event -> {
            if (event.succeeded()) {
                context.response()
                        .putHeader("Content-Type", "application/json")
                        .setStatusCode(OK)
                        .end(event.result().body().toString());
            } else {
                context.response()
                        .setStatusCode(BAD_REQUEST)
                        .end();
            }
        });
    }

    private void insertService(RoutingContext context) {
        vertx.eventBus().send("services.insert", context.getBodyAsJson(), event -> {
            if (event.succeeded()) {
                context.response().setStatusCode(CREATED).end();
            } else {
                context.response().setStatusCode(BAD_REQUEST).end();
            }
        });
    }

    private void deleteService(RoutingContext context) {
        String serviceId = context.queryParams().get("service_id");
        vertx.eventBus().send("services.delete", serviceId, event -> {
            if (event.succeeded()) {
                context.response().setStatusCode(OK).end();
            } else {
                context.response().setStatusCode(BAD_REQUEST).end();
            }
        });
    }
}
