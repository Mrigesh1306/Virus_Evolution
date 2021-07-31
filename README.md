# INFO6205_PSA_finalProject
project for program structure and algorithm course(INFO6205)


Your task in this project is to study the evolution of variants of a positive-sense single-stranded RNA virus (Links to an external site.)[14] (Links to an external site.) for example SARS-CoV-2 (Links to an external site.). However, this is an algorithms class, not a biology class, so you will concentrate mostly on the mechanisms of simulating evolution using a genetic algorithm.

Viruses, like bacteria, have a single strand of genetic material (RNA in this case). There is no true crossover involved in the reproduction of these viruses (as in sexual reproduction of eukaryotes like humans) but there is a process called recombination (RNA virus recombination (Links to an external site.)). And like any genetic material, there is also the possibility of mutations (simple copying errors).

The SARS-CoV-2 virus has a genome consisting of almost 30,000 bases (i.e. A, C, G, or T) with 10 individual genes (see Genome Browser (Links to an external site.) for more information than you could possibly use). You may assume that recombination works at the gene level but that mutation can effect any of the bases. Note that a "codon" consists of three bases and so has 64 possible values. However, there are only 20 different amino acids (see Genetic Code (Links to an external site.)). The relevant translation table (I think) is the Standard RNA Codon Table (Links to an external site.). Again, this is more information than you actually need. But, a random mutation will have a 1 in 3 probability (approx.) of changing the coded amino acid. You may assume that k-1 out of k amino acid changes will result in non-functional viruses (i.e. fitness = 0).  You get to choose k. You may assume that the one other change increases the fitness by some factor g.  And you also get to choose p the probability of a mutation occurring when a virus replicates. You will want to make this value quite small, I think.

Your task is to model each of these mechanisms and to simulate virus reproduction in a series of hosts. These potential hosts will be either (a) naive (i.e. not infected, not vaccinated), (b) previously infected, (c) vaccinated, (d) previously infected and vaccinated.  The relative proportions of the population in these groups will vary with time (your experiment should cover two years). The hosts in your experiment will live on an island that has no connection with the outside world. It has a population of 1,000 people.

Continued...

A genetic algorithm, or simulation (which is what we're doing here), works as follows:

1. start with a "seed" population, each individual having its own genotype (there is a bit of confusion between the terms genome and genotype, but here, I use genotype to mean the specific genetic code of an individual, whereas the genome describes all the possible genotypes of a species).

2. Repeat the following loop for ever (or at least until nothing interesting is happening):

i .measure the fitness of each individual in its own environment;

ii. kill off any individuals whose fitness is below some threshold;


iii. create progeny (children) from each individual by copying its genotype, The number of progeny (the fecundity), can be controlled with a configurable value: start with 1 and see how that goes.

iv. add these progeny to the general population and go back to the #1.


Fitness is a function of an individual's phenotype and its environment (see below). The phenotype is derived from the genotype by (gene) expression. For our purposes, we won't worry about the phenotype at all and we will simply take the 10 genes and determine which variant we have from those. You will create a table of variant versus environment (host) and determine the fitness from that. When recombination or mutation results in an individual with a different set of the 10 genes that you haven't seen before, you have a new variant. Assign it a fitness in your table. (Recommend you use a hash-table with variant as the key and list of fitness values (one for each of the four host types - see below).

You will need to model the fitness function of individual viruses (for our purposes, these can be called virions) in the environment (a particular host) in which they find themselves. For this, you may assume that there are two loci (genes) of the host, each with two alleles, which affect the fitness of a particular virion. Thus, the host population is made up of individuals with, as far as this simulation is concerned, four different genomes: A1, A2, B1, B2. You determine the relative proportions of these populations of the host species (the 1000 humans living on the island, in this case).

Your project isn't however, a straightforward simulation as I've described above. You also need to model the reproduction of virions a little more in line with reality. I just said that they arise as children in the inner loop. However, you should actual model the mechanism for virions to jump from one host (its environment) to another (a different environment). 

You should use the Config package in the repository to manage all of the "constants" and other values that you need to determine to start the simulation. For example, the population of the host species is 1000 as noted above but you should make that a configurable value so that you can change it if, for example, you find that your simulation is running too slow (or too fast). Config is pretty each to use. Just look at its unit tests to see how to use it.

I expect to be able to run your simulation (so your project has to be built using maven) and be careful when you set up your .gitignore file to use the Java version (git will offer you this choice when you create the new repository). If there are derived files (.class, .idea, etc.) in your repository you will lose a small number of marks. You could also make a movie out of your simulation so that we (Urvi and myself) can watch that if we can't build your project.

You must write a report showing your results and observations. Did any new variants become dominant (as the delta variant seems to be doing with COVID-19).

 

 

