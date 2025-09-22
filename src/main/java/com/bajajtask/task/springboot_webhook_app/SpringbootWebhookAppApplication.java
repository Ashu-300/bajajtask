package com.bajajtask.task.springboot_webhook_app;

import com.bajajtask.task.springboot_webhook_app.service.WebhookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringbootWebhookAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootWebhookAppApplication.class, args);
    }

    @Bean
    CommandLineRunner run(WebhookService webhookService) {
        return args -> webhookService.executeWorkflow();
    }
}
