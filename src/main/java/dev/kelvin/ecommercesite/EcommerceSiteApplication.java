package dev.kelvin.ecommercesite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "dev.kelvin.ecommercesite.model")
@EnableJpaRepositories(basePackages = "dev.kelvin.ecommercesite.repositories")
public class EcommerceSiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceSiteApplication.class, args);
    }

}
