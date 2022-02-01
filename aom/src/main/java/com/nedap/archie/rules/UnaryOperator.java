package com.nedap.archie.rules;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class UnaryOperator extends Operator {

    private Expression operand;

    public UnaryOperator() {

    }

    public UnaryOperator(ExpressionType type, OperatorKind operator, String operatorSymbol, Expression operand) {
        setType(type);
        setOperatorDef(new OperatorDefBuiltin(operator));
        setSymbol(operatorSymbol);
        this.operand = operand;
    }

    public Expression getOperand() {
        return operand;
    }

    public void setOperand(Expression operand) {
        this.operand = operand;
    }
}
