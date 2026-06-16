package site.valdigleis.whicom.frontend.utils.AST;

import site.valdigleis.whicom.frontend.utils.AST.statement.Statement;

import java.util.List;

public record Program(List<Statement> statements) implements Node { } 
