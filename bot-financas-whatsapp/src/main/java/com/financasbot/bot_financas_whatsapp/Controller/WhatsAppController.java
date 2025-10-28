package com.financasbot.bot_financas_whatsapp.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WhatsAppController {

    // ✅ Health Check - TESTE PRIMEIRO
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("✅ Bot de Finanças WhatsApp está RODANDO! - " + java.time.LocalDateTime.now());
    }

    @GetMapping("/test-db")
    public ResponseEntity<String> testDatabase() {
        try {
            return ResponseEntity.ok("✅ Banco de dados conectado! - " + java.time.LocalDateTime.now());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Erro no banco: " + e.getMessage());
        }
    }

    // ✅ Webhook do WhatsApp
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(HttpServletRequest request) {
        try {
            String mensagem = request.getParameter("Body");
            String telefone = request.getParameter("From");

            log.info("📱 Mensagem recebida de {}: {}", telefone, mensagem);

            if (mensagem == null || telefone == null) {
                return ResponseEntity.badRequest().body("Parâmetros faltando");
            }

            // Resposta simples para teste
            String resposta = processarMensagemSimples(mensagem);

            // Formato Twilio
            String xmlResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<Response>" +
                    "<Message>" + resposta + "</Message>" +
                    "</Response>";

            return ResponseEntity.ok()
                    .header("Content-Type", "application/xml")
                    .body(xmlResponse);

        } catch (Exception e) {
            log.error("Erro no webhook", e);
            return ResponseEntity.status(500).body("Erro interno");
        }
    }

    // ✅ Processador simples de mensagens
    private String processarMensagemSimples(String mensagem) {
        if (mensagem == null) return "❌ Mensagem vazia";

        String msg = mensagem.toLowerCase().trim();

        switch (msg) {
            case "ajuda":
                return getMenuAjuda();

            case "extrato":
                return "📊 Extrato:\n- Entrada: R$ 1500,00\n- Saída: R$ 350,00\n💰 Saldo: R$ 1150,00";

            case "saldo":
                return "💰 Saldo atual: R$ 1150,00";

            default:
                if (msg.startsWith("entrada") || msg.startsWith("saida")) {
                    return "✅ Transação registrada com sucesso!";
                }
                return "🤖 Comando não reconhecido. Digite 'ajuda' para ver opções.";
        }
    }

    private String getMenuAjuda() {
        return """
               💼 *BOT FINANÇAS PESSOAIS* 💼
               
               📥 Registrar entrada:
               "entrada [valor] [categoria] [descrição]"
               
               📤 Registrar saída:
               "saida [valor] [categoria] [descrição]"
               
               📊 Ver extrato: "extrato"
               💰 Ver saldo: "saldo"
               
               💡 Exemplos:
               entrada 1500 salario pagamento
               saida 350 mercado compra
               """;
    }

    // ✅ Endpoint simples para teste
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("🏠 Bot Finanças WhatsApp - Use /health para testar");
    }
}