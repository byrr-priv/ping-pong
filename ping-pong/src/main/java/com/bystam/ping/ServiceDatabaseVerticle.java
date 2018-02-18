package com.bystam.ping;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class ServiceDatabaseVerticle extends AbstractVerticle {

    public static final String GET_ALL = "services.get_all";
    public static final String INSERT = "services.insert";
    public static final String DELETE = "services.delete";
    public static final String UPDATE_PING = "services.update_ping";

    private static final String SERVICES_FILE = "/tmp/.ping_services_db.json";

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        vertx.eventBus().consumer(GET_ALL, this::getAllServices);
        vertx.eventBus().consumer(INSERT, this::insertService);
        vertx.eventBus().consumer(DELETE, this::deleteService);
        vertx.eventBus().consumer(UPDATE_PING, this::updatePing);

        vertx.fileSystem().exists(SERVICES_FILE, event -> {
           if (!event.result()) {
               vertx.fileSystem().writeFile(SERVICES_FILE, new JsonArray().toBuffer(), startFuture.completer());
           } else {
               startFuture.complete();
           }
        });
    }

    private void getAllServices(Message<Void> message) {
        readServices().setHandler(ar -> {
            if (ar.succeeded()) {
                message.reply(ar.result());
            } else {
                message.fail(-1000, ar.cause().getMessage());
            }
        });
    }

    private void insertService(Message<JsonObject> message) {
        JsonObject newService = message.body()
                .put("id", UUID.randomUUID().toString());

        readServices()
                .compose(services -> {
                    services.add(newService);
                    return writeServices(services);
                })
                .setHandler(ar -> {
                    if (ar.succeeded()) {
                        message.reply("success");
                    } else {
                        message.fail(-1000, ar.cause().getMessage());
                    }
                });
    }

    private void deleteService(Message<String> message) {
        String serviceId = message.body();

        readServices()
                .compose(services -> {
                    JsonArray updated = new JsonArray();
                    services.stream()
                            .map(o -> (JsonObject)o)
                            .filter(o -> !o.getString("id").equals(serviceId))
                            .forEach(updated::add);
                    return writeServices(updated);
                })
                .setHandler(ar -> {
                    if (ar.succeeded()) {
                        message.reply("success");
                    } else {
                        message.fail(-1000, ar.cause().getMessage());
                    }
                });
    }

    private void updatePing(Message<JsonObject> message) {
        long time = System.currentTimeMillis();
        JsonObject pingResults = message.body();

        readServices()
                .compose(services -> {

                    // set ping result state for each service
                    services.stream()
                            .forEach(o -> {
                        JsonObject service = (JsonObject)o;
                        String serviceId = service.getString("id");

                        service
                                .put("status", pingResults.getString(serviceId))
                                .put("lastChecked", time);
                    });

                    return writeServices(services);
                });
    }

    private Future<JsonArray> readServices() {
        Future<JsonArray> future = Future.future();
        vertx.fileSystem().readFile(SERVICES_FILE, ar -> {
           if (ar.succeeded()) {
               future.complete(ar.result().toJsonArray());
           } else {
               future.fail(ar.cause());
           }
        });
        return future;
    }

    private Future<Void> writeServices(JsonArray services) {
        Future<Void> future = Future.future();
        vertx.fileSystem().writeFile(SERVICES_FILE, services.toBuffer(), ar -> {
            if (ar.succeeded()) {
                future.complete();
            } else {
                future.fail(ar.cause());
            }
        });
        return future;
    }
}
