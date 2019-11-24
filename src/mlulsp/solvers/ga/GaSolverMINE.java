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
        Individual.mutationsWahrscheinlichkeit();
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
                Individual eltern1 = populationEltern.get(0);
                Individual eltern2 = populationEltern.get(1);

                //Crossover - Rekombination der Eltern

                //Mutation - beider Nachkommen


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

    public void crossover(){
        //ist schon in Individual
    }

    public void mutation(){
        //mutate() von Individual auf beide Nachkommen anwenden
    }
}
