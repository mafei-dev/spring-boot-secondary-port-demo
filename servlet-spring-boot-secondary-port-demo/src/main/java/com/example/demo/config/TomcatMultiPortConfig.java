package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TomcatMultiPortConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private final SecondaryServerProperties secondaryServerProperties;

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        // The main Spring Boot port (default 8080 or server.port) is configured automatically.
        factory.addAdditionalTomcatConnectors(createSecondaryConnector());
    }

    private Connector createSecondaryConnector() {
        Connector connector = new Connector(Http11NioProtocol.class.getName());
        connector.setScheme("http");
        connector.setPort(this.secondaryServerProperties.getPort());
        connector.setProperty("address", this.secondaryServerProperties.getAddress().getHostName()); //
        if (connector.getProtocolHandler() instanceof Http11NioProtocol nioProtocol) {
            nioProtocol.setMaxThreads(this.secondaryServerProperties.getMaxThreads());
            nioProtocol.setMinSpareThreads(this.secondaryServerProperties.getMinSpareThreads());
        }
        return connector;
    }
}