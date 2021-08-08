package Mutation;

import org.ini4j.Ini;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Mutation {

    public static HashMap<Integer, Color> mutationColor = new HashMap<>();

    public static void main(String[] args) throws IOException {
        int s = calculateGenotypeFitness("1BCDEFGHIJ", 93);
        int q = calculateGenotypeFitness("1BCDEFGHIJ", 97);
    }

    public static int calculateGenotypeFitness(String genotype, int infection_factor) throws IOException {
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> gene_length = ini.get("gene_length");
        Map<String, String> base_value = ini.get("base_value");
        int baseVal = Integer.parseInt(base_value.get("base"));
        int genotypefitnessValue = 0;
        int gene_fitness_value = 0;
        boolean islarger = genotype.length() > 10 ? true : false;
        int loop = genotype.length() > 10 ? genotype.length() : genotype.length();

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
            genotypefitnessValue += gene_fitness_value;
            j++;
        }
        return genotypefitnessValue;
    }



    //Inserts every mutation into this HashMap and assigns random color
    public static void insertIntoMutationList(int mutationCount) {
        Random r = new Random();
        mutationColor.put(mutationCount, new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
    }

    //Fetch the color based on mutation count
    public static Color fetchmutationColor(int mutationCount) {
        return mutationColor.get(mutationCount);
    }





}
