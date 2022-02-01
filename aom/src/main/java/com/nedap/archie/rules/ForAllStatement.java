package com.nedap.archie.rules;

/**
 * Created by pieter.bos on 10/05/16.
 */
public class ForAllStatement extends Operator {

    private String variableName;
    private Expression pathExpression;
    private Expression assertion;

    public ForAllStatement() {

    }

    public ForAllStatement(String variableName, Expression pathExpression, Expression assertion) {
        setType(ExpressionType.BOOLEAN);
        setOperatorDef(new OperatorDefBuiltin(OperatorKind.for_all));
        this.variableName = variableName;
        this.pathExpression = pathExpression;
        this.assertion = assertion;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public Expression getPathExpression() {
        return pathExpression;
    }

    public void setPathExpression(Expression pathExpression) {
        this.pathExpression = pathExpression;
    }

    public Expression getAssertion() {
        return assertion;
    }

    public void setAssertion(Expression assertion) {
        this.assertion = assertion;
    }
}
