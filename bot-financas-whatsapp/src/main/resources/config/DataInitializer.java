package com.financasbot.bot_financas_whatsapp.config;

import com.financasbot.bot_financas_whatsapp.Model.Transacao;
import com.financasbot.bot_financas_whatsapp.Repository.TransacaoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final TransacaoRepository transacaoRepository;

    @PostConstruct
    public void init() {
        log.info("=== INICIALIZANDO BANCO DE DADOS ===");

        try {
            // Isso forçará a criação da tabela
            if (transacaoRepository.count() == 0) {
                log.info("Criando dados de exemplo...");

                Transacao t1 = new Transacao();
                t1.setTelefone("whatsapp:+5511999999999");
                t1.setTipo("entrada");
                t1.setCategoria("salario");
                t1.setValor(2500.0);
                t1.setDescricao("Salário mensal");

                transacaoRepository.save(t1);
                log.info("Tabela criada e dados inseridos!");
            }
        } catch (Exception e) {
            log.error("Erro ao inicializar banco: " + e.getMessage());
        }
    }
}