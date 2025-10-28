package com.financasbot.bot_financas_whatsapp.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String tipo; // "RECEITA" ou "DESPESA"

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private Double valor;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime data;

    // Construtores
    public Transacao() {}

    // Construtor para TransacaoService
    public Transacao(String telefone, String tipo, String categoria, Double valor, String descricao) {
        this.telefone = telefone;
        this.tipo = tipo;
        this.categoria = categoria;
        this.valor = valor;
        this.descricao = descricao;
        this.data = LocalDateTime.now();
    }

    // Construtor para FinancasService
    public Transacao(String descricao, Double valor, String tipo, String telefone) {
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.telefone = telefone;
        this.categoria = "GERAL";
        this.data = LocalDateTime.now();
    }

    public Transacao(String descricao, Double valor, String tipo, String telefone, String categoria) {
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.telefone = telefone;
        this.categoria = categoria;
        this.data = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (data == null) {
            data = LocalDateTime.now();
        }
    }
}