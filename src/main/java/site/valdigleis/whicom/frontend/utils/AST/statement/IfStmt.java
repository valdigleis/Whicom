package site.valdigleis.whicom.frontend.utils.AST.statement;

import site.valdigleis.whicom.frontend.utils.AST.expression.Expression;

public record IfStmt(Expression condition, Statement thenBranch, Statement elseBranch) implements Statement { }
