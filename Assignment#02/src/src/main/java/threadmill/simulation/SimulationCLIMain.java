package threadmill.simulation;

import threadmill.simulation.controller.ThreadController;

/**
 * 
 * N-Body simulation - simple sequential version
 * 
 * @author aricci
 */
public class SimulationCLIMain {

	public static void main(String[] args) {

		int nSteps = 5000;
		int nBodies = 5000;
		// sim.init(TestBodySets.bodySet_two_bodies());
		// sim.init(TestBodySets.bodySet_three_bodies());
		ThreadController control = new ThreadController(nSteps, nBodies);

		long startTime = System.currentTimeMillis();
		log("Starting simulation with " + nBodies + " bodies - " + nSteps + " steps...");

		control.init();
		try {
			control.join();
		} catch (InterruptedException ex) {
			System.getLogger(SimulationCLIMain.class.getName()).log(
					System.Logger.Level.ERROR,
					"Error while simulating bodies",
					ex);
		}

		long endTime = System.currentTimeMillis();
		log("Simulation completed in " + (endTime - startTime) / 1000 + " seconds.");
	}

	private static void log(String msg) {
		System.out.println("[" + System.currentTimeMillis() + "] " + msg);
	}

}
