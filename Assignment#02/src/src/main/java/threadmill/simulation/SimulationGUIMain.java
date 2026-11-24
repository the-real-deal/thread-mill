package threadmill.simulation;

import threadmill.simulation.controller.SimulationController;
import threadmill.simulation.controller.TestBodySets;
import threadmill.simulation.controller.ThreadController;
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
		SimulationController c = new ThreadController(
				TestBodySets.bodySet_two_bodies(),
				nSteps);
		View view = new SimulationView(620, 620, c);
		view.init();
		c.setView(view);
	}
}
