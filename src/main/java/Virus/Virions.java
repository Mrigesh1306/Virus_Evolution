package Virus;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static void VirionFamily() {
        String st, t="";
        for (int i = 0; i < 10; i++) {

                st = vg.generateGenotype("true", "ACGT", 10);
                StringBuilder sb = new StringBuilder(st);
                sb.setCharAt(0, 'U');
                t = sb.toString();
                System.out.println(t);
                v1.add(t);
        }
        VirionReproduction(t);
        //System.out.println("v1" +v1);
        //System.out.println(Arrays.toString(v1));
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
        String pv;

        Random r = new Random();
        pv = shuffle(s);
        v1.add(pv);

        return pv;



    }
}
