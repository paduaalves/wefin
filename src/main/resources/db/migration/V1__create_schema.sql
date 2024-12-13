CREATE TABLE wefin.produto (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(255) NOT NULL UNIQUE,
                         preco_base DECIMAL(19,2) NOT NULL
);

CREATE TABLE wefin.moeda (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       nome VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE wefin.reino (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       nome VARCHAR(255) NOT NULL UNIQUE,
                       fator_REINO DECIMAL(10,2) NOT NULL
);

CREATE TABLE wefin.taxa_cambio (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            valor_atual DECIMAL(19,2) NOT NULL,
                            ativa BOOLEAN NOT NULL,
                            data_ativacao TIMESTAMP NULL,
                            data_desativacao TIMESTAMP NULL,
                            MOEDA_origem_id BIGINT NOT NULL,
                            MOEDA_destino_id BIGINT NOT NULL,
                            FOREIGN KEY (MOEDA_origem_id) REFERENCES moeda(id),
                            FOREIGN KEY (MOEDA_destino_id) REFERENCES moeda(id)
);

CREATE TABLE wefin.transacao (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           PRODUTO_id BIGINT NOT NULL,
                           MOEDA_origem_id BIGINT NOT NULL,
                           MOEDA_destino_id BIGINT NOT NULL,
                           REINO_id BIGINT NOT NULL,
                           quantidade DOUBLE NOT NULL,
                           valor_transacao DECIMAL(19,2) NOT NULL,
                           data_transacao TIMESTAMP NOT NULL,
                           FOREIGN KEY (PRODUTO_id) REFERENCES produto(id),
                           FOREIGN KEY (MOEDA_origem_id) REFERENCES moeda(id),
                           FOREIGN KEY (MOEDA_destino_id) REFERENCES moeda(id),
                           FOREIGN KEY (REINO_id) REFERENCES reino(id)
);

Modelo de Dados

