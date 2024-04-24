package fr.usmb.m1isc.compilation.tp;

import java.io.*;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {
        LexicalAnalyzer yy = new LexicalAnalyzer((args.length > 0) ? new FileReader(args[0]) : new InputStreamReader(System.in));
        @SuppressWarnings("deprecation")
        parser p = new parser(yy);

        Noeud racine = (Noeud) p.parse().value;

        String dataWrite = dataSegments(racine);
        String code = codeSegments(racine);

        try (FileWriter fw = new FileWriter("resultat.asm", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(dataWrite);
            out.print(code);
        } catch (IOException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private static String dataSegments(Noeud racine){
        Set<String> lets = racine.getLet();
        StringBuilder sb = new StringBuilder();
        sb.append("DATA SEGMENT\n");
        for(String let : lets){
            sb.append("\t").append(let).append(" DD\n");
        }
        sb.append("DATA ENDS\n");
        return sb.toString();
    }

    private static String codeSegments(Noeud racine){
        return "CODE SEGMENT\n" +
                racine.codeSegment() +
                "CODE ENDS\n";
    }

}
