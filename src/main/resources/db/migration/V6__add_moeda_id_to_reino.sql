ALTER TABLE wefin.reino
    ADD COLUMN moeda_id BIGINT;

ALTER TABLE wefin.reino
    ADD CONSTRAINT fk_reino_moeda
        FOREIGN KEY (moeda_id) REFERENCES wefin.moeda(id);
