package com.nedap.archie.rules;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class Operator extends Expression {

    private OperatorDef operatorDef;

    private String symbol;

    public OperatorDef getOperatorDef() {
        return operatorDef;
    }

    public void setOperatorDef(OperatorDef operatorDef) {
        this.operatorDef = operatorDef;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public OperatorKind getOperator() {
        return OperatorKind.parseFromIdentifier(operatorDef.getIdentifier());
    }

    public void setOperator(OperatorKind operator) {
        this.operatorDef = new OperatorDefBuiltin(operator);
    }
}
