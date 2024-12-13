package com.wefin.strategy;

import com.wefin.model.Produto;
import com.wefin.model.Reino;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Component("hidromel")
public class ConversaoHidromelStrategy implements ConversaoStrategy {
    @Override
    public BigDecimal converter(double quantidade, Produto produto, BigDecimal taxaCambio, Reino reino) {
        return taxaCambio.multiply(produto.getPrecoBase())
                .multiply(BigDecimal.valueOf(quantidade))
                .multiply(BigDecimal.valueOf(reino.getFatorReino()));
    }
}

