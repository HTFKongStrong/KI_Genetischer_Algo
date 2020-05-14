package mlulsp.solvers.gaMethods;

import mlulsp.solvers.ga.Individual;

public class Mutation {

    public static void swapMutation(Individual ind, double pMut){
        int[][] indArray = ind.getGenotype();
        for (int zeile = 0; zeile < indArray.length ; zeile++) {
            //get 2 random integers between 0 and size of array
            int zufallszahl1 = (int) (Math.random() * indArray[zeile].length);
            int zufallszahl2 = (int) (Math.random() * indArray[zeile].length);
            //make sure the 2 numbers are different
            while(zufallszahl1 == zufallszahl2){
                zufallszahl2 = (int) (Math.random() * indArray[zeile].length);
            }
            //swap array elements at those indices
            if (Math.random() <= pMut ){
                int temp = indArray[zeile][zufallszahl1];
                indArray[zeile][zufallszahl1] = indArray[zeile][zufallszahl2];
                indArray[zeile][zufallszahl2] = temp;
            }
        }
        ind.setGenotype(indArray);
    }

    private void flipMutation(Individual ind, double pMut){
        for (int zeile = 0; zeile < ind.getGenotype().length ; zeile++) {
            for (int spalte = 0; spalte < ind.getGenotype()[zeile].length ; spalte++) {
                if (Math.random() <= pMut ){
                    if(ind.getGenotype()[zeile][spalte] == 1) ind.getGenotype()[zeile][spalte] = 0;
                    else                                      ind.getGenotype()[zeile][spalte] = 1;
                }

            }
        }
    }
    
    public static void flipMutationVar(Individual ind, double pMut){
        for (int zeile = 0; zeile < ind.getGenotype().length ; zeile++) {
            for (int spalte = Individual.lastPeriodforItems[zeile]; spalte < Individual.lastPeriodforItems[zeile]; spalte++) {
                if (Math.random() <= pMut ){
                    if(ind.getGenotype()[zeile][spalte] == 1) ind.getGenotype()[zeile][spalte] = 0;
                    else                                      ind.getGenotype()[zeile][spalte] = 1;
                }

            }
        }
    }

    //Die Mutationswahrscheinlichkeit (pMutVar) erhoeht sich randomly nachdem die Populationsgroesse (populationsGroesseMutVar)
    //erreicht wurde. Bis zu dieser Populationsgroesse bleibt die Mutationsws (pMutVar = pMut) gleich.
    //Die neue Mutationsws wird nach jeder Generation geaendert und bleibt ueber das Erstellen (/Crossover, Mutation) einer Generation gleich.
    //Sie erhoeht sich dabei zwischen dem Wert von pMut bis 1 randomly und wird niemals niedriger als pMut sein.
    public static double variationPmut(int populationsGroesseMutVar, double pMutVar, double pMutHilf, int anzahlIndividuenGes, double pMut){
        if (anzahlIndividuenGes >= populationsGroesseMutVar){
            pMutVar = (Math.random() *pMutHilf) + pMut;
            //System.out.println("pMutVar: " + pMutVar);
        }
        return pMutVar;
    }
    
    public static void swapMutationVar(Individual ind, double pMutVar){
        int[][] indArray = ind.getGenotype();
        for (int zeile = 0; zeile < indArray.length ; zeile++) {
            //get 2 random integers between 0 and size of array
        	int max = Individual.lastPeriodforItems[zeile];
        	int min = Individual.firstPeriodforItems[zeile];
        	int range = max - min +1;
        	int zufallszahl1 = (int) (Math.random() * range) + min;
        	int zufallszahl2 = (int) (Math.random() * range) + min;
            //make sure the 2 numbers are different
            while(zufallszahl1 == zufallszahl2){
                zufallszahl2 =  (int) (Math.random() * range) + min;
            }
            if (Math.random() <= pMutVar ){ 
                int temp = indArray[zeile][zufallszahl1];
                indArray[zeile][zufallszahl1] = indArray[zeile][zufallszahl2];
                indArray[zeile][zufallszahl2] = temp;
            }
        }
        ind.setGenotype(indArray);
    }
}
