package com.nedap.archie.rules;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class BinaryOperator extends Operator {

    private Expression leftOperand;
    private Expression rightOperand;

    public BinaryOperator() {

    }

    public BinaryOperator(ExpressionType type, OperatorKind operator, String operatorSymbol, Expression leftOperand, Expression rightOperand) {
        setType(type);
        setOperatorDef(new OperatorDefBuiltin(operator));
        setSymbol(operatorSymbol);
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    public Expression getLeftOperand() {
        return leftOperand;
    }

    public void setLeftOperand(Expression leftOperand) {
        this.leftOperand = leftOperand;
    }

    public Expression getRightOperand() {
        return rightOperand;
    }

    public void setRightOperand(Expression rightOperand) {
        this.rightOperand = rightOperand;
    }
}
