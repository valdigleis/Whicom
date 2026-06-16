package site.valdigleis.whicom.frontend.utils.AST.statement;

import java.util.ArrayList;

public record BlockStmt(ArrayList<Statement> statements) implements Statement { }
