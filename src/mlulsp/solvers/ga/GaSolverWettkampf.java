package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.solvers.Solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GaSolverWettkampf implements Solver {
    //Eigene zusetzende Entscheidungsvariablen
    private final int populationsGroesse = 200;
    private final int anzahlKeepDelete = 20; //replaceDeleteNLast: die anzahl der Individuuen der Eltern die man behalten möchte und von kids löscht
    private final int selektionsDruck = 4; //ab 4 //replaceWettkampf:  wie viele in den matingpool gelangen sollen, also die höchste Anzahl an Eltern die reproduzieren können

    private double bestFitness = 999999999; //beste Lösung
    private int anzahlIndividuenGes = 0; //ist Anzahl der Individuuen, von denen die Fitness berechnet wurde
    private final int anzahlLoesungen;

    public GaSolverWettkampf(int anzahlLoesungen) {
        this.anzahlLoesungen = anzahlLoesungen;
    }

    public ProductionSchedule solve(Instance instance) {
        Individual.firstLastPeriodsBerechnen(instance);
       // Individual.mutationsWahrscheinlichkeit(); //auskommentiert Da Individual finale Mutationsws jetzt besitzt

        Individual indBestFitness = new Individual(instance);

        //Population erstellen & Auswerten
        ArrayList<Individual> populationEltern = new ArrayList<>();
        for (int i = 0; i < populationsGroesse; i++) {
            populationEltern.add(new Individual(instance));
            populationEltern.get(i).initRandom();
            populationEltern.get(i).decoding(instance);
            populationEltern.get(i).evaluate();

            double fitness = populationEltern.get(i).getFitness();
            if (i == 0){ bestFitness = fitness; indBestFitness = populationEltern.get(0);}
            indBestFitness = minimizeBestFitness(populationEltern.get(i), indBestFitness);
        }
        anzahlIndividuenGes += populationsGroesse;

        //Kinder zeugen
        int generation =0;
        while(anzahlIndividuenGes < anzahlLoesungen){ // es dürfen nur 400.000 Individuuen pro Optimierungslauf erstellt werden
            ArrayList<Individual> selektierteEltern = new ArrayList<>(); //Damit bei replace() nicht schon selektierte Individuuen behalten werden
            ArrayList<Individual> populationKids = new ArrayList<>();
            //matingpool befüllen
            ArrayList<Individual> matingpool = new ArrayList<>();
            matingpool = getMatingpool(populationEltern, matingpool, selektionsDruck);

            while(populationKids.size() < populationsGroesse){ //beenden wenn gleiche Populationsgröße erreicht ist : AUFPASSEN; wenn populationsgröße ungerade, muss dies angepasst werden
                //Selektieren 2er Eltern
                ArrayList<Individual> selektierteIndividuuen = selektionWettkampf(matingpool);
                Individual mama = selektierteIndividuuen.get(0);
                Individual papa = selektierteIndividuuen.get(1);
                selektierteEltern.add(mama);
                selektierteEltern.add(papa);

                //Crossover bzw. Rekombination der Eltern
                ArrayList<Individual> kids = templateCrossover(mama, papa, instance);

                //Mutation - beider Nachkommen
                myMutation(kids.get(0));
                myMutation(kids.get(1));

                //zur neuen Population hinzufügen
                populationKids.add(kids.get(0));
                populationKids.add(kids.get(1));

            }
            //decode und evaluate neue Population
            //dabei in der Methode schon nach neuer bestLösung gesucht
            indBestFitness = decodeKids(populationKids, instance, indBestFitness);

            //Replace Eltern mit kids
            ArrayList<Individual> newGeneration = replaceDeleteNlast(populationKids, populationEltern, anzahlKeepDelete, selektierteEltern);
            populationKids.clear();
            matingpool.clear();

            Collections.copy(populationEltern, newGeneration);
            newGeneration.clear();

            anzahlIndividuenGes += populationsGroesse;
            generation++;
            System.out.println("Generation "+ generation+ " ende " + indBestFitness.getFitness());
        }

        indBestFitness.ausgabe(instance);

        return indBestFitness.getPhaenotype(); //return den Phänotyp vom Typ ProductionSchedule
    }

    private ArrayList<Individual> getMatingpool(ArrayList<Individual> populationEltern, ArrayList<Individual> matingpool, int selektionsdruck){
        ArrayList<Individual> populationElternCopy = new ArrayList<>(populationEltern);
        //schlechteste Fitness unten: größte Fitness
        Comparator<Individual> compareByFitness = (Individual o1, Individual o2) -> Double.compare(o1.getFitness(), o2.getFitness());
        Collections.sort(populationElternCopy, compareByFitness);

        for (int i = (populationElternCopy.size() - 1); i >= selektionsdruck ; i--) {
            populationElternCopy.remove(i);
        }
        for (Individual ind: populationElternCopy) {
            matingpool.add(ind);
        }
        return matingpool;
    }
    private int indexRandom(int obereGrenze){
        int selektierterIndex = (int) (Math.random() * obereGrenze) -1;
        if (selektierterIndex < 0){ selektierterIndex =0;}
        return selektierterIndex;
    }
    private ArrayList<Individual> selektionWettkampf(ArrayList<Individual> matingpool){
        int obereGrenze = matingpool.size();
        int anzahlKinder = 2;
        ArrayList<Individual> selektierteIndividuuen = new ArrayList<>();
        int selektierterIndex1 =0;
        for (int i = 0; i < anzahlKinder ; i++) {
            int selektierterIndex = indexRandom(obereGrenze);
            if(i ==0 ){ selektierterIndex1 = selektierterIndex; }
            if(i ==1 && selektierterIndex1 == selektierterIndex) { i = 0; selektierterIndex = indexRandom(obereGrenze);}
            else selektierteIndividuuen.add(matingpool.get(selektierterIndex));
        }
        return selektierteIndividuuen;
    }

    //Crossover ist auch in Individual von Homberger
    private ArrayList<Individual> templateCrossover(Individual mama, Individual papa, Instance instance){
        //Ergebnis sind 2 Kinder
        Individual schwester = new Individual(instance);
        Individual bruder = new Individual(instance);
        schwester.initRandom(); //Da pk: unter umständen nicht alles in der Matrix (genotyp) befüllt
        bruder.initRandom();

        //Erstellung eines Templates
        Individual template = new Individual(instance);
        template.initRandom();

        for (int zeile = 0; zeile < template.getGenotype().length ; zeile++) {
            for (int spalte = 0; spalte < template.getGenotype()[zeile].length ; spalte++) {
                if(Math.random() < Individual.pK){ //Kreuzungswahrscheinlichkeit
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
        ArrayList<Individual> kids = new ArrayList<>();
        kids.add(schwester);
        kids.add(bruder);
        return kids;
    }

    //es gibt auch eine Mutate() von Homberger in Individual
    //verwirrend mit firstPeriodforItems und lastPeriodForItems
    private void myMutation(Individual ind){
        for (int zeile = 0; zeile < ind.getGenotype().length ; zeile++) {
            for (int spalte = 0; spalte < ind.getGenotype()[zeile].length ; spalte++) {
                if (Math.random() <= Individual.pMut ){
                    if(ind.getGenotype()[zeile][spalte] == 1) ind.getGenotype()[zeile][spalte] = 0;
                    else                                      ind.getGenotype()[zeile][spalte] = 1;
                }

            }
        }
    }

    //suche nach minimierter Fitness
    private Individual minimizeBestFitness(Individual ind, Individual indBestFitness){
        double fitness = ind.getFitness();
        if (fitness < bestFitness){
            bestFitness = fitness;
            indBestFitness = ind;
        }
        return indBestFitness;
    }

    //decode und evaluate neue Generation
    // dabei neue bestLösung suchen
    private Individual decodeKids(ArrayList<Individual> populationKids, Instance instance, Individual indBestFitness){
        for (Individual ind: populationKids) {
            ind.decoding(instance);
            ind.evaluate();

            indBestFitness = minimizeBestFitness(ind, indBestFitness);
        }
        return indBestFitness;
    }

    private int check(ArrayList<Integer> zahlenBesetzt, ArrayList<Individual> selektierteEltern, ArrayList<Individual> populationEltern){
        int indexInd = (int) (Math.random() * populationsGroesse); //Zufallszahl zw. 0-populationsgröße-1 //deswegen keine +1 am ende
        Individual ind = populationEltern.get(indexInd);
        boolean x = selektierteEltern.contains(ind);
        if (!zahlenBesetzt.contains(indexInd) && !selektierteEltern.contains(ind)) {
            zahlenBesetzt.add(indexInd);
            return indexInd;
        }else{
            return check(zahlenBesetzt, selektierteEltern, populationEltern);
        }
    }
    //Delete 75% der Eltern und behalte 25% (Random)
    //Delete 25% der Kinder und behalte 75% (delete schlechteste)
    //andere Verhältnisse vllt besser?
    private ArrayList<Individual> replaceDeleteNlast(ArrayList<Individual> populationKids, ArrayList<Individual> populationEltern, int anzahlKeepDelete, ArrayList<Individual> selektierteEltern){
        ArrayList<Individual> newGeneration = new ArrayList<>();
        ArrayList<Integer> zahlenBesetzt = new ArrayList<>();

        //50 eltern (25%) sollen in newGeneration übernommen werden
        for (int i = 0; i < anzahlKeepDelete ; i++) {
            int indexInd = check(zahlenBesetzt, selektierteEltern, populationEltern);
            newGeneration.add(populationEltern.get(indexInd));
        }

        //sort populationKids: schlechteste Fitness unten: größte Fitness
        Comparator<Individual> compareByFitness = (Individual o1, Individual o2) -> Double.compare(o1.getFitness(), o2.getFitness());
        Collections.sort(populationKids, compareByFitness);

        //kids 25% / 50 schlechtesten löschen
        int löschen = populationsGroesse - anzahlKeepDelete;
        for (int i = (populationKids.size() - 1); i >= löschen ; i--) {
            populationKids.remove(i);
        }

        //restlichen Kids zur neuen Generation hinzufügen
        newGeneration.addAll(populationKids);
        if (newGeneration.size() != populationEltern.size()){
            System.out.println("FEEEEEEEHLER ungleiche Populationsgröße: size: " + newGeneration.size());
        }
        return newGeneration;
    }
}
