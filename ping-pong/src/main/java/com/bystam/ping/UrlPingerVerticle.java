package com.bystam.ping;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class UrlPingerVerticle extends AbstractVerticle {

    private static final long PERIOD = 60 * 1000; // 60 seconds

    private HttpClient client;

    @Override
    public void start() throws Exception {
        super.start();

        client = vertx.createHttpClient(new HttpClientOptions().setSsl(true));

        vertx.setPeriodic(PERIOD, this::pingAllServices);
    }

    private void pingAllServices(Long time) {
        fetchAllServices()
                .compose(services -> {

                    List<Future> pingFutures =
                            services.stream()
                            .map(this::createPingRequest)
                            .map(this::pingService)
                            .collect(Collectors.toList());

                    // pingFutures had to be type erased due to CompositeFuture.all
                    // here, we dig out the underlying PingResponse objects
                    return CompositeFuture.all(pingFutures).map(c -> pingFutures.stream()
                            .map(f -> (PingResponse)f.result())
                            .collect(Collectors.toList()));
                })
                .setHandler(responses -> {
                    // create a result message and send it to the database
                    JsonObject resultMessage = new JsonObject();
                    responses.result().stream()
                            .forEach(res -> {
                                resultMessage.put(res.serviceId, res.status);
                            });

                    vertx.eventBus().send(ServiceDatabaseVerticle.UPDATE_PING, resultMessage);
                });
    }

    private Future<JsonArray> fetchAllServices() {
        Future<Message<JsonArray>> fetchFuture = Future.future();
        vertx.eventBus().send(ServiceDatabaseVerticle.GET_ALL, null, fetchFuture.completer());
        return fetchFuture.map(Message::body);
    }

    private PingRequest createPingRequest(Object o) {
        JsonObject service = (JsonObject)o;
        String serviceId = service.getString("id");
        String url = service.getString("url");
        return new PingRequest(serviceId, url);
    }

    private Future<PingResponse> pingService(PingRequest request) {
        Future<PingResponse> pingFuture = Future.future();

        client.getAbs(request.url).handler(response -> {
            if (response.statusCode() == 200) {
                pingFuture.complete(new PingResponse(request.serviceId, "OK"));
            } else {
                pingFuture.complete(new PingResponse(request.serviceId, "FAIL"));
            }
        }).end();
        return pingFuture;
    }

    private static class PingRequest {
        final String serviceId;
        final String url;

        private PingRequest(String serviceId, String url) {
            this.serviceId = serviceId;
            this.url = url;
        }
    }

    private static class PingResponse {
        final String serviceId;
        final String status;

        private PingResponse(String serviceId, String status) {
            this.serviceId = serviceId;
            this.status = status;
        }
    }
}
