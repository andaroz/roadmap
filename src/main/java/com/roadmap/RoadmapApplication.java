package com.roadmap;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log
@SpringBootApplication
public class RoadmapApplication {
    public static void main(String[] args) {
        SpringApplication.run (RoadmapApplication.class, args);
    }
}