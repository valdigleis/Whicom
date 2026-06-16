package site.valdigleis.whicom.frontend.utils.AST.expression;

import site.valdigleis.whicom.frontend.utils.AST.operator.Operators;

public record BinaryExpr(Expression left, Operators.Binary operator, Expression right) implements Expression { }
