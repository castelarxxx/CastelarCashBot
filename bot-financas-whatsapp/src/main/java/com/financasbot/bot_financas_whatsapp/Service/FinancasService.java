package com.financasbot.bot_financas_whatsapp.Service;

import com.financasbot.bot_financas_whatsapp.Model.Transacao;
import com.financasbot.bot_financas_whatsapp.Repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FinancasService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    // CORREÇÃO: Método com parâmetro telefone
    public String processMessage(String message, String telefone) {
        String messageLower = message.trim().toLowerCase();

        if (messageLower.startsWith("add receita") || messageLower.startsWith("adicionar receita")) {
            return adicionarReceita(messageLower, telefone);
        } else if (messageLower.startsWith("add despesa") || messageLower.startsWith("adicionar despesa")) {
            return adicionarDespesa(messageLower, telefone);
        } else if (messageLower.contains("extrato")) {
            return gerarExtrato(telefone);
        } else if (messageLower.contains("saldo")) {
            return consultarSaldo(telefone);
        } else if (messageLower.equals("menu") || messageLower.equals("ajuda") || messageLower.equals("/start")) {
            return mostrarMenu();
        } else {
            return mostrarMenu();
        }
    }

    private String adicionarReceita(String message, String telefone) {
        try {
            String[] parts = message.split(" ", 4);
            double valor = Double.parseDouble(parts[2]);
            String descricao = parts[3];

            Transacao transacao = new Transacao(descricao, valor, "RECEITA", telefone);
            transacaoRepository.save(transacao);

            return "✅ *Receita Adicionada!*\n" +
                    "💵 Valor: R$ " + valor + "\n" +
                    "📝 Descrição: " + descricao + "\n" +
                    "💰 Tipo: Receita";

        } catch (Exception e) {
            return "❌ Formato incorreto! Use: `add receita [valor] [descrição]`\n" +
                    "Exemplo: `add receita 1500 Salário`";
        }
    }

    private String adicionarDespesa(String message, String telefone) {
        try {
            String[] parts = message.split(" ", 4);
            double valor = Double.parseDouble(parts[2]);
            String descricao = parts[3];

            Transacao transacao = new Transacao(descricao, valor, "DESPESA", telefone);
            transacaoRepository.save(transacao);

            return "✅ *Despesa Adicionada!*\n" +
                    "💵 Valor: R$ " + valor + "\n" +
                    "📝 Descrição: " + descricao + "\n" +
                    "💰 Tipo: Despesa";

        } catch (Exception e) {
            return "❌ Formato incorreto! Use: `add despesa [valor] [descrição]`\n" +
                    "Exemplo: `add despesa 300 Mercado`";
        }
    }

    private String gerarExtrato(String telefone) {
        List<Transacao> transacoes = transacaoRepository.findByTelefoneOrderByDataDesc(telefone);

        if (transacoes.isEmpty()) {
            return "📊 *Extrato Vazio*\nNenhuma transação registrada ainda.";
        }

        StringBuilder extrato = new StringBuilder();
        extrato.append("📊 *Extrato Financeiro*\n\n");

        double total = 0;
        for (Transacao transacao : transacoes) {
            String emoji = transacao.getTipo().equals("RECEITA") ? "✅" : "❌";
            extrato.append(emoji).append(" ")
                    .append(transacao.getDescricao()).append(": R$ ")
                    .append(transacao.getValor()).append("\n");

            if (transacao.getTipo().equals("RECEITA")) {
                total += transacao.getValor();
            } else {
                total -= transacao.getValor();
            }
        }

        extrato.append("\n💰 *Saldo Total: R$ ").append(total).append("*");
        return extrato.toString();
    }

    private String consultarSaldo(String telefone) {
        Double saldo = transacaoRepository.calcularSaldo(telefone);
        return "💰 *Saldo Atual:* R$ " + (saldo != null ? saldo : 0);
    }

    private String mostrarMenu() {
        return "🤖 *Bot Finanças* \\- Menu\n\n" +
                "💡 *Comandos Disponíveis:*\n\n" +
                "💰 *Adicionar Receita:*\n" +
                "`add receita [valor] [descrição]`\n\n" +
                "💸 *Adicionar Despesa:*\n" +
                "`add despesa [valor] [descrição]`\n\n" +
                "📊 *Ver Extrato:*\n" +
                "`extrato`\n\n" +
                "💰 *Consultar Saldo:*\n" +
                "`saldo`\n\n" +
                "📋 *Menu de Ajuda:*\n" +
                "`menu` ou `ajuda`";
    }
}