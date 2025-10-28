package com.financasbot.bot_financas_whatsapp.Service;

import com.financasbot.bot_financas_whatsapp.Model.Transacao;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanilhaService {

    private final TransacaoService transacaoService;

    public byte[] gerarPlanilha(String telefone) throws IOException {
        List<Transacao> transacoes = transacaoService.obterTodasTransacoes(telefone);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Finanças Pessoais");

            // Estilos
            CellStyle headerStyle = criarEstiloCabecalho(workbook);
            CellStyle currencyStyle = criarEstiloMoeda(workbook);
            CellStyle dateStyle = criarEstiloData(workbook);

            // Cabeçalho
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Data", "Tipo", "Categoria", "Valor (R$)", "Descrição"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Dados
            int rowNum = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Transacao transacao : transacoes) {
                Row row = sheet.createRow(rowNum++);

                // Data
                Cell cellData = row.createCell(0);
                cellData.setCellValue(transacao.getData().format(formatter));
                cellData.setCellStyle(dateStyle);

                // Tipo
                row.createCell(1).setCellValue(
                        "entrada".equals(transacao.getTipo()) ? "Entrada" : "Saída"
                );

                // Categoria
                row.createCell(2).setCellValue(transacao.getCategoria());

                // Valor
                Cell cellValor = row.createCell(3);
                cellValor.setCellValue(Math.abs(transacao.getValor()));
                cellValor.setCellStyle(currencyStyle);

                // Descrição
                row.createCell(4).setCellValue(transacao.getDescricao());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Linha do saldo
            Row saldoRow = sheet.createRow(rowNum + 1);
            saldoRow.createCell(0).setCellValue("SALDO TOTAL:");
            Cell cellSaldo = saldoRow.createCell(3);
            cellSaldo.setCellValue(transacaoService.calcularSaldo(telefone));
            cellSaldo.setCellStyle(currencyStyle);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private CellStyle criarEstiloCabecalho(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle criarEstiloMoeda(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("R$ #,##0.00"));
        return style;
    }

    private CellStyle criarEstiloData(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
        return style;
    }
}