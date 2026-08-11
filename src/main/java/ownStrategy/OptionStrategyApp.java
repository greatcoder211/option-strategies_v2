package ownStrategy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OptionStrategyApp {

    public static void main(String[] args) {
        SpringApplication.run(OptionStrategyApp.class, args);
    }
}