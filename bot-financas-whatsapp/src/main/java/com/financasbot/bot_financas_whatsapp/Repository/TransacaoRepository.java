package com.financasbot.bot_financas_whatsapp.Repository;


import com.financasbot.bot_financas_whatsapp.Model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    // Buscar transações por telefone ordenadas por data (mais recentes primeiro)
    List<Transacao> findByTelefoneOrderByDataDesc(String telefone);


    List<Transacao> findByTelefone(String telefone);


    // Calcular saldo total por telefone
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t WHERE t.telefone = :telefone")
    Double calcularSaldo(@Param("telefone") String telefone);

    // Buscar últimas N transações
    @Query(value = "SELECT * FROM transacoes t WHERE t.telefone = :telefone ORDER BY t.data DESC LIMIT :limite",
            nativeQuery = true)
    List<Transacao> findUltimasTransacoes(@Param("telefone") String telefone,
                                          @Param("limite") int limite);

    // Buscar transações por período
    @Query("SELECT t FROM Transacao t WHERE t.telefone = :telefone AND t.data BETWEEN :inicio AND :fim ORDER BY t.data DESC")
    List<Transacao> findByTelefoneAndPeriodo(@Param("telefone") String telefone,
                                             @Param("inicio") LocalDateTime inicio,
                                             @Param("fim") LocalDateTime fim);

    List<Transacao> findByTelefoneAndCategoriaOrderByDataDesc(String telefone, String categoria);

    @Query("SELECT t.categoria, SUM(t.valor) FROM Transacao t WHERE t.telefone = :telefone GROUP BY t.categoria")
    List<Object[]> getEstatisticasPorCategoria(@Param("telefone") String telefone);
}