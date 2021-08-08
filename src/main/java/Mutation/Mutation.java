package Mutation;

import org.ini4j.Ini;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Mutation {


    public static int infection_factor;   //U = host_value
    //public static int genotypefitnessValue;
    public static HashMap<String, Color> variantColor= new HashMap<String, Color>();;

    public static void main(String[] args) throws IOException {
       int s = calculateGenotypeFitness("1BCDEFGHIJ",93);
        int q = calculateGenotypeFitness("1BCDEFGHIJ",97);
    }


    public static int calculateGenotypeFitness(String genotype, int infection_factor) throws IOException {
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> gene_length = ini.get("gene_length");
        Map<String, String> base_value = ini.get("base_value");
        int baseVal = Integer.parseInt(base_value.get("base"));
        int genotypefitnessValue=0;
        int gene_fitness_value=0;
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



}
