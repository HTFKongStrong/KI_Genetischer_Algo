package mlulsp.solvers.gaMethods;

import java.util.Scanner;

public class Extras {

    public static void readConsole(int populationsGroesse, double pMut, double prozentVariationPmut,
                            double pK, int selektionsDruck, int anzahlKeepDelete ){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Fuer die Population:");
        System.out.println("Bitte geben Sie die initiale Populationsgroesse ein: ");
        System.out.print("populationsgroesse : Es muss ein int zw. 2 - 1000 sein\n");
        populationsGroesse = scanner.nextInt();
        System.out.println("\nFuer die Berechnung der Mutation:");
        System.out.println("Bitte geben Sie die initiale Mutationswahrscheinlichkeit pMut ein: ");
        System.out.println("pMut : Es muss ein double zw. 0.0 - 1.0 sein\n");
        pMut = scanner.nextDouble();
        System.out.println("Bitte geben Sie eine Prozentzahl an: Nach wie vielen Durchgaengen soll pMut erhoeht werden?");
        System.out.println("prozentVariationPmut : Es muss ein double zw. 0.0 - 1.0 sein\n");
        prozentVariationPmut = scanner.nextDouble();
        System.out.println("\nFuer das Crossover: ");
        System.out.println("Bitte geben Sie die Kreuzungswahrscheinlichkeit pk ein: ");
        System.out.println("pK : Es muss ein double zw. 0.0 - 1.0 sein\n");
        pK = scanner.nextDouble();
        System.out.println("\nFuer die Selektion: ");
        System.out.println("Bitte geben Sie an, wie viele der besten Individuuen zur Fortpflanzung benutzt werden: ");
        System.out.println("selektionsDruck : int min=4, max=populationsgroesse\n");
        selektionsDruck = scanner.nextInt();
        System.out.println("\nFuer das Ersetzungsschema: ");
        System.out.println("Bitte geben Sie an wie viele Eltern in der neuen Generation uebernommen werden sollen: ");
        System.out.println("anzahlKeepDelete : int min=4, max=populationsgroesse\n");
        anzahlKeepDelete = scanner.nextInt();

    }
}
