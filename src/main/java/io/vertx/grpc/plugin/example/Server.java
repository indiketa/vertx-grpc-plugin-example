package io.vertx.grpc.plugin.example;

import com.google.common.base.Joiner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.grpc.GrpcBidiExchange;
import io.vertx.grpc.GrpcReadStream;
import io.vertx.grpc.GrpcWriteStream;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import io.vertx.grpc.plugin.example.ExampleServiceVertxGrpc.ExampleServiceVertxImplBase;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ecatala
 */
public class Server extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startFuture) throws Exception {
        // The service
        ExampleServiceVertxImplBase service = new ExampleServiceVertxImplBase() {
            @Override
            public void manyMany(GrpcBidiExchange<ExampleRequest, ExampleResponse> exchange) {
                exchange
                        .handler(req -> {
                            exchange.write(buildGreeting(req.getName()));
                        })
                        .endHandler(v -> {
                            exchange.end();
                        });

            }

            @Override
            public void manyUnary(GrpcReadStream<ExampleRequest> request, Promise<ExampleResponse> response) {
                List<String> names = new LinkedList<>();
                request
                        .handler(req -> {
                            names.add(req.getName());
                        })
                        .endHandler(v -> {
                            response.complete(buildGreeting(Joiner.on(", ").join(names)));
                        });
            }

            @Override
            public void unaryMany(ExampleRequest request, GrpcWriteStream<ExampleResponse> response) {
                response.write(buildGreeting(request.getName() + " 1"))
                        .compose(v -> response.write(buildGreeting(request.getName() + " 2")))
                        .compose(v -> response.end());
            }

            @Override
            public void unaryUnary(ExampleRequest request, Promise<ExampleResponse> response) {
                response.complete(buildGreeting(request.getName()));
            }

            private ExampleResponse buildGreeting(String name) {
                return ExampleResponse.newBuilder().setGreeting("Hi, " + name + "!").build();
            }

        };

        VertxServer rpcServer = VertxServerBuilder
                .forAddress(vertx, "localhost", 8080)
                .addService(service)
                .build();

        rpcServer.start(startFuture);
    }

}
