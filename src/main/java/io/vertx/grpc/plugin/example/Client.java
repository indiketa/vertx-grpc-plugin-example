package io.vertx.grpc.plugin.example;

import io.grpc.ManagedChannel;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.grpc.VertxChannelBuilder;
import io.vertx.grpc.plugin.example.ExampleServiceVertxGrpc.ExampleServiceVertxStub;

/**
 *
 * @author ecatala
 */
public class Client extends AbstractVerticle {

    private final String name = "Eduard";

    @Override
    public void start(Promise startFuture) throws Exception {
        // Create the channel
        ManagedChannel channel = VertxChannelBuilder
                .forAddress(vertx, "localhost", 8080)
                .usePlaintext(true)
                .build();

        // Create the stub
        ExampleServiceVertxStub stub = ExampleServiceVertxGrpc.newExampleServiceVertxStub(channel);

        // Execute example calls sequentially
        Future.succeededFuture()
                .compose(v -> oneToOne(stub))
                .compose(v -> oneToMany(stub))
                .compose(v -> manyToOne(stub))
                .compose(v -> manyToMany(stub))
                .compose(v -> {
                    startFuture.complete();
                    return Future.succeededFuture();
                });

    }

    private Future oneToOne(ExampleServiceVertxStub stub) {
        Promise p = Promise.promise();
        System.out.println("unaryUnary: Sent: " + name);
        stub.unaryUnary(buildRequest(name), ar -> {
            if (ar.succeeded()) {
                System.out.println("unaryUnary: Received: " + ar.result().getGreeting());
            }
            p.complete();
        });
        return p.future();
    }

    private Future oneToMany(ExampleServiceVertxStub stub) {
        Promise p = Promise.promise();
        System.out.println("unaryMany: Sent: " + name);
        stub.unaryMany(buildRequest(name), readStream -> {
            readStream.handler(er -> System.out.println("unaryMany: Received: " + er.getGreeting()))
                    .endHandler(v -> {
                        System.out.println("unaryMany: End of stream");
                        p.complete();
                    });
        });
        return p.future();
    }

    private Future manyToOne(ExampleServiceVertxStub stub) {
        Promise p = Promise.promise();
        System.out.println("manyUnary: Sent: " + name);
        stub.manyUnary(exchange -> {
            exchange.handler(ar -> {
                System.out.println("manyUnary: Received: " + ar.result().getGreeting());
                p.complete();
            });
            System.out.println("manyUnary: Sent: " + name + " 1");
            exchange.write(buildRequest(name + " 1"));
            System.out.println("manyUnary: Sent: " + name + " 2");
            exchange.write(buildRequest(name + " 2"));
            System.out.println("manyUnary: Sent: " + name + " 3");
            exchange.end(buildRequest(name + " 3"));
        });
        return p.future();
    }

    private Future manyToMany(ExampleServiceVertxStub stub) {
        Promise p = Promise.promise();
        stub.manyMany(bidiExchange -> {
            bidiExchange.handler(er -> System.out.println("manyMany: Received: " + er.getGreeting()))
                    .endHandler(v -> {
                        System.out.println("manyMany: End of response stream");
                        p.complete();
                    });

            System.out.println("manyMany: Sent: " + name + " 1");
            bidiExchange.write(buildRequest(name + " 1"));
            System.out.println("manyMany: Sent: " + name + " 2");
            bidiExchange.write(buildRequest(name + " 2"));
            System.out.println("manyMany: end of sending stream");
            bidiExchange.end();
        });
        return p.future();
    }

    private ExampleRequest buildRequest(String name) {
        return ExampleRequest.newBuilder().setName(name).build();
    }

}
