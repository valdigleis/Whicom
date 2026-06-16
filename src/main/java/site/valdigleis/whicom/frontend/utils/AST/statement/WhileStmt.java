package site.valdigleis.whicom.frontend.utils.AST.statement;

import site.valdigleis.whicom.frontend.utils.AST.expression.Expression;

public record WhileStmt(Expression condition, Statement body) implements Statement { }
