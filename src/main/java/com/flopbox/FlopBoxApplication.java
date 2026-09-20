package com.flopbox;

import com.flopbox.config.FtpProperties;
import com.flopbox.config.JwtProperties;
import com.flopbox.config.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = {
        JwtProperties.class, SecurityProperties.class, FtpProperties.class
})
public class FlopBoxApplication {

    static void main(String[] args) {
        SpringApplication.run(FlopBoxApplication.class, args);
    }
}
