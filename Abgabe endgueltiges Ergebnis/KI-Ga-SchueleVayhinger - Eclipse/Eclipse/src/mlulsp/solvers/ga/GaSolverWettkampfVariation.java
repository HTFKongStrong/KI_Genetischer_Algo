package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.solvers.Solver;
import mlulsp.solvers.gaMethods.*;


public class GaSolverWettkampfVariation implements Solver {
    //Eigens zusetzende Entscheidungsvariablen: nur zu Beginn, entwickeln dann Eigenleben
    private final int populationsGroesse = 200; //NUR bis 500, sonst exception
    private final int selektionsDruck = 6; //ab 4!!!! //replaceWettkampf:  wie viele in den matingpool gelangen sollen, also die hoechste Anzahl an Eltern die reproduzieren koennen
    private final int anzahlKeepDelete = 10; //replaceDeleteNLast: die anzahl der Individuuen der Eltern die man behalten moechte und von kids loescht
    private final double pMut = 0.7;//Mutationswahrscheinlichkeit Individuen
    private final double prozentVariationPmut = 0.5; // 0,0 - 1,0 // ab welchem Verhaeltnis von anzahlLoesungen soll pMut erhoeht werden
    private final double pK = 0.5; //Kreuzungswahrscheinlichkeit

    //Variablen nicht veraendern
    private double bestFitness = Double.MAX_VALUE; //beste Loesung
    private int anzahlIndividuenGes = 0; //ist Anzahl der Individuuen, von denen die Fitness berechnet wurde
    private final int anzahlLoesungen;

    public GaSolverWettkampfVariation(int anzahlLoesungen) {
        this.anzahlLoesungen = anzahlLoesungen;
    }

    public ProductionSchedule solve(Instance instance) {
        Individual.firstLastPeriodsBerechnen(instance);
        //Erstmal VariablenInput einlesen
        //Extras.readConsole(populationsGroesse, pMut, prozentVariationPmut, pK, selektionsDruck, anzahlKeepDelete);

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

        //Variablen fuer Mutation initialisieren
        double pMutVar = pMut;
        int populationsGroesseMutVar = (int) (prozentVariationPmut * anzahlLoesungen);
        double pMutHilf = 1 - pMutVar;

        int generation = 0;
        //Kinder zeugen
        while(anzahlIndividuenGes < anzahlLoesungen){ // es duerfen nur 400.000 Individuuen pro Optimierungslauf erstellt werden
            Individual[] selektierteEltern = new Individual[populationsGroesse]; //Damit bei replace() nicht schon selektierte Individuuen behalten werden
            int sizeSelekEltern = 0;

            Individual[] populationKids = new Individual[populationsGroesse];
            int sizePopKids = 0;

            //matingpool befuellen
            Individual[] matingpool = new Individual[selektionsDruck];
            matingpool = SelektionWettkampf.getMatingpool(popEltern, matingpool, selektionsDruck);

            while(sizePopKids < populationsGroesse && anzahlIndividuenGes < anzahlLoesungen){ //beenden wenn gleiche Populationsgroesse erreicht ist
                //Selektieren 2er Eltern
                Individual[] selektierteIndividuuen = SelektionWettkampf.selektionWettkampf(matingpool);
                Individual mama = selektierteIndividuuen[0];
                Individual papa = selektierteIndividuuen[1];
                selektierteEltern[sizeSelekEltern] = mama;
                selektierteEltern[sizeSelekEltern +1] = papa;
                sizeSelekEltern +=2;

                //Crossover bzw. Rekombination der Eltern
                Individual[] kids = Crossover.templateCrossover(mama, papa, instance, pK);

                //Mutation - beider Nachkommen
                Mutation.swapMutation(kids[0], pMutVar);
                Mutation.swapMutation(kids[1], pMutVar);

                //zur neuen Population hinzuf�gen
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

            pMutVar = Mutation.variationPmut(populationsGroesseMutVar, pMutVar, pMutHilf, anzahlIndividuenGes, pMut);

            generation++;
            //System.out.println("Generation "+ generation+ " ende " + indBestFitness.getFitness());
        }

        indBestFitness.ausgabe(instance);

        return indBestFitness.getPhaenotype(); //return den Phaenotyp vom Typ ProductionSchedule
    }
}
