/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Virus;

import java.util.HashMap;
import java.util.Random;

/**
 * @author mrigesh
 */
public class VirusGenome {

    String generateGenotype(String isMutationOccured, String bases, int gene) {


        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("A", 4);
        map.put("C", 3);
        map.put("T", 2);
        map.put("G", 1);
        map.put("U", 10);

        int sum = 0;
        StringBuilder sb = new StringBuilder(gene);
        for (int i = 0; i < gene; i++) {
            int index = (int) (bases.length() * Math.random());
            sum += map.get(Character.toString(bases.charAt(index)));
            sb.append(bases.charAt(index));
        }
        displayfitnessValue(sum);
        return sb.toString();

    }

    public void displayGenotype(String isMutationOccured, String rate, String recombination_rate) {
        Random randomno = new Random();
        for (int i = 0; i < Integer.parseInt(recombination_rate); i++) {
            if (randomno.nextDouble() < Double.parseDouble(rate)) {
                String base = "ACGTU";
                System.out.println("*********[Mutant]********  : " + generateGenotype(isMutationOccured, base, 10));
            } else {
                String base = "ACGT";
                System.out.println(generateGenotype(isMutationOccured, base, 10));
            }
        }

    }

    private void displayfitnessValue(int sum) {
        //System.out.println("Sum : " + sum);
    }

}
