package com.example.WebhookSender;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WebhookSenderApplication {

	private static final String WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

	public static void main(String[] args) {
		SpringApplication.run(WebhookSenderApplication.class, args);
	}

	@Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

	@Bean
    public CommandLineRunner run(RestTemplate restTemplate) {
        return args -> {
            System.out.println("Application starting up. Sending POST request to webhook URL...");

            // 1. Set up the request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. Create the request body as a Map
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "John Doe");
            requestBody.put("regNo", "REG12347");
            requestBody.put("email", "john@example.com");

            // 3. Create the HttpEntity, which combines headers and the body
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            try {
                // 4. Send the POST request and get the response
                ResponseEntity<String> response = restTemplate.postForEntity(WEBHOOK_URL, request, String.class);

                // 5. Log the response details to the console
                System.out.println("Webhook request sent successfully!");
                System.out.println("HTTP Status Code: " + response.getStatusCode());
                System.out.println("Response Body:");
                System.out.println(response.getBody());

            } catch (Exception e) {
                // 6. Catch any errors during the request and print them
                System.err.println("Failed to send webhook request. Error details:");
                e.printStackTrace();
            }
        };
    }

}
