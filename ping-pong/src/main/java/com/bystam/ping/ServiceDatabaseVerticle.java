package com.bystam.ping;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ServiceDatabaseVerticle extends AbstractVerticle {

    private static final String SERVICES_FILE = "/tmp/.ping_services_db.json";

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        vertx.eventBus().consumer("services.get_all", this::getAllServices);
        vertx.eventBus().consumer("services.insert", this::insertService);
        vertx.eventBus().consumer("services.delete", this::deleteService);

        vertx.fileSystem().exists(SERVICES_FILE, event -> {
           if (!event.result()) {
               vertx.fileSystem().writeFile(SERVICES_FILE, new JsonArray().toBuffer(), event1 -> {
                  if (event1.succeeded()) {
                      startFuture.complete();
                   } else {
                      startFuture.fail(event1.cause());
                   }
               });
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

        JsonObject newService = message.body();
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
        //String serviceId = message.body();
        FileSystem fs = vertx.fileSystem();

        fs.readFile(SERVICES_FILE, event -> {
            if (event.succeeded()) {
                JsonArray services = event.result().toJsonArray();

                if (!services.isEmpty()) {
                    services.remove(0);
                }

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
}
