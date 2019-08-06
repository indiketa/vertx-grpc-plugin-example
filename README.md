# vertx-grpc-plugin-example

Overview
========
This project is an example on how to use
https://github.com/indiketa/vertx-grpc-plugin

### vertx-grpc-plugin
vertx-grpc-plugin offers a replacement for 
https://github.com/vert-x3/vertx-grpc-java-compiler

It generates the `[name]VertxStub` and `[name]VertxImplBase` classes compatible with `4.0.0-milestone1`

Installation
============
### Maven
`vertx-grpc-plugin` is not yet published (waiting directions/recommendations from vertx team).

Clone the plugin [vertx-grpc-plugin](https://github.com/indiketa/vertx-grpc-plugin) and install locally. 

```
  mvn clean install
```

Usage
=====
After installing the plugin, add a [custom protoc plugin configuration section](https://www.xolstice.org/protobuf-maven-plugin/examples/protoc-plugin.html) into your `protobuf-maven-plugin`.

```xml
<protocPlugins>
    <protocPlugin>
        <id>vertxgrpc</id>
        <groupId>io.vertx</groupId>
        <artifactId>vertx-grpc-plugin</artifactId>
        <version>0.1-SNAPSHOT</version>
        <mainClass>io.vertx.grpc.plugin.Generator</mainClass>
    </protocPlugin>
</protocPlugins>
```
This will use the standard grpc-java generator and then call the vertx-grpc-plugin to generate vertx service stubs and base implementation along with your gRPC service stubs.
  
* To implement a service using an VertxGrpc service subclass `[Name]VertxGrpc.[Name]VertxImplBase` and override the   methods as shown in this example repository in `Client.java` and `Server.java` classes.
