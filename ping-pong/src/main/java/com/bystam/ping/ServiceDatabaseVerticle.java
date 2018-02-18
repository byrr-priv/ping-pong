package com.bystam.ping;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class ServiceDatabaseVerticle extends AbstractVerticle {

    public static final String GET_ALL = "services.get_all";
    public static final String INSERT = "services.insert";
    public static final String DELETE = "services.delete";

    private static final String SERVICES_FILE = "/tmp/.ping_services_db.json";

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        vertx.eventBus().consumer(GET_ALL, this::getAllServices);
        vertx.eventBus().consumer(INSERT, this::insertService);
        vertx.eventBus().consumer(DELETE, this::deleteService);

        vertx.fileSystem().exists(SERVICES_FILE, event -> {
           if (!event.result()) {
               vertx.fileSystem().writeFile(SERVICES_FILE, new JsonArray().toBuffer(), startFuture.completer());
           } else {
               startFuture.complete();
           }
        });
    }

    private void getAllServices(Message<Void> message) {
        vertx.fileSystem().readFile(SERVICES_FILE, event -> {
            if (event.succeeded()) {
                message.reply(event.result().toJsonArray());
            } else {
                message.fail(404, "file missing");
            }
        });
    }

    private void insertService(Message<JsonObject> message) {
        JsonObject newService = message.body()
                .put("id", UUID.randomUUID().toString());

        FileSystem fs = vertx.fileSystem();

        fs.readFile(SERVICES_FILE, event -> {
            if (event.succeeded()) {
                JsonArray services = event.result().toJsonArray();
                services.add(newService);

                fs.writeFile(SERVICES_FILE, services.toBuffer(), event1 -> {
                    if (event1.succeeded()) {
                        message.reply("success");
                    } else {
                        message.fail(404, "file missing");
                    }
                });
            } else {
                message.fail(404, "file missing");
            }
        });
    }

    private void deleteService(Message<String> message) {
        String serviceId = message.body();
        FileSystem fs = vertx.fileSystem();

        fs.readFile(SERVICES_FILE, event -> {
            if (event.succeeded()) {

                JsonArray updated = new JsonArray();
                event.result().toJsonArray().stream()
                        .map(o -> (JsonObject)o)
                        .filter(o -> !o.getString("id").equals(serviceId))
                        .forEach(updated::add);

                fs.writeFile(SERVICES_FILE, updated.toBuffer(), event1 -> {
                    if (event1.succeeded()) {
                        message.reply("success");
                    } else {
                        message.fail(404, "file missing");
                    }
                });
            } else {
                message.fail(404, "file missing");
            }
        });
    }
}
