package mlulsp.solvers.gaMethods;

import mlulsp.domain.Instance;
import mlulsp.solvers.ga.Individual;

public class Crossover {
    public static Individual[] templateCrossover(Individual mama, Individual papa, Instance instance, double pK){
        //Ergebnis sind 2 Kinder
        Individual schwester = new Individual(instance);
        Individual bruder = new Individual(instance);
        schwester.reproduce(mama); //Da pk: unter umstaenden nicht alles in der Matrix (genotyp) befuellt
        bruder.reproduce(papa);

        //Erstellung eines Templates
        Individual template = new Individual(instance);
        template.initRandom();

        for (int zeile = 0; zeile < template.getGenotype().length ; zeile++) {
            for (int spalte = 0; spalte < template.getGenotype()[zeile].length ; spalte++) {
                if(Math.random() < pK){ //Kreuzungswahrscheinlichkeit
                    if (template.getGenotype()[zeile][spalte] == 1 ){
                        schwester.getGenotype()[zeile][spalte] = mama.getGenotype()[zeile][spalte];
                        bruder.getGenotype()[zeile][spalte] = papa.getGenotype()[zeile][spalte];
                    }
                    if (template.getGenotype()[zeile][spalte] == 0 ){
                        schwester.getGenotype()[zeile][spalte] = papa.getGenotype()[zeile][spalte];
                        bruder.getGenotype()[zeile][spalte] = mama.getGenotype()[zeile][spalte];
                    }
                }
            }
        }
        Individual[] kids = new Individual[2];
        kids[0] = schwester;
        kids[1] = bruder;
        return kids;
    }
}
