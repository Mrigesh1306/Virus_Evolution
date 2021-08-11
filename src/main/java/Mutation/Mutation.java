package Mutation;

import org.ini4j.Ini;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Mutation {

    public static HashMap<Integer, Color> mutationColor = new HashMap<>();
    public String genotype;
    public int infection_factor;
    public static double previousHostFitness = 0;
    public static int maxCurrentFitness = 0;
    public static int maxPreviousFitness = 0;

//    public static void main(String[] args) throws IOException {
//
//        int fitness1 = calculateGenotypeFitness("2BCDEFGHIJ", 97);
//        boolean isVariant1 = calculateMutationFactor("2BCDEFGHIJ", fitness1, fitnessHashTable);
//
//        int fitness2 = calculateGenotypeFitness("3BCDEFGHIJ", 96);
//        boolean isVariant2 = calculateMutationFactor("3BCDEFGHIJ", fitness2, fitnessHashTable);
//        System.out.println("isVariant2 "+isVariant2);
//    }


    public static int calculateGenotypeFitness(String genotype, double infection_factor) throws IOException {
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> gene_length = ini.get("gene_length");
        Map<String, String> base_value = ini.get("base_value");
        int baseVal = Integer.parseInt(base_value.get("base"));
        int genotypefitnessValue = 0;
        double gene_fitness_value = 0;
        boolean islarger = genotype.length() > 10 ? true : false;

        char[] gene = genotype.toCharArray();
        int j = 0;
        for (int i = 0; i < genotype.length(); i++) {
            int geneLength = Integer.parseInt(gene_length.get(gene_length.keySet().toArray()[j]));

            if (Character.isAlphabetic(gene[i])) {

                gene_fitness_value = (3 * baseVal) * geneLength;

            } else if (Character.isDigit(gene[i])) {
                if (islarger) {
                    int sum = Integer.parseInt(Character.toString(gene[i])) * 10 +
                            Integer.parseInt(Character.toString(gene[i + 1]));

                    gene_fitness_value = ((2 * baseVal)
                            + infection_factor * sum)
                            * geneLength;
                    islarger = false;
                    i = i + 2;
                } else {
                    gene_fitness_value = ((2 * baseVal)
                            + infection_factor * Integer.parseInt(Character.toString(gene[i])))
                            * geneLength;
                }
            }
            // insertIntoMutationList(i);
            genotypefitnessValue += gene_fitness_value;
            j++;
        }
        System.out.println("Genotype " + genotype + " Value " + genotypefitnessValue);
        return genotypefitnessValue;
    }

    //Inserts every mutation into this HashMap and assigns random color
    public static void insertIntoMutationList(int mutationCount) {
        Random r = new Random();
        new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
        while(true)
        {
            int rc=r.nextInt(255);
            int g=r.nextInt(255);
            int b=r.nextInt(255);
            if(!(rc==255 && g==255 && b==255 || rc==90 && g==255 && b==0 || rc==177 && g==177 && b==177 || rc==0 && g==0 && b==0 ))
            {
                mutationColor.put(mutationCount,new Color(rc,g,b) );
               break;
            }


        }

    }

    //Fetch the color based on mutation count
    public Color fetchmutationColor(int mutationCount) {
        return mutationColor.get(mutationCount);
    }

    //    Check if the mutation is variant
    public static boolean calculateMutationFactor(String genotype, double currentfitnessValue, Hashtable<String, List<String>> fitnessHashTable) throws IOException {
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> map = ini.get("default");
        double dominant_factor = Double.parseDouble(map.get("dominant_factor"));

        maxCurrentFitness = calculateGenotypeFitness(genotype, 100);

        double variantThreshold = maxPreviousFitness + (maxCurrentFitness - maxPreviousFitness) * dominant_factor;

        maxPreviousFitness = maxCurrentFitness;
        previousHostFitness = currentfitnessValue;

        if(currentfitnessValue > variantThreshold)
        {
            //insert into variant directory if mutation is variant
            insertIntoMutationList(fitnessHashTable.size()+1);
        }
        return currentfitnessValue > variantThreshold;
    }

    public static HashMap<Integer, Color> getMutationColor() {
        return mutationColor;
    }

    public static void setMutationColor(HashMap<Integer, Color> mutationColor) {
        Mutation.mutationColor = mutationColor;
    }
}
