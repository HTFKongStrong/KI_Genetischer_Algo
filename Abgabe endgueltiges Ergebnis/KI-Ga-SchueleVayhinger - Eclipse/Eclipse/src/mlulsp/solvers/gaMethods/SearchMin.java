package mlulsp.solvers.gaMethods;

import mlulsp.domain.Instance;
import mlulsp.solvers.ga.Individual;


public class SearchMin {
    //suche nach minimierter Fitness
    public static Individual minimizeBestFitness(Individual ind, Individual indBestFitness, double bestFitness){
        double fitness = ind.getFitness();
        if (fitness <  bestFitness){
            indBestFitness = ind;
        }
        return indBestFitness;
    }

    //Decodieren
    //decode und evaluate neue Generation
    //dabei neue bestLoesung suchen
    public static Individual decodeKids(Individual[] populationKids, int sizePopKids, Instance instance, Individual indBestFitness, double bestFitness){
        double bestFitnessC = bestFitness;
        for (int i = 0; i < sizePopKids; i++) {
            populationKids[i].decoding(instance);
            populationKids[i].evaluate();

            indBestFitness = minimizeBestFitness(populationKids[i], indBestFitness, bestFitness);
            bestFitnessC = indBestFitness.getFitness();

        }
        return indBestFitness;
    }
}
