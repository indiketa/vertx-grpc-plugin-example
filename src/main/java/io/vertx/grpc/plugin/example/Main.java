package io.vertx.grpc.plugin.example;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 *
 * @author ecatala
 */
public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx
                .deployVerticle(new Server())
                .compose(s -> vertx.deployVerticle(new Client()))
                .compose(s -> {
                    System.out.println("Main: Reached end of example!");
                    System.exit(0);
                    return Future.succeededFuture();
                });
    }

}
