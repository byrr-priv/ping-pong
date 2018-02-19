package com.bystam.ping;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(VertxUnitRunner.class)
public class RestApiVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(RestApiVerticle.class.getName(),
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }


    @Test
    public void testInsertService(TestContext context) {
//        final Async async = context.async(2);
//
//        WebClient client = WebClient.create(vertx);
//
//        String pingUrl = "https://www.google.com/";
//        JsonObject postBody = new JsonObject()
//                .put("url", pingUrl);
//
//        client.post(8080, "localhost", "/service")
//                .sendJson(postBody, event -> {
//                    assertEquals(201, event.result().statusCode());
//                    async.countDown();
//                });
//
//        client.get(8080, "localhost", "/service")
//                .as(BodyCodec.jsonObject())
//                .send(event -> {
//
//                    JsonObject root = event.result().body();
//
//                    JsonArray services = root.getJsonArray("services");
//                    JsonObject service = services.getJsonObject(0);
//
//                    assertEquals(services.size(), 1);
//                    assertEquals(pingUrl, service.getString("url"));
//
//                    async.countDown();
//                });
    }
}