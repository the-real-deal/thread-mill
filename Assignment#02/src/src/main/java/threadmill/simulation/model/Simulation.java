package threadmill.simulation.model;

import java.util.List;

public interface Simulation {
    /**
     * Initializes the simulation with a predefined set of bodies
     * 
     * @param bodies
     */
    void init(List<Body> bodies);

    /**
     * Inizializes the simulation with a random set of bodies
     * 
     * @param nBodies the number of bodies to randomly generate
     */
    void initRandom(int nBodies);

    /**
     * Calculates the next simulation step
     * 
     * @param dt The simulation delta time
     */
    void computeStep(double dt);

    /**
     * @return Returns a list containing the bodies of the simulation
     */
    List<Body> getBodies();
}
