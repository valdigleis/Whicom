package site.valdigleis.whicom.frontend.utils.AST.expression;

import site.valdigleis.whicom.frontend.utils.AST.operator.Operators;

public record UnaryExpr(Operators.Unary operator, Expression operand) implements Expression { }
