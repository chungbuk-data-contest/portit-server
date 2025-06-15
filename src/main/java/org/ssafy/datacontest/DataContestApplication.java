package org.ssafy.datacontest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class DataContestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataContestApplication.class, args);
    }

}
