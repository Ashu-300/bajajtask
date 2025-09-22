package com.bajajtask.task.springboot_webhook_app.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate;

    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate; // Use injected RestTemplate bean
    }

    @EventListener(ApplicationReadyEvent.class)
    public void executeWorkflow() {
        // 1️⃣ Generate webhook
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> body = Map.of(
                "name", "John Doe",
                "regNo", "REG12347",
                "email", "john@example.com"
        );

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, body, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String webhook = (String) response.getBody().get("webhook");
                String accessToken = (String) response.getBody().get("accessToken");

                // ✅ Do NOT validate JWT locally; just use as-is
                System.out.println("Received webhook: " + webhook);

                // 2️⃣ Solve SQL problem
                String finalQuery = solveSQLProblem();

                // 3️⃣ Submit solution
                submitSolution(webhook, accessToken, finalQuery);

            } else {
                System.err.println("Failed to generate webhook: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error executing workflow: " + e.getMessage());
        }
    }

    private String solveSQLProblem() {
        return """
            SELECT p.amount AS SALARY,
                   CONCAT(e.first_name, ' ', e.last_name) AS NAME,
                   FLOOR(DATEDIFF(CURRENT_DATE, e.dob)/365) AS AGE,
                   d.department_name AS DEPARTMENT_NAME
            FROM payments p
            JOIN employee e ON p.emp_id = e.emp_id
            JOIN department d ON e.department = d.department_id
            WHERE DAY(p.payment_time) <> 1
              AND p.amount = (
                  SELECT MAX(amount)
                  FROM payments
                  WHERE DAY(payment_time) <> 1
              );
            """;
    }

    private void submitSolution(String webhook, String accessToken, String finalQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ Send token as received
        headers.setBearerAuth(accessToken);

        Map<String, String> body = Map.of("finalQuery", finalQuery);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(webhook, request, String.class);
            System.out.println("Submission Response: " + response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error submitting solution: " + e.getMessage());
        }
    } 
}
