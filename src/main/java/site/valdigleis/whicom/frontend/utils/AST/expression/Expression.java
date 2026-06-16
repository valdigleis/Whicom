package site.valdigleis.whicom.frontend.utils.AST.expression;

import site.valdigleis.whicom.frontend.utils.AST.Node;

public sealed interface Expression extends Node permits 
    IntLiteral, 
    BoolLiteral, 
    VariableExpr,
    UnaryExpr,
    BinaryExpr,
    RelationalExpr { }
