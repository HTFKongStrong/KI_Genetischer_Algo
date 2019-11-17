package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.solvers.Solver;


public class GaSolver implements Solver {
	private final int anzahlLoesungen;

	/*
	 * hier k�nnen Parameter des GA angegeben werden z.B. PopulationsGroesse,
	 * IterationenAnzahl
	 */

	public GaSolver(int anzahlLoesungen) {
		this.anzahlLoesungen = anzahlLoesungen;
	}

	public ProductionSchedule solve(Instance instance) {
		Individual.firstLastPeriodsBerechnen(instance);
		Individual.mutationsWahrscheinlichkeit();

		Individual elter, child; //eigentlich n Individuen
		
		//Mögliche Vorgehensweise
		int size = 10;
		Individual [] pop = new Individual[size];
		
		for (int i = 0; i < pop.length; i++) {
			pop[i]= new Individual(instance);
			pop[i].initRandom();
			pop[i].decoding(instance);
			pop[i].evaluate();
		}
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

		return elter.getPhaenotype();

	}
}