package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.solvers.Solver;


public class GaSolver implements Solver {
	private final int anzahlLoesungen;

	/*
	 * hier können Parameter des GA angegeben werden z.B. PopulationsGroesse,
	 * IterationenAnzahl
	 */

	public GaSolver(int anzahlLoesungen) {
		this.anzahlLoesungen = anzahlLoesungen;
	}

	public ProductionSchedule solve(Instance instance) {
		Individual.firstLastPeriodsBerechnen(instance);
		Individual.mutationsWahrscheinlichkeit();

		Individual elter, child;
		elter = new Individual(instance);
		elter.initRandom();
		elter.decoding(instance);
		elter.evaluate();

		
		for (int i = 1; i < anzahlLoesungen; i++) {
			child = new Individual(instance);
			child.reproduce(elter);
			child.mutate();
			child.decoding(instance);
			child.evaluate();
			if (child.getFitness() <= elter.getFitness()) {
				if (child.getFitness() < elter.getFitness()) {
					//System.out.println(i + " " + elter.getFitness());	
				}				
				elter = child;
			}
		}

		return elter.getPhaenotype();

	}
}
