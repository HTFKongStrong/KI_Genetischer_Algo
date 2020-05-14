package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.solvers.Solver;
import mlulsp.solvers.gaMethods.*;

public class GaSolverSelektion implements Solver {
    //Eigene zusetzende Entscheidungsvariablen
    private final int populationsGroesse = 100; //NUR bis 500, sonst exception
    private final int anzahlKeepDelete = 10; //replaceDeleteNLast: die Anzahl der Individuuen der Eltern die man behalten moechte und von kids loescht
    private final double pMut = 0.3;
    private final double pK = 0.7;

    //nicht veraendern
    private double bestFitness = Double.MAX_VALUE; //beste Loesung
    private int anzahlIndividuenGes = 0; //ist Anzahl der Individuuen, von denen die Fitness berechnet wurde
    private final int anzahlLoesungen;

    public GaSolverSelektion(int anzahlLoesungen) {
        this.anzahlLoesungen = anzahlLoesungen;
    }

    public ProductionSchedule solve(Instance instance) {
        Individual.firstLastPeriodsBerechnen(instance);
       // Individual.mutationsWahrscheinlichkeit(); //auskommentiert Da jetzt finale Mutationsws 

        Individual indBestFitness = new Individual(instance);

        //Population erstellen & Auswerten
        Individual[] popEltern = new Individual[populationsGroesse];

        for (int i = 0; i < populationsGroesse; i++) {
            popEltern[i] = new Individual(instance);
            popEltern[i].initRandom();
            popEltern[i].decoding(instance);
            popEltern[i].evaluate();

            double fitness = popEltern[i].getFitness();
            if (i==0) {bestFitness = fitness; indBestFitness = popEltern[0];}
            indBestFitness = SearchMin.minimizeBestFitness(popEltern[i], indBestFitness, bestFitness);
            bestFitness = indBestFitness.getFitness();
        }
        anzahlIndividuenGes += populationsGroesse;

        //Kinder zeugen
        int generation =0;
        while(anzahlIndividuenGes < anzahlLoesungen){ // es duerfen nur 400.000 Individuuen pro Optimierungslauf erstellt werden
            Individual[] selektierteEltern = new Individual[populationsGroesse]; //Damit bei replace() nicht schon selektierte Individuuen behalten werden
            int sizeSelekEltern = 0;

            Individual[] populationKids = new Individual[populationsGroesse];
            int sizePopKids = 0;

            while(sizePopKids < populationsGroesse && anzahlIndividuenGes < anzahlLoesungen){ //beenden wenn gleiche Populationsgroesse erreicht ist
                //Selektieren 2er Eltern
                //muessen nicht zu anzahlIndividuenGes hinzugefuegt werden, da oben schon hinzugefuegt
                Integer[] elternIndex = SelektionRoulette.selektionRoulette(popEltern, populationsGroesse);
                int index1 = elternIndex[0];
                int index2 = elternIndex[1];
                Individual mama = popEltern[index1];
                Individual papa = popEltern[index2];
                selektierteEltern[sizeSelekEltern] = mama;
                selektierteEltern[sizeSelekEltern + 1] = papa;
                sizeSelekEltern +=2;

                //Crossover bzw. Rekombination der Eltern
                Individual[] kids = Crossover.templateCrossover(mama, papa, instance, pK);

                //Mutation - beider Nachkommen
                Mutation.swapMutation(kids[0], pMut);
                Mutation.swapMutation(kids[1], pMut);

                //zur neuen Population hinzufügen
                populationKids[sizePopKids] = kids[0];
                anzahlIndividuenGes +=1;
                if (anzahlIndividuenGes >= anzahlLoesungen){
                    break;
                }
                populationKids[sizePopKids+1] = kids[1];

                anzahlIndividuenGes += 1;
                sizePopKids +=2;
            }
            //decode und evaluate neue Population
            //dabei in der Methode schon nach neuer bestLoesung gesucht
            indBestFitness = SearchMin.decodeKids(populationKids, sizePopKids, instance, indBestFitness, bestFitness);
            bestFitness = indBestFitness.getFitness();

            //Replace Eltern mit kids
            Individual[] newGeneration = Replace.replaceDeleteNlast(populationKids, popEltern, anzahlKeepDelete, selektierteEltern, populationsGroesse);

            popEltern = newGeneration.clone();

            generation++;
            //System.out.println("Generation "+ generation+ " ende " + indBestFitness.getFitness());
        }

        indBestFitness.ausgabe(instance);

        return indBestFitness.getPhaenotype(); //return den Phaenotyp vom Typ ProductionSchedule
    }
}
