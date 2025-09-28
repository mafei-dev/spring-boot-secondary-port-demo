package com.example.reactivespringbootsecondaryportdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.netty.resources.LoopResources;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Slf4j
@Configuration
public class NettyMultiPortConfig {
    @Bean
    public SecondaryServer secondaryServer() {
        return new SecondaryServer();
    }

    public static class SecondaryServer implements SmartLifecycle {

        private WebServer webServer;
        private boolean running = false;

        @Override
        public void start() {
            NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
            factory.addServerCustomizers(httpServer -> {
                LoopResources loopResources = LoopResources.create("custom-netty-loop",
                                                                   4,
                                                                   true);
                return httpServer.runOn(loopResources)
                                 .port(8585);
            });
            HttpHandler handler = RouterFunctions.toHttpHandler(RouterFunctions.route(GET("/starter/hello"),
                                                                                      req -> {
                                                                                          log.info("Received request on secondary server");
                                                                                          try {
                                                                                              Thread.sleep(20000);
                                                                                          } catch(InterruptedException e) {
                                                                                              throw new RuntimeException(e);
                                                                                          }
                                                                                          return ServerResponse.ok()
                                                                                                               .body(Mono.just("Hello from secondary server!"),
                                                                                                                     String.class);
                                                                                      }));

            this.webServer = factory.getWebServer(handler);
            this.webServer.start();
            this.running = true;
        }

        @Override
        public void stop() {
            System.out.println("Stopping secondary server...");
            if (this.webServer != null) {
                System.out.println("Secondary server is stopping...");
                this.webServer.stop();
            }
            this.running = false;
        }

        @Override
        public boolean isRunning() {
            return running;
        }
    }
}