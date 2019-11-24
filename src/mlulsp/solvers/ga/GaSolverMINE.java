package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;

import java.util.ArrayList;

public class GaSolverMINE {
    private final int anzahlLoesungen; //wofür brauche ich dich?????????

    public GaSolverMINE(int anzahlLoesungen) {
        this.anzahlLoesungen = anzahlLoesungen;
    }

    public ProductionSchedule solve(Instance instance) {
        Individual.firstLastPeriodsBerechnen(instance);
        Individual.mutationsWahrscheinlichkeit();
        //beste Lösung
        double bestFitness = 999999999;

        //Population erstellen & Auswerten
        int populationsGröße = 200;

        ArrayList<Individual> population = new ArrayList<>();
        for (int i = 0; i < populationsGröße; i++) {
            population.add(new Individual(instance));
            population.get(i).initRandom();
            population.get(i).decoding(instance);
            population.get(i).evaluate();

            double fitness =  population.get(i).getFitness();
            if (i == 0){ bestFitness = fitness; }
            if (fitness < bestFitness){ bestFitness = fitness;} //suche nach minimierter Fitness
        }

        //Kinder zeugen
        int terminationskriterium = 0;
        int anzahlPopulation = 0;

        while(terminationskriterium < 400000){ // es dürfen nur 400.000 Individuuen pro Optimierungslauf erstellt werden
            while(!(anzahlPopulation < populationsGröße)){ //beenden wenn gleiche Populationsgröße erreicht ist
                //Selektieren 2er Eltern

                //Crossover - Rekombination der Eltern

                //Mutation - beider Nachkommen


            }
        }
        //return den Phänotyp vom Typ ProductionSchedule
        return phänotypBestFitness;
    }
    public void selektion(){

    }
    public void crossover(){
        //ist schon in Individual
    }

    public void mutation(){
        //mutate() von Individual auf beide Nachkommen anwenden
    }
}
