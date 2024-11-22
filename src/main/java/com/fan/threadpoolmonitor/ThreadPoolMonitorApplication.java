package com.fan.threadpoolmonitor;


import com.fan.threadpoolmonitor.service.ThreadPoolConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(ThreadPoolConfigurationProperties.class)
@SpringBootApplication
public class ThreadPoolMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThreadPoolMonitorApplication.class, args);
    }

}
