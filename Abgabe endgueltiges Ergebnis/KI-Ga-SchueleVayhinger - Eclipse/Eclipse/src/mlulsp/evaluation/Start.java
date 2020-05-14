package mlulsp.evaluation;

import java.io.File;
import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.io.DirectoryWalker;
import mlulsp.io.InstanceReader;
import mlulsp.io.ReadBestResults;
import mlulsp.solvers.ga.GaSolverSelektion;
import mlulsp.solvers.ga.GaSolverWettkampf;
import mlulsp.solvers.ga.GaSolverWettkampfVarFlip;
import mlulsp.solvers.ga.GaSolverWettkampfVarSwap;
import mlulsp.solvers.ga.GaSolverWettkampfVariation;

public class Start {

	public static void main(String[] args) {
		new Start().exec();
	}

	

	public void exec() {
		int anzahlLoesungenProInstanz = 400000; //Anzahl an Loesungen die man rechnen will 
		//int anzahlLoesungenProInstanz = 50000; 
		// class1: anzahlLoesungenProInstanz =  50000
		// class2: anzahlLoesungenProInstanz = 200000 
		// class3: anzahlLoesungenProInstanz = 400000 
		
		int anzahlOptimal      = 0;
		int anzahlInstanzen    = 0;
		double gapOptimal      = 0.;
		double avgGapOptimal   = 0.;
		double optimalValue    = 0.;
		double precision       = 0.001;
		
		String insPath         = "instances";
		String solFile         = "solutions.txt";
		DirectoryWalker walker = new DirectoryWalker(new File(insPath), false);

		for (File insFile : walker) {
			InstanceReader inReader = new InstanceReader(insFile);
			Instance i              = inReader.parse();
			optimalValue            = ReadBestResults.getBestSolution(new File(solFile), i.getName());

	//		GaSolverSelektion solver         = new GaSolverSelektion(anzahlLoesungenProInstanz);
			GaSolverWettkampf solver= new GaSolverWettkampf(anzahlLoesungenProInstanz);
	//		GaSolverWettkampfVarSwap solver= new GaSolverWettkampfVarSwap(anzahlLoesungenProInstanz);
	//		GaSolverWettkampfVarFlip solver= new GaSolverWettkampfVarFlip(anzahlLoesungenProInstanz);
	//		GaSolverWettkampfVariation solver = new GaSolverWettkampfVariation(anzahlLoesungenProInstanz);

			long start = System.currentTimeMillis();
			ProductionSchedule solution = solver.solve(i);
			long time = System.currentTimeMillis() - start;

			if(optimalValue > 0.){
				gapOptimal = 100*((solution.getCostSum()-optimalValue)/optimalValue);
				if (Math.abs(gapOptimal) < precision)anzahlOptimal++;
				avgGapOptimal += gapOptimal;
				anzahlInstanzen++;
				System.out.print("Instance " + i.getName() + " solved in " + time + " ms");
				System.out.print(" ZF = " + solution.getCostSum() + " OPT = " + optimalValue);
				System.out.print(" Abweichung: " + gapOptimal + " %");
				System.out.println();
			}
			else{
				System.out.print("Kein Literaturwert vorhanden fuer: " + i.getName());
				System.out.print(" solved in " + time + " ms");
				System.out.print(" ZF = " + solution.getCostSum());
			}
		}
		System.out.println();
		if (anzahlInstanzen != 0) {
			System.out.println("----> Anzahl Instanzen mit Vergleichswerten: " + anzahlInstanzen);
			System.out.println("----> Anzahl optimal geloester Instanzen: "	+ anzahlOptimal + "\t (Precision: " + precision + ")");
			System.out.println("----> Durchschnittliche prozentuale Abweichung: " + (avgGapOptimal / anzahlInstanzen));
		}
	}
}
