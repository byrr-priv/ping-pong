package com.bystam.ping;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ServiceDatabaseVerticle extends AbstractVerticle {

    private static final String SERVICES_FILE = "services_db.json";

    private final List<Service> services = new ArrayList<>();

    @Override
    public void start() throws Exception {
        super.start();

        vertx.eventBus().consumer("services.get_all", this::getAllServices);
        vertx.eventBus().consumer("services.insert", this::insertService);
        vertx.eventBus().consumer("services.delete", this::deleteService);
    }

    private void getAllServices(Message<Void> message) {
        JsonArray array = new JsonArray();
        services.stream()
                .map(JsonObject::mapFrom)
                .forEach(array::add);
        JsonObject body = new JsonObject()
                .put("services", array);

        message.reply(body);
    }

    private void insertService(Message<JsonObject> message) {
        String url = message.body().getString("url");
        if (url == null) {
            message.fail(-1, "Malformed data");
        } else {
            services.add(new Service(url, 0));
            message.reply("success");
        }
    }

    private void deleteService(Message<String> message) {
        //String serviceId = message.body();
        services.stream()
                .filter(s -> true) // TODO filter based on ID
                .findFirst()
                .ifPresent(services::remove);
        message.reply("success");
    }
}
