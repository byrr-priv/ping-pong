package com.bystam.ping;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RestApiVerticle extends AbstractVerticle {

    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int BAD_REQUEST = 400;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd HH:mm");

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
        vertx.eventBus().send(ServiceDatabaseVerticle.GET_ALL, null, (Handler<AsyncResult<Message<JsonArray>>>) ar -> {
            if (ar.succeeded()) {

                // reformat timestamps in a readable time format
                JsonArray services = ar.result().body();
                services.forEach(o -> {
                    JsonObject service = (JsonObject)o;
                    Long time = service.getLong("lastChecked");
                    if (time != null) {
                        service.put("lastChecked", DATE_FORMAT.format(new Date(time)));
                    }
                });

                JsonObject body = new JsonObject()
                        .put("services", services);

                context.response()
                        .putHeader("Content-Type", "application/json")
                        .setStatusCode(OK)
                        .end(body.toString());
            } else {
                context.response()
                        .setStatusCode(BAD_REQUEST)
                        .end();
            }
        });
    }

    private void insertService(RoutingContext context) {
        JsonObject body = context.getBodyAsJson();
        String url = body.getString("url");
        String name = body.getString("name");

        if (url == null || name == null) {
            context.response().setStatusCode(BAD_REQUEST).end();
            return;
        }

        JsonObject message = new JsonObject()
                .put("url", url)
                .put("name", name);

        vertx.eventBus().send(ServiceDatabaseVerticle.INSERT, message, event -> {
            if (event.succeeded()) {
                context.response().setStatusCode(CREATED).end();
            } else {
                context.response().setStatusCode(BAD_REQUEST).end();
            }
        });
    }

    private void deleteService(RoutingContext context) {
        String serviceId = context.pathParam("service_id");
        vertx.eventBus().send(ServiceDatabaseVerticle.DELETE, serviceId, event -> {
            if (event.succeeded()) {
                context.response().setStatusCode(OK).end();
            } else {
                context.response().setStatusCode(BAD_REQUEST).end();
            }
        });
    }
}
