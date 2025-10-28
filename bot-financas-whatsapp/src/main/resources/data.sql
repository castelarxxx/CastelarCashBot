-- Remova dados existentes se necessário
DELETE FROM transacoes;

-- Insira dados com todas as colunas obrigatórias
INSERT INTO transacoes (descricao, valor, tipo, telefone, categoria, data) VALUES
('Salário', 2500.00, 'RECEITA', 'telegram_123456', 'SALÁRIO', CURRENT_TIMESTAMP),
('Aluguel', 800.00, 'DESPESA', 'telegram_123456', 'MORADIA', CURRENT_TIMESTAMP),
('Supermercado', 350.50, 'DESPESA', 'telegram_123456', 'ALIMENTAÇÃO', CURRENT_TIMESTAMP);