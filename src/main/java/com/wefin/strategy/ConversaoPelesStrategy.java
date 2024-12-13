package com.wefin.strategy;

import com.wefin.model.Produto;
import com.wefin.model.Reino;
import com.wefin.strategy.ConversaoStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("pele")
public class ConversaoPelesStrategy implements ConversaoStrategy {

    @Override
    public BigDecimal converter(double quantidade, Produto produto, BigDecimal taxaCambio, Reino reino) {
        double taxaPele = 1.05;

        return taxaCambio.multiply(produto.getPrecoBase())
                .multiply(BigDecimal.valueOf(quantidade))
                .multiply(BigDecimal.valueOf(reino.getFatorReino()))
                .multiply(BigDecimal.valueOf(taxaPele));
    }
}