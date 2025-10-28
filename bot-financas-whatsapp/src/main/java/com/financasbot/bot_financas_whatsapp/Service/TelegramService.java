package com.financasbot.bot_financas_whatsapp.Service;

import com.financasbot.bot_financas_whatsapp.Service.FinancasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TelegramService {

    @Value("${telegram.bot-token}")
    private String botToken;

    @Autowired
    private FinancasService financasService;

    private final String TELEGRAM_API = "https://api.telegram.org/bot";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long lastUpdateId = 0L;

    // Método que busca novas mensagens a cada 2 segundos
    @Scheduled(fixedRate = 2000)
    public void checkForNewMessages() {
        try {
            String url = TELEGRAM_API + botToken + "/getUpdates?offset=" + (lastUpdateId + 1) + "&timeout=10";

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode updates = objectMapper.readTree(response.getBody());

                if (updates.get("ok").asBoolean()) {
                    for (JsonNode update : updates.get("result")) {
                        processUpdate(update);
                        lastUpdateId = update.get("update_id").asLong();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar mensagens: " + e.getMessage());
        }
    }

    // Processa uma mensagem individual
    private void processUpdate(JsonNode update) {
        try {
            if (update.has("message") && update.get("message").has("text")) {
                String messageText = update.get("message").get("text").asText();
                Long chatId = update.get("message").get("chat").get("id").asLong();
                String userName = update.get("message").get("chat").get("first_name").asText();

                System.out.println("📩 Mensagem de " + userName + " (" + chatId + "): " + messageText);

                // Usa o chatId como "telefone" para identificar o usuário
                String telefone = "telegram_" + chatId;

                // Processa a mensagem
                String response = financasService.processMessage(messageText, telefone);

                sendMessage(chatId, response);
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao processar mensagem: " + e.getMessage());
        }
    }

    // Envia mensagem para o usuário
    public void sendMessage(Long chatId, String text) {
        try {
            String url = TELEGRAM_API + botToken + "/sendMessage";

            String requestBody = String.format(
                    "{\"chat_id\": %d, \"text\": \"%s\", \"parse_mode\": \"Markdown\"}",
                    chatId, escapeJsonString(text)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            System.out.println("✅ Mensagem enviada para chat " + chatId);

        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar mensagem Telegram: " + e.getMessage());
        }
    }

    private String escapeJsonString(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Método para deletar webhook se existir (opcional)
    public void deleteWebhook() {
        try {
            String url = TELEGRAM_API + botToken + "/deleteWebhook";
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("🔧 Webhook removido: " + response);
        } catch (Exception e) {
            System.err.println("❌ Erro ao remover webhook: " + e.getMessage());
        }
    }
}