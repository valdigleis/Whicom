package site.valdigleis.whicom.ast;

public class ASTPrinter {
    
    public String print(Cmd cmd) {
        return visit(cmd, 0);
    }

    private String visit(Cmd cmd, int indent) {
        String pad = "  ".repeat(indent);
        if (cmd instanceof Assign a) {
            return pad + "Assign " + a.getVariable().getLexeme() + " " + this.visitExpr(a.getValue(), indent);
        }
        if (cmd instanceof Block b) {
            StringBuilder sb = new StringBuilder();
            sb.append(pad).append("Block. . .\n");
            for (Cmd c : b.getCommands()) {
                sb.append(visit(c, indent + 1)).append("\n");
            }
            return sb.toString();
        }

        if (cmd instanceof Skip) {
            return pad + "Skip";
        }

        if (cmd instanceof Loop l) {
            return pad + "While " + this.visitExpr(l.getCondition(), indent + 1) + "\n" + pad + "\n" + this.visit(l.getBody(), indent + 1) + "\n" + pad + "";
        }
        if (cmd instanceof Conditional c) {
            return pad + "If " + visitExpr(c.getCondition(), indent + 1) + " " + pad + " Then \n" + visit(c.getThenBranch(), indent + 1) + "\n" + pad + " Else \n" + visit(c.getElseBranch(), indent + 1) + "\n" + pad + "";
        }
        return pad + "UnknownCmd";
    }

    private String visitExpr(Expr expr, int indent) {
        String pad = "  ".repeat(indent);
        if (expr instanceof Literal l) {
            return pad + l.getValue().getLexeme();
        }
        if (expr instanceof Variable v) {
            return pad + v.getName().getLexeme();
        }
        if (expr instanceof Binary b) {
            return pad + " " + visitExpr(b.getLeft(), 0) + " " + b.getOperator().getLexeme() + " " + visitExpr(b.getRight(), 0) + " ";
        }
        if (expr instanceof Unary u) {
            return pad + "(" + u.getOperator().getLexeme() + visitExpr(u.getExpr(), 0) + ")";
        }
        return pad + "UnknownExpr";
    }
}
