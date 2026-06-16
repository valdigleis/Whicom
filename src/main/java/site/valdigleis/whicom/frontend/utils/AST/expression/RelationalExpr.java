package site.valdigleis.whicom.frontend.utils.AST.expression;

import site.valdigleis.whicom.frontend.utils.AST.operator.Operators;

public record RelationalExpr(Expression left, Operators.Relational operator, Expression right) implements Expression { }
