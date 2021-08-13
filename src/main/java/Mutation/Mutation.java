package Mutation;

import org.ini4j.Ini;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
/**
 *
 * @author mrigesh
 */
public class Mutation {

    public static HashMap<Integer, Color> mutationColor = new HashMap<>();
    public static double previousHostFitness = 30180;
    public static double maxCurrentFitness = 0;
    public static double maxPreviousFitness = 30180;

    public static double calculateGenotypeFitness(String genotype, double infection_factor) throws IOException {
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> gene_length = ini.get("gene_length");
        Map<String, String> base_value = ini.get("base_value");
        double baseVal = Double.parseDouble(base_value.get("base"));
        double genotypefitnessValue = 0;
        double gene_fitness_value = 0;
        boolean islarger = genotype.length() > 10 ? true : false;

        char[] gene = genotype.toCharArray();
        double j = 0;
        for (double i = 0; i < genotype.length(); i++) {
            int geneLength = Integer.parseInt(gene_length.get(gene_length.keySet().toArray()[(int) j]));

            if (Character.isAlphabetic(gene[(int) i])) {

                gene_fitness_value = (3 * baseVal) * geneLength;

            } else if (Character.isDigit(gene[(int) i])) {
                if (islarger) {
                    int sum = Integer.parseInt(Character.toString(gene[(int) i])) * 10 +
                            Integer.parseInt(Character.toString(gene[(int) (i + 1)]));

                    gene_fitness_value = ((2 * baseVal)
                            + infection_factor * sum)
                            * geneLength;
                    islarger = false;
                    i = i + 2;
                } else {
                    gene_fitness_value = ((2 * baseVal)
                            + infection_factor * Integer.parseInt(Character.toString(gene[(int) i])))
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
        System.out.println("mutationColor "+mutationColor);
        return mutationColor.get(mutationCount);
    }

    public Color getCurrentVariantColor(){
        Color c=null;
        for(int i = 0;i <mutationColor.size();i++){
            c= fetchmutationColor(i);
        }
        return c;
    }

    //    Check if the mutation is variant
    public static boolean calculateMutationFactor(String genotype, double currentfitnessValue, Hashtable<String, List<String>> fitnessHashTable) throws IOException {
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> map = ini.get("default");
        double dominant_factor = Double.parseDouble(map.get("dominant_factor"));

        maxCurrentFitness = calculateGenotypeFitness(genotype, 96);

        double variantThreshold = maxPreviousFitness + (maxCurrentFitness - maxPreviousFitness) * dominant_factor;

        maxPreviousFitness = maxCurrentFitness;
        previousHostFitness = currentfitnessValue;

        if(currentfitnessValue > variantThreshold)
        {
            //insert into variant directory if mutation is variant
            insertIntoMutationList(fitnessHashTable.size());
        }
        return currentfitnessValue > variantThreshold;
    }

    public static HashMap<Integer, Color> getMutationColor() {
        return mutationColor;
    }

}
