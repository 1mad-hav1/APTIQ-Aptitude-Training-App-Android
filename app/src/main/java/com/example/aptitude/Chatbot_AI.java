// Chatbot_AI.java
package com.example.aptitude;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Chatbot_AI extends AppCompatActivity {

    private static final String API_KEY = "AIzaSyAIcGw0rEzWnz0IOo-5TZrkfFiPuD7_uKM";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
    private EditText messageInput;
    private ImageView sendButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot_ai);

        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Add initial chatbot greeting
        chatMessages.add(new ChatMessage("Chatbot", "Hello! I'm Nova, your Aptitude Training Assistant. How can I help you today?", false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);

        sendButton.setOnClickListener(v -> {
            String userMessage = messageInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                sendMessage(userMessage);
                messageInput.setText("");
            }
        });
    }

    private void sendMessage(String userMessage) {
        // Add user message to chat
        chatMessages.add(new ChatMessage("You", userMessage, true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);

        // Classify user input
        if (isGreeting(userMessage)) {
            respondToGreeting();
            return;
        }

        // Define system behavior
        String systemInstruction = "You are Nova, an aptitude training assistant. "
                + "Answer only aptitude-related questions (e.g., logical reasoning, "
                + "quantitative aptitude, and verbal ability). "
                + "If asked about anything else, reply with: 'Sorry, I can't assist with that. But I'm here to help with aptitude-related queries! Let me know if you have any questions on logical reasoning, quantitative aptitude, or verbal ability.'\n\n"
                + "User: " + userMessage + "\nNova:";

        // Use executor to send the request in background
        executor.execute(() -> {
            try {
                // Create URL and open connection
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create request JSON
                String requestJson = "{"
                        + "\"contents\": [{"
                        + "    \"parts\": [{\"text\": \"" + systemInstruction.replace("\"", "\\\"") + "\"}]"
                        + "}]"
                        + "}";

                // Write request body
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestJson.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Get response
                StringBuilder response = new StringBuilder();
                int responseCode = connection.getResponseCode();

                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new Exception("Server responded with code: " + responseCode);
                }

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                String aiResponse = jsonResponse
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");

                // Update UI on main thread
                runOnUiThread(() -> {
                    chatMessages.add(new ChatMessage("Chatbot", aiResponse, false));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                });

            } catch (Exception e) {
                // Handle errors gracefully
                runOnUiThread(() -> {
                    chatMessages.add(new ChatMessage("Chatbot", "Sorry, I can't process your request right now.", false));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                });
            }
        });
    }

    // Check if user input is a greeting
    private boolean isGreeting(String userMessage) {
        String lowerCaseMessage = userMessage.toLowerCase();

        // Check for common greetings
        boolean isBasicGreeting = lowerCaseMessage.contains("hello") || lowerCaseMessage.contains("hi")
                || lowerCaseMessage.contains("hey") || lowerCaseMessage.contains("good morning")
                || lowerCaseMessage.contains("good evening") || lowerCaseMessage.contains("good night");

        // Check if greeting contains any name other than Nova
        if (isBasicGreeting) {
            if (lowerCaseMessage.contains("nova")) {
                return true;
            } else if (lowerCaseMessage.matches(".*\\b(?!nova\\b)[a-zA-Z]+\\b.*")) {
                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                return false;
            }
            return true;
        }
        return false;
    }

    // Respond to greetings
    private void respondToGreeting() {
        String greetingResponse = "Hello! How can I assist you with aptitude training today?";
        chatMessages.add(new ChatMessage("Chatbot", greetingResponse, false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    public static class ChatMessage {
        private final String sender;
        private final String message;
        private final boolean isUser;

        public ChatMessage(String sender, String message, boolean isUser) {
            this.sender = sender;
            this.message = message;
            this.isUser = isUser;
        }

        public String getSender() { return sender; }
        public String getMessage() { return message; }
        public boolean isUser() { return isUser; }
    }
}
