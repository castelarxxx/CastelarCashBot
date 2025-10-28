package com.financasbot.bot_financas_whatsapp.Controller;

import com.financasbot.bot_financas_whatsapp.Service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/telegram")
public class TelegramController {

    @Autowired
    private TelegramService telegramService;

    // Endpoint para testar envio de mensagem manualmente
    @PostMapping("/send-test")
    public String sendTestMessage(@RequestParam Long chatId, @RequestParam String message) {
        telegramService.sendMessage(chatId, message);
        return "Mensagem enviada para chat: " + chatId;
    }

    // Endpoint para verificar se está funcionando
    @GetMapping("/status")
    public String status() {
        return "✅ Bot Telegram está rodando com Polling!";
    }

    // Endpoint para limpar webhook se existir
    @GetMapping("/cleanup")
    public String cleanup() {
        telegramService.deleteWebhook();
        return "Webhook removido! Agora usando polling.";
    }
}