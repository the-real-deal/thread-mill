package threadmill.simulation;

import threadmill.simulation.controller.TestBodySets;
import threadmill.simulation.model.NBodySimulation;
import threadmill.simulation.view.SimulationView;
import threadmill.simulation.view.View;

/**
 *
 * N-Body simulation - simple sequential version - with a GUI
 * 
 * @author aricci
 */
public class SimulationGUIMain {

	public static void main(String[] args) {

		int nSteps = 50000;

		View view = new SimulationView(620, 620);
		NBodySimulation sim = new NBodySimulation(view);

		// sim.init(TestBodySets.bodySet_two_bodies());
		sim.init(TestBodySets.bodySet_three_bodies());
		// sim.initRandom(5000);

		sim.execute(nSteps);
	}
}
