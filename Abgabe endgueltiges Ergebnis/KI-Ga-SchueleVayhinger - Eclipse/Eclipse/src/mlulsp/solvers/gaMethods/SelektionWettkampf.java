package mlulsp.solvers.gaMethods;

import mlulsp.solvers.ga.Individual;

import java.util.Arrays;

public class SelektionWettkampf {

    public static Individual[] getMatingpool(Individual[] populationEltern, Individual[] matingpool, int selektionsdruck){
        Individual[] populationElternCopy = populationEltern.clone();
        //beste/kleinste Fitness oben
        Arrays.sort(populationElternCopy);

        for (int i = 0; i < selektionsdruck ; i++) {
            matingpool[i] = populationElternCopy[i];
        }
        return matingpool;
    }

    //GaSolverWettkampf Selektion Wettkampf
    public static int indexRandom(int obereGrenze){
        int selektierterIndex = (int) (Math.random() * obereGrenze);
        return selektierterIndex;
    }
    public static Individual[] selektionWettkampf(Individual[] matingpool){
        int obereGrenze = matingpool.length;
        int anzahlKinder = 2;
        Individual[] selektierteIndividuuen = new Individual[anzahlKinder];
        int selektierterIndex1 =0;

        for (int i = 0; i < anzahlKinder ; i++) {
            int selektierterIndex = indexRandom(obereGrenze);
            if(i ==0 ){ selektierterIndex1 = selektierterIndex; }
            if(i ==1 && selektierterIndex1 == selektierterIndex) { i = 0; selektierterIndex = indexRandom(obereGrenze);}
            selektierteIndividuuen[i] = matingpool[selektierterIndex];
        }
        return selektierteIndividuuen;
    }
}
