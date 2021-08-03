package com.crazymakercircle.imClient.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
@PropertySource("classpath:application.properties")
public class SystemConfig {

    // 服务器ip地址
    @Value("${chat.server.ip}")
    private String host;
    // 服务器端口
    @Value("${chat.server.port}")
    private int port;


    @Bean
    public static PropertySourcesPlaceholderConfigurer    propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
