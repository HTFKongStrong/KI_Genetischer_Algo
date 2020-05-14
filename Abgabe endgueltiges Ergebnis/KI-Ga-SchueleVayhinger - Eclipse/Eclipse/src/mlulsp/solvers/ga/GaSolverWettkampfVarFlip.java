package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.solvers.gaMethods.Crossover;
import mlulsp.solvers.gaMethods.Mutation;
import mlulsp.solvers.gaMethods.Replace;
import mlulsp.solvers.gaMethods.SearchMin;
import mlulsp.solvers.gaMethods.SelektionWettkampf;

public class GaSolverWettkampfVarFlip {
	 //Eigens zusetzende Entscheidungsvariablen: nur zu Beginn, entwickeln dann Eigenleben
    private final int populationsGroesse = 320; //NUR bis 500, sonst exception; da Nullpointer muss durch AnzahLoesungen teilbar sein
    private final int selektionsDruck = 10; //ab 4!!!! //replaceWettkampf:  wie viele in den matingpool gelangen sollen, also die hoechste Anzahl an Eltern die reproduzieren koennen
    private final int anzahlKeepDelete = 10; //replaceDeleteNLast: die anzahl der Individuuen der Eltern die man behalten moechte und von kids loescht
    private final double prozentVariationPmut = 0.05; // 0,0 - 1,0 // ab welchem Verhaeltnis von anzahlLoesungen soll pMut erhoeht werden
    private final double pK = 0.7; //Kreuzungswahrscheinlichkeit

    //Variablen nicht veraendern
    private double bestFitness = Double.MAX_VALUE; //beste Loesung
    private int anzahlIndividuenGes = 0; //ist Anzahl der Individuuen, von denen die Fitness berechnet wurde
    private final int anzahlLoesungen;

    public GaSolverWettkampfVarFlip(int anzahlLoesungen) {
        this.anzahlLoesungen = anzahlLoesungen;
    }

    public ProductionSchedule solve(Instance instance) {
        Individual.firstLastPeriodsBerechnen(instance);
        Individual.mutationsWahrscheinlichkeit();
        double pMut = Individual.pMut;
   
        Individual indBestFitness = new Individual(instance);
        
        int generation = 0;
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
        generation++;
        //System.out.println("Generation "+ generation+ " ende " + indBestFitness.getFitness());
        
        //Variablen fuer Mutation initialisieren
        double pMutVar = pMut;
        int populationsGroesseMutVar = (int) (prozentVariationPmut * anzahlLoesungen);
        double pMutHilf = 1 - pMutVar;
        
        //Wechsel der Mutationsmethode nach erreichen einer bestimmten Anzahl von Individuuen
        boolean wechsel = false;
       
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
                if (!wechsel) {
                	kids[0].mutate();
                	kids[1].mutate();
                }
                if(wechsel && (generation%2==0)) {
                    Mutation.flipMutationVar(kids[0], pMutVar);
                    Mutation.flipMutationVar(kids[1], pMutVar);
                }
               
                //zur neuen Population hinzufuegen
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
            if (pMutVar != pMut) {
            	wechsel = true;
            }
            
            generation++;
            //System.out.println("Generation "+ generation+ " ende " + indBestFitness.getFitness());
        }

        indBestFitness.ausgabe(instance);

        return indBestFitness.getPhaenotype(); //return den Phaenotyp vom Typ ProductionSchedule
    }
}
