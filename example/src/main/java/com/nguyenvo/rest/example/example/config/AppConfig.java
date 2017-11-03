package com.nguyenvo.rest.example.example.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ComponentScan(basePackages = "${spring.componentScan.basePackages}")
public class AppConfig {

}
