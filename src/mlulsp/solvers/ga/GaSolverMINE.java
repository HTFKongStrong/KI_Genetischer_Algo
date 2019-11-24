package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GaSolverMINE {
    private final int anzahlLoesungen; //wofür brauche ich dich?????????

    public GaSolverMINE(int anzahlLoesungen) {
        this.anzahlLoesungen = anzahlLoesungen;
    }

    public ProductionSchedule solve(Instance instance) {
        Individual.firstLastPeriodsBerechnen(instance);
       // Individual.mutationsWahrscheinlichkeit(); //auskommentiert Da Individual finale Mutationsws jetzt besitzt
        //beste Lösung
        double bestFitness = 999999999;

        //Population erstellen & Auswerten
        int populationsGröße = 200;

        ArrayList<Individual> populationEltern = new ArrayList<>();
        for (int i = 0; i < populationsGröße; i++) {
            populationEltern.add(new Individual(instance));
            populationEltern.get(i).initRandom();
            populationEltern.get(i).decoding(instance);
            populationEltern.get(i).evaluate();

            double fitness =  populationEltern.get(i).getFitness();
            if (i == 0){ bestFitness = fitness; }
            if (fitness < bestFitness){ bestFitness = fitness;} //suche nach minimierter Fitness
        }

        //Kinder zeugen
        int terminationskriterium = 0;
        int anzahlPopulation = 0;

        while(terminationskriterium < 400000){ // es dürfen nur 400.000 Individuuen pro Optimierungslauf erstellt werden
            while(!(anzahlPopulation < populationsGröße)){ //beenden wenn gleiche Populationsgröße erreicht ist
                //Selektieren 2er Eltern
                ArrayList<Integer> elternIndex = selektionRoulette(populationEltern);
                Individual mama = populationEltern.get(0);
                Individual papa = populationEltern.get(1);

                //Crossover bzw. Rekombination der Eltern
                ArrayList<Individual> kids = crossoverTemplateCrossover(mama, papa, instance);

                //Mutation - beider Nachkommen
                myMutation(kids.get(0));
                myMutation(kids.get(1));

                //Listen leeren?
            }
        }
        //return den Phänotyp vom Typ ProductionSchedule
        return phänotypBestFitness;
    }
    public ArrayList<Integer> selektionRoulette(ArrayList<Individual> populationEltern){
        double GesamtFitness = 0; //KANN SEIN DASS DER WERT zu groß ist, und ein Error dadurch entsteht
        ArrayList<Double> verhältnisIndividuum = new ArrayList<>();
        ArrayList<Integer> maxZahlenInd = new ArrayList<>(); //index = Individuum; gespeichert = maximale Zahlen (addiert mit der Vorherigen)
        ArrayList<Integer> selectedInd = new ArrayList<>();

        //Berechnung Gesamtfitness
        for (int i = 0; i < populationEltern.size(); i++){
            GesamtFitness +=populationEltern.get(i).getFitness();
        }

        //Berechnung Verhältnisse & in ArrayList speichern
        for (int i = 0; i < populationEltern.size(); i++) {
            double verhältnis = populationEltern.get(i).getFitness()/GesamtFitness; //Verhältnis = wert zw. 0 und 1
            verhältnisIndividuum.add(verhältnis);
            //Max zahlen ber. und in ArrayList speichern
            int add=0;
            int max = (int) verhältnis * 1000; //Möglicher Fehler durch Rundung? Manche Zahlen nicht besetzt? (sind dann Zahlen kurz vor 1000)
            add += max;
            maxZahlenInd.add(add);
        }
        //Berechnung Zufallszahlen
        int zufallszahl1 = (int)(Math.random() * 1000) + 1;
        int zufallszahl2 = (int)(Math.random() * 1000) + 1;

        //get Index Individuum das die Zufallszahl trifft
        int indZufallszahl1 = getIndRoulette(zufallszahl1, maxZahlenInd);
        int indZufallszahl2 = getIndRoulette(zufallszahl2, maxZahlenInd);

        selectedInd.add(indZufallszahl1);
        selectedInd.add(indZufallszahl2);

        return selectedInd;

    }
    //Get Index Individuum das die Zufallszahl trifft
    public int getIndRoulette(int zufallszahl, ArrayList<Integer> maxZahlenInd ){
        int indexIndividuum = 0;
        int maxZahlIndVorherig = 0;
        if (!(zufallszahl == 0)){
            for (Integer maxZahlInd: maxZahlenInd) {
                if (zufallszahl == maxZahlInd){ break; }
                if (zufallszahl > maxZahlIndVorherig && zufallszahl < maxZahlInd){ break;}
                indexIndividuum++;
                maxZahlIndVorherig = maxZahlInd;
            }
        }
        return indexIndividuum;
    }

    //Kreuzungswahrscheinlichkeit in Individual nicht erwähnt
    //ist schon in Individual
    public ArrayList<Individual> crossoverTemplateCrossover(Individual mama, Individual papa, Instance instance){
        //Ergebnis sind 2 Kinder
        Individual schwester = new Individual(instance);
        Individual bruder = new Individual(instance);

        //Erstellung eines Templates
        //vllt nochmal ohne erstellung eines Individuums, da begrenzte Anzahl zur Verfügung
        Individual template = new Individual(instance);
        template.initRandom();

        for (int zeile = 0; zeile < template.getGenotype().length ; zeile++) {
            for (int spalte = 0; spalte < template.getGenotype()[zeile].length ; spalte++) {
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
        ArrayList<Individual> kids = new ArrayList<>();
        kids.add(schwester);
        kids.add(bruder);
        return kids;
    }

    //es gibt auch eine Mutate() von Homberger in Individual
    //verwirrend mit firstPeriodforItems und lastPeriodForItems
    public void myMutation(Individual ind){
        for (int zeile = 0; zeile < ind.getGenotype().length ; zeile++) {
            for (int spalte = 0; spalte < ind.getGenotype()[zeile].length ; spalte++) {
                if (Math.random() <= Individual.pMut ){
                    if(ind.getGenotype()[zeile][spalte] == 1) ind.getGenotype()[zeile][spalte] = 0;
                    else                                      ind.getGenotype()[zeile][spalte] = 1;
                }

            }
        }
    }
}
