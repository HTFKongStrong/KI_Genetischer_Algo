package mlulsp.solvers;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;


public interface Solver {

	public ProductionSchedule solve(Instance instance);

}
