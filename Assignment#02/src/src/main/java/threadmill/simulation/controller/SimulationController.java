package threadmill.simulation.controller;

import threadmill.simulation.view.View;

public interface SimulationController {
    /**
     * Sets the view
     */
    void setView(View v);

    /**
     * Starts the simulation execution
     */
    void init();

    /**
     * Pauses the simulation execution
     */
    void suspend();

    /**
     * Resumes the simulation execution
     */
    void resume();

    /**
     * When stopped, this causes the simulation to only perform one step
     */
    void step();
}
