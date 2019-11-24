package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;

import java.util.ArrayList;

public class GaSolverMINE {
    private final int anzahlLoesungen;

    /*
     * hier können Parameter des GA angegeben werden z.B. PopulationsGroesse,
     * IterationenAnzahl
     */

    public GaSolverMINE(int anzahlLoesungen) {
        this.anzahlLoesungen = anzahlLoesungen;
    }

    public ProductionSchedule solve(Instance instance) {
        Individual.firstLastPeriodsBerechnen(instance);
        Individual.mutationsWahrscheinlichkeit();

        //Population erstellen
        int populationsGröße = 200;

        ArrayList<Individual> population = new ArrayList<>();
        for (int i = 0; i < populationsGröße; i++) {
            population.add(new Individual(instance));
            population.get(i).initRandom();
            population.get(i).decoding(instance);
            population.get(i).evaluate();
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

        //Schritte aufschreiben und folgendes Verändern
        Individual elter, child; //eigentlich n Individuen

        //macht nur einen Child durch: -> Mutation elter -> Kind
        elter = new Individual(instance);
        elter.initRandom(); //mit Nullen und Einsen in Individuum f�llen : Gene berechnen
        elter.decoding(instance); //Berechnung ph�notyp : L�sung durch decodieren berechnen
        elter.evaluate(); //Fitness der L�sung berechnen

        //z.B 40 L�sungen: pro L�sung child erstellen
        for (int i = 1; i < anzahlLoesungen; i++) {
            //mit while ersetzen
            // erlaubte Schleifendurchl�ufe: 400.000 / anz : danach break;
            child = new Individual(instance);
            child.reproduce(elter); //elternteil reproduzieren (Gene): es entsteht eine Kopie der Eltern
            child.mutate(); //Gene werden mutiert: aus 1 wird 0
            child.decoding(instance); //Gene werden decodiert
            child.evaluate(); //Kind wird bewertet
            if (child.getFitness() < elter.getFitness()) { //wollen minimieren: also kleiner Verbesserung
                //oder <= (das ist die Frage f�r uns)
                if (child.getFitness() < elter.getFitness()) {
                    //System.out.println(i + " " + elter.getFitness());	//in jeder Iteration zeige beste L�sung
                }
                elter = child;
            }
        }
        elter.ausgabe(instance);
        return elter.getPhaenotype();
    }
    public void selektion(){

    }
    public void crossover(){

    }

    public void mutation(){

    }
}
