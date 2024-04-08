package fr.usmb.m1isc.compilation.tp;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {
        LexicalAnalyzer yy = new LexicalAnalyzer((args.length > 0) ? new FileReader(args[0]) : new InputStreamReader(System.in));
        @SuppressWarnings("deprecation")
        parser p = new parser(yy);

        Noeud racine = (Noeud) p.parse().value;
        printDataSegments(racine);
        printCodeSegments(racine);
    }

    private static void printDataSegments(Noeud racine){
        Set<String> lets = racine.getLet();
        StringBuilder sb = new StringBuilder();
        sb.append("DATA SEGMENT\n");
        for(String let : lets){
            sb.append("\t").append(let).append(" DD\n");
        }
        sb.append("DATA ENDS\n");
        System.out.println(sb.toString());
    }

    private static void printCodeSegments(Noeud racine){
        StringBuilder sb = new StringBuilder();
        sb.append("CODE SEGMENT\n");
        sb.append(racine.codeSegment());
        sb.append("CODE ENDS\n");
        System.out.println(sb.toString());
    }

}
