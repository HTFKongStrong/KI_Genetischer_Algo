package mlulsp.solvers.gaMethods;

import mlulsp.solvers.ga.Individual;

import java.util.ArrayList;

public class SelektionRoulette {

    public static Integer[] selektionRoulette(Individual[] populationEltern, int populationsGroesse){
        double gesamtFitness = 0;

        Integer[] selectedInd = new Integer[2];
        Double[] maxZahlenInd = new  Double[populationsGroesse]; //index = Individuum; gespeichert = maximale Zahlen (addiert mit der Vorherigen)

        //Berechnung Gesamtfitness
        for (Individual ind: populationEltern) {
            gesamtFitness += ind.getFitness();
        }

        //Berechnung Verhaeltnisse & in ArrayList speichern
        double add = 0;
        for (int i = 0; i < populationsGroesse; i++) {
            double verhaeltnis = populationEltern[i].getFitness()/gesamtFitness; //Verhaeltnis = wert zw. 0 und 1
            //Max zahlen ber. und in ArrayList speichern
            double max = (verhaeltnis * 100); //double da -> Moeglicher Fehler durch Rundung/casten? Manche Zahlen nicht besetzt? (sind dann Zahlen kurz vor & nach 1000)
            add += max;
            maxZahlenInd[i] = add;
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

        selectedInd[0] = indexZufallszahl1;
        selectedInd[1] = indexZufallszahl2;

        return selectedInd;

    }
    //Get Index Individuum das die Zufallszahl trifft
    public static int getIndRoulette(double zufallszahl, Double[] maxZahlenInd ){
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
}
