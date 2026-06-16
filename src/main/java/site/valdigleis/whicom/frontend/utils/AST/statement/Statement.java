package site.valdigleis.whicom.frontend.utils.AST.statement;

import site.valdigleis.whicom.frontend.utils.AST.Node;

public sealed interface Statement extends Node permits 
    AssignStmt,
    SkipStmt,
    IfStmt,
    WhileStmt,
    BlockStmt {
}
