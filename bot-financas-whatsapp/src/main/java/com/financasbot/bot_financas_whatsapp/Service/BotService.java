package com.financasbot.bot_financas_whatsapp.Service;


import com.financasbot.bot_financas_whatsapp.Model.Transacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotService {

    private final TransacaoService transacaoService;
    private final PlanilhaService planilhaService;

    public String processarMensagem(String mensagem, String telefone) {
        try {
            String[] partes = mensagem.toLowerCase().split(" ");
            if (partes.length == 0) {
                return getMenuAjuda();
            }

            String comando = partes[0];

            switch (comando) {
                case "ajuda":
                    return getMenuAjuda();

                case "entrada":
                case "saida":
                    return registrarTransacao(partes, telefone, comando);

                case "extrato":
                    return gerarExtrato(telefone);

                case "saldo":
                    return obterSaldo(telefone);

                case "planilha":
                    return "📈 Para gerar planilha, acesse: /api/planilha/" +
                            java.net.URLEncoder.encode(telefone, "UTF-8");

                default:
                    return "🤖 Comando não reconhecido. Digite 'ajuda' para ver opções.";
            }

        } catch (Exception e) {
            log.error("Erro ao processar mensagem", e);
            return "❌ Erro ao processar sua mensagem. Tente novamente.";
        }
    }

    private String registrarTransacao(String[] partes, String telefone, String tipo) {
        if (partes.length < 3) {
            return "❌ Formato incorreto. Use: \"" + tipo + " valor categoria descrição\"\n" +
                    "Ex: " + tipo + " 150.50 mercado compra semanal";
        }

        try {
            double valor = Double.parseDouble(partes[1]);
            if (valor <= 0) {
                return "❌ Valor deve ser maior que zero.";
            }

            String categoria = partes[2];
            String descricao = partes.length > 3 ?
                    String.join(" ", java.util.Arrays.copyOfRange(partes, 3, partes.length)) :
                    "Sem descrição";

            transacaoService.registrarTransacao(telefone, tipo, categoria, valor, descricao);

            String emoji = "entrada".equals(tipo) ? "📥" : "📤";
            return emoji + " Transação registrada com sucesso!\n" +
                    "💵 Valor: R$ " + String.format("%.2f", valor) + "\n" +
                    "🏷️ Categoria: " + categoria + "\n" +
                    "📝 Descrição: " + descricao;

        } catch (NumberFormatException e) {
            return "❌ Valor inválido. Use números (ex: 150.50)";
        }
    }

    private String gerarExtrato(String telefone) {
        List<Transacao> transacoes = transacaoService.obterUltimasTransacoes(telefone, 10);
        double saldo = transacaoService.calcularSaldo(telefone);

        if (transacoes.isEmpty()) {
            return "📭 Nenhuma transação encontrada.";
        }

        StringBuilder extrato = new StringBuilder();
        extrato.append("📊 *ÚLTIMAS TRANSAÇÕES*\n\n");

        for (Transacao transacao : transacoes) {
            String emoji = "entrada".equals(transacao.getTipo()) ? "⬆️" : "⬇️";
            String valorFormatado = String.format("R$ %.2f", Math.abs(transacao.getValor()));

            extrato.append(emoji).append(" ").append(valorFormatado)
                    .append(" - ").append(transacao.getCategoria()).append("\n")
                    .append("📝 ").append(transacao.getDescricao()).append("\n")
                    .append("📅 ").append(formatarData(transacao.getData())).append("\n\n");
        }

        extrato.append("💰 *SALDO ATUAL: R$ ").append(String.format("%.2f", saldo)).append("*");

        return extrato.toString();
    }

    private String obterSaldo(String telefone) {
        double saldo = transacaoService.calcularSaldo(telefone);
        return "💰 *SALDO ATUAL:* R$ " + String.format("%.2f", saldo);
    }

    private String getMenuAjuda() {
        return """
               💼 *BOT FINANÇAS PESSOAIS* 💼
               
               📥 Registrar entrada:
               "entrada [valor] [categoria] [descrição]"
               Ex: entrada 1500 salário pagamento março
               
               📤 Registrar saída:
               "saida [valor] [categoria] [descrição]"
               Ex: saida 350 mercado compra mensal
               
               📊 Ver extrato:
               "extrato"
               
               💰 Ver saldo:
               "saldo"
               
               📈 Gerar planilha:
               "planilha"
               
               💡 Categorias sugeridas:
               alimentacao, transporte, moradia, lazer, 
               saude, educacao, salario, investimentos
               """;
    }

    private String formatarData(java.time.LocalDateTime data) {
        return data.format(java.time.format.DateTimeFormatter
                .ofPattern("dd/MM/yyyy HH:mm"));
    }

    public byte[] gerarPlanilhaBytes(String telefone) {
        try {
            return planilhaService.gerarPlanilha(telefone);
        } catch (Exception e) {
            log.error("Erro ao gerar planilha", e);
            throw new RuntimeException("Erro ao gerar planilha");
        }
    }
}
