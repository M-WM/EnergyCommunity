package at.fhtw.disys.percentageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "at.fhtw.disys")
public class PercentageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PercentageServiceApplication.class, args);
    }
}