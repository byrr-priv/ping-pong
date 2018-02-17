package com.bystam.ping;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.List;

public class PingRestVerticle extends AbstractVerticle {

    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int BAD_REQUEST = 400;

    private final List<Service> services = new ArrayList<>();

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
        JsonArray array = new JsonArray();
        services.stream()
                .map(JsonObject::mapFrom)
                .forEach(array::add);
        JsonObject body = new JsonObject();
        body.put("services", array);

        context.response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(OK)
                .end(body.toString());
    }

    private void insertService(RoutingContext context) {
        JsonObject body = context.getBodyAsJson();
        String url = body.getString("url");

        if (url == null) {
            context.response().setStatusCode(BAD_REQUEST).end();
        } else {
            services.add(new Service(url, 0));
            context.response().setStatusCode(CREATED).end();
        }
    }

    private void deleteService(RoutingContext context) {
        //String serviceId = context.queryParams().get("service_id");
        services.stream()
                .filter(s -> true) // TODO filter based on ID
                .findFirst()
                .ifPresent(services::remove);
        context.response().setStatusCode(OK).end();
    }
}
