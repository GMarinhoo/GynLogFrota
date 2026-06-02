package br.com.gynlog.app;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "br.com.gynlog")
public class GynLogApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(GynLogApp.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}