package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
@ConfigurationProperties("server.secondary")
@Getter
@Setter
public class SecondaryServerProperties {
    private InetAddress address;
    private int port;
    private int maxThreads = 100;
    private int minSpareThreads = 10;
}
