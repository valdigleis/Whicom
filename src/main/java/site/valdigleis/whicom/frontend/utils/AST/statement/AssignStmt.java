package site.valdigleis.whicom.frontend.utils.AST.statement;

import site.valdigleis.whicom.frontend.utils.AST.expression.Expression;

public record AssignStmt(String variable, Expression expression) implements Statement { }
