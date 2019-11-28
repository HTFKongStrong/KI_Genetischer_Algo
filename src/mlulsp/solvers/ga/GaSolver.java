package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.solvers.Solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GaSolver implements Solver {
    //Eigene zusetzende Entscheidungsvariablen
    private final int populationsGroesse = 200;
    private final int anzahlKeepDelete = 0; //replaceDeleteNLast: die anzahl der Individuuen der Eltern die man behalten möchte und von kids löscht

    private double bestFitness = 999999999; //beste Lösung
    private int anzahlIndividuenGes = 0; //ist Anzahl der Individuuen, von denen die Fitness berechnet wurde
    private final int anzahlLoesungen;

    public GaSolver(int anzahlLoesungen) {
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
        ArrayList<Individual> populationKids = new ArrayList<>();
        int generation =0;
        while(anzahlIndividuenGes < anzahlLoesungen){ // es dürfen nur 400.000 Individuuen pro Optimierungslauf erstellt werden
            ArrayList<Individual> selektierteEltern = new ArrayList<>(); //Damit bei replace() nicht schon selektierte Individuuen behalten werden
            while(populationKids.size() < populationsGroesse){ //beenden wenn gleiche Populationsgröße erreicht ist : AUFPASSEN; wenn populationsgröße ungerade, muss dies angepasst werden
                //Selektieren 2er Eltern
                //müssen nicht zu anzahlIndividuenGes hinzugefügt werden, da oben schon hinzugefügt
                ArrayList<Integer> elternIndex = selektionRoulette(populationEltern);
                int groese1 = elternIndex.get(0);
                int groese2 = elternIndex.get(1);

                if(groese2 > populationsGroesse || groese1 > populationsGroesse){
                    System.out.println("FEEEEEEEEHLER Index ist größer");
                }
                Individual mama = populationEltern.get(groese1);
                Individual papa = populationEltern.get(groese2);
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
            populationEltern.clear();
            populationKids.clear();
            populationEltern = newGeneration;

            anzahlIndividuenGes += populationsGroesse;
            generation++;
            System.out.println("Generation "+ generation+ " ende " + indBestFitness.getFitness());
        }

        indBestFitness.ausgabe(instance);

        return indBestFitness.getPhaenotype(); //return den Phänotyp vom Typ ProductionSchedule
    }

    private ArrayList<Integer> selektionRoulette(ArrayList<Individual> populationEltern){
        double gesamtFitness = 0; //KANN SEIN DASS DER WERT zu groß ist, und ein Error dadurch entsteht -> größeren Datentyp finden
        //BigDecimal wenn zu klein

        ArrayList<Double> maxZahlenInd = new ArrayList<>(); //index = Individuum; gespeichert = maximale Zahlen (addiert mit der Vorherigen)
        ArrayList<Integer> selectedInd = new ArrayList<>();

        //Berechnung Gesamtfitness
        for (Individual ind: populationEltern) {
            gesamtFitness += ind.getFitness();
        }

        //Berechnung Verhältnisse & in ArrayList speichern
        double add = 0;
        for (Individual ind: populationEltern) {
            double verhaeltnis = ind.getFitness()/gesamtFitness; //Verhältnis = wert zw. 0 und 1
            //Max zahlen ber. und in ArrayList speichern
            double max = (verhaeltnis * 100); //double da -> Möglicher Fehler durch Rundung/casten? Manche Zahlen nicht besetzt? (sind dann Zahlen kurz vor & nach 1000)
            add += max;
            maxZahlenInd.add(add);
        }
        //Berechnung Zufallszahlen: zw. 0-1000
//        int zufallszahl1 = (int)(Math.random() * 100) + 1;
//        int zufallszahl2 = (int)(Math.random() * 100) + 1;

        //um Fehler s.o zu vermeiden (zahlen !=100)
        double obereGrenze = add;
        double zufallszahl1 = (Math.random() * obereGrenze) ;
        double zufallszahl2 = (Math.random() * obereGrenze) ;

        //get Index des Individuums das die Zufallszahl trifft
        int indexZufallszahl1 = getIndRoulette(zufallszahl1, maxZahlenInd);
        int indexZufallszahl2 = getIndRoulette(zufallszahl2, maxZahlenInd);

        //Verhindern dass das gleiche Individuum zweimal selektiert wird: Zufallszahl muss nochmal berechnet werden
        while (indexZufallszahl2 == indexZufallszahl1){
            zufallszahl2 = (Math.random() * obereGrenze) ;
            indexZufallszahl2 = getIndRoulette(zufallszahl2, maxZahlenInd);
        }
        //ArrayIndexOutofBounds vermeiden
        if (indexZufallszahl1 == populationsGroesse){ indexZufallszahl1 = populationsGroesse - 1; }
        if (indexZufallszahl2 == populationsGroesse){ indexZufallszahl2 = populationsGroesse - 1; }

        selectedInd.add(indexZufallszahl1);
        selectedInd.add(indexZufallszahl2);

        return selectedInd;

    }
    //Get Index Individuum das die Zufallszahl trifft
    private int getIndRoulette(double zufallszahl, ArrayList<Double> maxZahlenInd ){
        int indexIndividuum = 0;
        double maxZahlIndVorherig = 0;
        if (!(zufallszahl == 0)){
            for (Double maxZahlInd: maxZahlenInd) {
                if (zufallszahl == maxZahlInd){ break; }
                if (zufallszahl > maxZahlIndVorherig && zufallszahl < maxZahlInd){ break;}
                indexIndividuum++;
                maxZahlIndVorherig = maxZahlInd;
            }
        }
        return indexIndividuum;
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

        //sort populationKids: schlechteste Fitness oben: größte Fitness
        Comparator<Individual> compareByFitness = (Individual o1, Individual o2) -> Double.compare(o1.getFitness(), o2.getFitness());
        Collections.sort(populationKids, compareByFitness.reversed());

        //kids 25% / 50 schlechtesten löschen
        for (int i = 0; i < anzahlKeepDelete ; i++) {
            populationKids.remove(i);
        }

        //restlichen Kids zur neuen Generation hinzufügen
        newGeneration.addAll(populationKids);
        if (newGeneration.size() != populationEltern.size()){
            System.out.println("FEEEEEEEHLER ungleiche Populationsgröße");
        }
        return newGeneration;
    }
}
