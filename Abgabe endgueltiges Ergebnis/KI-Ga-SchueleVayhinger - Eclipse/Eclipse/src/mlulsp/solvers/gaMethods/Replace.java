package mlulsp.solvers.gaMethods;

import mlulsp.solvers.ga.Individual;

import java.util.ArrayList;
import java.util.Arrays;

public class Replace {

    //Delete 75% der Eltern und behalte 25% (Random)
    //Delete 25% der Kinder und behalte 75% (delete schlechteste)
    //andere Verhaeltnisse vllt besser?
    public static Individual[] replaceDeleteNlast(Individual[] populationKids, Individual[] populationEltern,
                                                           int anzahlKeepDelete, Individual[] selektierteEltern, int populationsGroesse){
        Individual[] newGeneration = new Individual[populationEltern.length];
        Integer[] zahlenBesetzt = new Integer[anzahlKeepDelete];

        //50 eltern (25%) sollen in newGeneration uebernommen werden
        for (int i = 0; i < anzahlKeepDelete ; i++) {
            int indexInd = check(zahlenBesetzt, selektierteEltern, populationEltern, populationsGroesse);
            newGeneration[i] = populationEltern[indexInd];
        }
        //sort populationKids: beste/kleinste Fitness oben
        Arrays.sort(populationKids); //um jegliche Populationsgroesse benutzen zu koennen, muessten zuerst alle Null Werte entfernt werden

        for (int i = anzahlKeepDelete; i < populationsGroesse ; i++) {
            newGeneration[i] = populationKids[i];
        }
        return newGeneration;
    }
    
    // Keine der schon selektierten Eltern soll uebernommen werden und auch keine Eltern doppelt
    public static int check(Integer[] zahlenBesetzt, Individual[] selektierteEltern, Individual[] populationEltern, int populationsGroesse){
        int indexInd = (int) (Math.random() * populationsGroesse); //Zufallszahl zw. 0 bis populationsgroesse-1 //deswegen keine +1 am ende
        Individual ind = populationEltern[indexInd];
     // boolean containsInd = contains(selektierteEltern, ind);
     //  boolean zahlBesetzt = contains(zahlenBesetzt, indexInd);
        boolean containsInd = Arrays.asList(selektierteEltern).contains(ind);
        boolean zahlBesetzt = Arrays.asList(zahlenBesetzt).contains(indexInd);
        if (!zahlBesetzt && !containsInd) {
            for (int i = 0; i < zahlenBesetzt.length; i++) {
                if(zahlenBesetzt[i] == null){
                    zahlenBesetzt[i] = indexInd;
                    break;
                }
            }
            return indexInd;
        }else{
            return check(zahlenBesetzt, selektierteEltern, populationEltern, populationsGroesse);
        }
    }

    public static boolean contains(Individual[] array, Individual indToCheck){
        boolean contains = false;
        for (Individual ind: array) {
            if (ind == indToCheck){
                contains = true;
                break;
            }
        }
        return contains;
    }

    public static boolean contains(Integer[] array, int valueToCheck){
        boolean contains = false;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null){
                if (array[i] == valueToCheck){
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }
}
