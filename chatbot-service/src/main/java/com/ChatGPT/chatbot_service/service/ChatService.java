package com.ChatGPT.chatbot_service.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ChatGPT.chatbot_service.model.ChatRequest;
import com.ChatGPT.chatbot_service.model.ChatResponse;

@Service
public class ChatService {

    private final String RASA_URL = "http://localhost:5005/webhooks/rest/webhook";

    public ChatResponse getChatbotReply(ChatRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> payload = new HashMap<>();
        payload.put("sender", "user");
        payload.put("message", request.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        String botReply = "Sorry, I didn't get that.";

        try {
            ResponseEntity<Map[]> response = restTemplate.postForEntity(RASA_URL, entity, Map[].class);

            if (response.getBody() != null && response.getBody().length > 0) {
                Map firstResponse = response.getBody()[0];
                botReply = (String) firstResponse.get("text");
            }
        } catch (Exception e) {
            System.err.println("Error communicating with Rasa: " + e.getMessage());
        }

        return new ChatResponse(botReply);
    }
}
