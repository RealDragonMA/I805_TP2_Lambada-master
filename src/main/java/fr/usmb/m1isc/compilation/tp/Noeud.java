package fr.usmb.m1isc.compilation.tp;

import java.util.HashSet;
import java.util.Set;

public class Noeud {

    public enum TypeNoeud {SEQUENCE, EXPRESSION, EXPR, VAR, INT, OUTPUT, INPUT, NIL}

    private TypeNoeud typeNoeud;
    private String typeExpression;
    private Noeud gauche;
    private Noeud droit;

    private static int cpt = 0;

    public Noeud(TypeNoeud typeNoeud, String typeExpression, Noeud gauche, Noeud droit) {
        this.typeNoeud = typeNoeud;
        this.typeExpression = typeExpression;
        this.gauche = gauche;
        this.droit = droit;
    }

    public Noeud(TypeNoeud typeNoeud, String typeExpression) {
        this.typeNoeud = typeNoeud;
        this.typeExpression = typeExpression;
        this.gauche = null;
        this.droit = null;
    }

    /**
     * Retourne l'ensemble des variables let déclarées dans le programme
     * @return {@link Set<String>}
     */
    public Set<String> getLet() {
        Set<String> lets = new HashSet<>();
        if(getType() == TypeNoeud.EXPRESSION && getValue().equalsIgnoreCase("let")) {
            lets.add(getGauche().getValue());
        }

        Set<String> letsGauche = getGauche() != null ? getGauche().getLet() : null;
        Set<String> letsDroit = getDroit() != null ? getDroit().getLet() : null;

        if(letsGauche != null) lets.addAll(letsGauche);
        if(letsDroit != null) lets.addAll(letsDroit);

        return lets;
    }

    /**
     * Retourne le code assembleur du segment de données
     * @return {@link String}
     */
    public String codeSegment(){
        return switch (getType()) {
            case SEQUENCE -> generateSequence();
            case EXPRESSION -> generateExpression();
            case INT, VAR -> generateInt();
            default -> generateExpr();
        };
    }

    public String generateSequence(){
        return (getGauche() == null ? "" : getGauche().codeSegment()) + (getDroit() == null ? "" : getDroit().codeSegment());
    }

    public String generateExpression(){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        switch (getValue()){
            case "LET":
                String right = getDroit().codeSegment();
                sb.append(right);
                sb.append("\t").append("mov ").append(getGauche().getValue()).append(", eax").append("\n");
                break;
            case "WHILE":
                i = cpt++;
                sb.append("debut_while_").append(i).append(":\n");
                sb.append(getGauche().codeSegment());
                sb.append("\tjz ").append("fin_while_").append(i).append("\n");
                sb.append(getDroit().codeSegment());
                sb.append("\tjmp ").append("debut_while_").append(i).append("\n");
                sb.append("fin_while_").append(i).append(":\n");
                break;
            case "IF":
                i = cpt++;
                sb.append(getGauche().codeSegment());
                sb.append("\tjz ").append("else_").append(i).append("\n");
                sb.append(getDroit().getGauche().codeSegment());
                sb.append("\tjmp ").append("fin_if_").append(i).append("\n");
                sb.append("else_").append(i).append(":\n");
                sb.append(getDroit().getDroit() == null ? "" : getDroit().getDroit().codeSegment());
                sb.append("fin_if_").append(i).append(":\n");
                break;
        }
        return sb.toString();
    }

    public String generateInt(){
        StringBuilder sb = new StringBuilder();
        sb.append("\t").append("mov eax, ").append(getValue()).append("\n");
        sb.append("\t").append("push eax").append("\n");
        return sb.toString();
    }


    public String generateExpr(){
        StringBuilder sb = new StringBuilder();
        String leftExpr;
        String rightExpr;
        int i = 0;
        Noeud temp;
        switch (getValue()){
            case "+":
                leftExpr = getGauche().generateExpr();
                rightExpr = getDroit().generateExpr();
                sb.append(leftExpr);
                sb.append(rightExpr);
                sb.append("\t").append("pop eax").append("\n");
                sb.append("\t").append("pop ebx").append("\n");
                sb.append("\t").append("add eax, ebx").append("\n");
                sb.append("\t").append("push eax").append("\n");
                break;
            case "-":
                leftExpr = getGauche().generateExpr();
                rightExpr = getDroit().generateExpr();
                sb.append(leftExpr);
                sb.append(rightExpr);
                sb.append("\t").append("pop eax").append("\n");
                sb.append("\t").append("pop ebx").append("\n");
                sb.append("\t").append("sub eax, ebx").append("\n");
                sb.append("\t").append("push eax").append("\n");
                break;
            case "*":
                leftExpr = getGauche().generateExpr();
                rightExpr = getDroit().generateExpr();
                sb.append(leftExpr);
                sb.append(rightExpr);
                sb.append("\t").append("pop eax").append("\n");
                sb.append("\t").append("pop ebx").append("\n");
                sb.append("\t").append("imul eax, ebx").append("\n");
                sb.append("\t").append("push eax").append("\n");
                break;
            case "/":
                leftExpr = getGauche().generateExpr();
                rightExpr = getDroit().generateExpr();
                sb.append(leftExpr);
                sb.append(rightExpr);
                sb.append("\t").append("pop eax").append("\n");
                sb.append("\t").append("pop ebx").append("\n");
                sb.append("\t").append("idiv eax, ebx").append("\n");
                sb.append("\t").append("push eax").append("\n");
                break;
            case "<":
                i = cpt++;
                temp = new Noeud(TypeNoeud.EXPR, "-", getGauche(), getDroit());
                sb.append(temp.codeSegment());
                sb.append("\t").append("jl ").append("debut_lt_").append(i).append("\n");
                sb.append("\t").append("mov eax, 0").append("\n");
                sb.append("\t").append("jmp ").append("fin_lt_").append(i).append("\n");
                sb.append("debut_lt_").append(i).append(":\n");
                sb.append("\t").append("mov eax, 1").append("\n");
                sb.append("fin_lt_").append(i).append(":\n");
                break;
            case ">":
                i = cpt++;
                temp = new Noeud(TypeNoeud.EXPR, "-", getGauche(), getDroit());
                sb.append(temp.codeSegment());
                sb.append("\t").append("jl ").append("debut_gt_").append(i).append("\n");
                sb.append("\t").append("mov eax, 0").append("\n");
                sb.append("\t").append("jmp ").append("fin_gt_").append(i).append("\n");
                sb.append("debut_gt_").append(i).append(":\n");
                sb.append("\t").append("mov eax, 1").append("\n");
                sb.append("fin_gt_").append(i).append(":\n");
                break;
            case "<=":
                i = cpt++;
                temp = new Noeud(TypeNoeud.EXPR, "-", getGauche(), getDroit());
                sb.append(temp.codeSegment());
                sb.append("\t").append("jg ").append("debut_lte_").append(i).append("\n");
                sb.append("\t").append("mov eax, 0").append("\n");
                sb.append("\t").append("jmp ").append("fin_lte_").append(i).append("\n");
                sb.append("debut_lte_").append(i).append(":\n");
                sb.append("\t").append("mov eax, 1").append("\n");
                sb.append("fin_lte_").append(i).append(":\n");
                break;
            case ">=":
                i = cpt++;
                temp = new Noeud(TypeNoeud.EXPR, "-", getGauche(), getDroit());
                sb.append(temp.codeSegment());
                sb.append("\t").append("jg ").append("debut_gte_").append(i).append("\n");
                sb.append("\t").append("mov eax, 0").append("\n");
                sb.append("\t").append("jmp ").append("fin_gte_").append(i).append("\n");
                sb.append("debut_gte_").append(i).append(":\n");
                sb.append("\t").append("mov eax, 1").append("\n");
                sb.append("fin_gte_").append(i).append(":\n");
                break;
            case "AND":
                i = cpt++;
                leftExpr = getGauche().codeSegment();
                rightExpr = getDroit().codeSegment();
                sb.append(leftExpr);
                sb.append("\t").append("jz ").append("fin_and_").append(i).append("\n");
                sb.append(rightExpr);
                sb.append("\t").append("jz ").append("fin_and_").append(i).append("\n");
                break;
        }
        return sb.toString();
    }


    public TypeNoeud getType() {
        return typeNoeud;
    }

    public String getValue() {
        return typeExpression;
    }

    public Noeud getGauche() {
        return gauche;
    }

    public Noeud getDroit() {
        return droit;
    }


}
