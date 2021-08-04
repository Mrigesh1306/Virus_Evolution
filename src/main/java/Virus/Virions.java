package Virus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Virions {
    List<ArrayList<String>> virionFam = new ArrayList<ArrayList<String>>();
    static ArrayList<String> v1 = new ArrayList<String>();
    //static String[] cat1 = new String[10];
    //static String[] cat2 = new String[10];
    //static String[] cat3 = new String[10];
    //static String[] cat4 = new String[10];
    static VirusGenome vg = new VirusGenome();

    public static void virionFamily() {

        for (int i = 0; i < 3000; i++) {
            for (String s : v1) {
                v1.add(vg.generateGenotype("true", "ACGT", 10));
            }
        }
        for (String str : v1)
        {
            System.out.println(str);
        }

    }
    public static String shuffle(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        double r;
        for (char c: string.toCharArray()) {
            r = Math.random();
            if (r < 0.34)
                sb.append(c);
            else if (r < 0.67)
                sb.insert(sb.length() / 2, c);
            else
                sb.insert(0, c);
        }
        return sb.toString();
    }

    public static String VirionReproduction(String s){
        //String pv;

        Random r = new Random();
        shuffle(s);
        char gb = s.charAt(s.length()-1), temp;
        if(gb=='A'){
            String g = "GCT";
            temp = g.charAt(r.nextInt(g.length()));
            s.replace(s.charAt(9), temp);
        }
        else if(gb=='G'){
            String g = "ACT";
            temp = g.charAt(r.nextInt(g.length()));
            s.replace(s.charAt(9), temp);
        }
        else if(gb=='C'){
            String g = "AGT";
            temp = g.charAt(r.nextInt(g.length()));
            s.replace(s.charAt(9), temp);
        }

        else {
            String g = "ACG";
            temp = g.charAt(r.nextInt(g.length()));
            s.replace(s.charAt(9), temp);
        }
        return s;



    }
}
