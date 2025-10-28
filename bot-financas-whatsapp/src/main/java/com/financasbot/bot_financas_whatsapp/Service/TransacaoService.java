package com.financasbot.bot_financas_whatsapp.Service;

import com.financasbot.bot_financas_whatsapp.Model.Transacao;
import com.financasbot.bot_financas_whatsapp.Repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    public Transacao registrarTransacao(String telefone, String tipo, String categoria,
                                        Double valor, String descricao) {
        log.info("Registrando transação: {} - {} - R$ {}", tipo, categoria, valor);

        Transacao transacao = new Transacao(telefone, tipo, categoria, valor, descricao);
        Transacao salva = transacaoRepository.save(transacao);

        log.info("Transação registrada com ID: {}", salva.getId());
        return salva;
    }

    // REMOVA este método duplicado para evitar conflitos
    /*
    public Transacao salvarTransacao(String telefone, String tipo, String categoria, Double valor, String descricao) {
        Transacao transacao = new Transacao(descricao, valor, tipo, telefone, categoria);
        return transacaoRepository.save(transacao);
    }
    */

    public List<Transacao> buscarPorTelefone(String telefone) {
        return transacaoRepository.findByTelefone(telefone);
    }

    public List<Transacao> obterExtrato(String telefone) {
        return transacaoRepository.findByTelefoneOrderByDataDesc(telefone);
    }

    public List<Transacao> obterUltimasTransacoes(String telefone, int limite) {
        return transacaoRepository.findUltimasTransacoes(telefone, limite);
    }

    public Double calcularSaldo(String telefone) {
        return transacaoRepository.calcularSaldo(telefone);
    }

    public List<Transacao> obterTransacoesPorCategoria(String telefone, String categoria) {
        return transacaoRepository.findByTelefoneAndCategoriaOrderByDataDesc(telefone, categoria);
    }

    public List<Transacao> obterTransacoesDoMes(String telefone) {
        LocalDateTime inicioMes = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime fimMes = LocalDateTime.now();

        return transacaoRepository.findByTelefoneAndPeriodo(telefone, inicioMes, fimMes);
    }

    public Map<String, Double> obterEstatisticasPorCategoria(String telefone) {
        List<Object[]> resultados = transacaoRepository.getEstatisticasPorCategoria(telefone);

        return resultados.stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> (Double) obj[1]
                ));
    }

    public boolean validarTransacao(String tipo, Double valor) {
        if (valor == null || valor <= 0) {
            return false;
        }
        return "entrada".equals(tipo) || "saida".equals(tipo);
    }

    public List<Transacao> obterTodasTransacoes(String telefone) {
        return transacaoRepository.findByTelefone(telefone);
    }
}