package com.hostel.accommodation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        exclude = {
                net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration.class
        }
)
public class AccommodationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccommodationServiceApplication.class, args);
    }

}
