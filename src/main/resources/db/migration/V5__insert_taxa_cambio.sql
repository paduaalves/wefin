-- Inserir uma taxa ativa de 2.5 entre Ouro Real (origem) e Tibar (destino)
INSERT INTO wefin.taxa_cambio (valor_atual, ativa, data_ativacao, moeda_origem_id, moeda_destino_id)
VALUES (2.5, true, NOW(),
        (SELECT id FROM moeda WHERE nome = 'Ouro Real'),
        (SELECT id FROM moeda WHERE nome = 'Tibar'));

-- Inserir uma segunda taxa inativa de exemplo entre Ouro Real e Tibar
INSERT INTO wefin.taxa_cambio (valor_atual, ativa, data_ativacao, moeda_origem_id, moeda_destino_id)
VALUES (0.4, true, NOW(),
        (SELECT id FROM moeda WHERE nome = 'Tibar'),
        (SELECT id FROM moeda WHERE nome = 'Ouro Real'));
