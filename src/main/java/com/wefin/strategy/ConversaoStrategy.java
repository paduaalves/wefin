package com.wefin.strategy;

import com.wefin.model.Produto;
import com.wefin.model.Reino;

import java.math.BigDecimal;

public interface ConversaoStrategy {
    BigDecimal converter(double quantidade, Produto produto, BigDecimal taxaCambio, Reino reino);
}
