package com.candidate.othercandidateservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        exclude = {
                net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration.class
        }
)
public class OtherCandidateServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtherCandidateServiceApplication.class, args);
    }

}
