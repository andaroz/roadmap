package com.roadmap.config;

import com.roadmap.models.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@Component
@PropertySource("classpath:application.properties")
public class Config {
    @Bean
    public Order order(){
        return new Order ();
    }

}
