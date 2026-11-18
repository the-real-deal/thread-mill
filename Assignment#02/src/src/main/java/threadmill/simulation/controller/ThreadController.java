package threadmill.simulation.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import threadmill.simulation.model.Body;
import threadmill.simulation.model.MultiThreadedSimulation;
import threadmill.simulation.model.Simulation;
import threadmill.simulation.view.View;

public class ThreadController extends Thread implements SimulationController {

    private Optional<View> view;
    private final SharedState state;
    private final Simulation simulaton;

    private final Lock suspender = new ReentrantLock();

    /* Simulation variables */
    private double vt;
    private final double dt;
    private int iter = 0;
    private int maxIter;

    /**
     * Initializes the simulation with a random initial state and a predefined
     * number of bodies
     * 
     * @param view    the attached view
     * @param nBodies the number of bodies in the simulation
     */
    public ThreadController(int nBodies) {
        this.view = Optional.empty();
        this.state = new SharedState();
        this.dt = 0.01;
        this.vt = 0;
        this.iter = 0;
        this.simulaton = new MultiThreadedSimulation();
        this.simulaton.initRandom(nBodies);
    }

    /**
     * Initializes the simulation with a random initial state and a predefined
     * number of bodies
     * 
     * @param view    the attached view
     * @param nBodies the number of bodies in the simulation
     */
    public ThreadController(List<Body> bodies) {
        this.view = Optional.empty();
        this.state = new SharedState();
        this.dt = 0.01;
        this.vt = 0;
        this.iter = 0;
        this.simulaton = new MultiThreadedSimulation();
        this.simulaton.init(bodies);
    }

    @Override
    public void setView(View v) {
        this.view = Optional.of(v);
    }

    @Override
    public void init() {
        if (view.isPresent()) {
            view.get().init();
        }
        this.state.setState(SimulationState.RUNNING);
    }

    @Override
    public void suspend() {
        this.suspender.lock();
        this.state.setState(SimulationState.SUSPENDED);
    }

    @Override
    public void resume() {
        this.state.setState(SimulationState.RUNNING);
        this.suspender.unlock();
    }

    @Override
    public void step() {
        this.state.setState(SimulationState.STEP);
    }

    @Override
    public void run() {
        // long lastFrame = System.nanoTime();
        while (this.iter < this.maxIter) {
            switch (state.getState()) {
                case SimulationState.RUNNING -> {
                    this.simulaton.computeStep(this.dt);
                    updateSimulationValues();
                }
                case SimulationState.STEP -> {
                    this.simulaton.computeStep(this.dt);
                    updateSimulationValues();
                    state.setState(SimulationState.SUSPENDED);
                }
                case SimulationState.SUSPENDED -> {
                    this.suspender.lock();
                    this.suspender.unlock();
                }
            }
            // [TODO] update so cmd and gui both handle correctly

            // long now = System.nanoTime();
            // if (now - lastFrame >= 16_000_000L) {
            // // view.render(this.bodies, this.vt, this.currentIter);
            // lastFrame = now;
            // }
            /* display current stage */

            if (view.isPresent()) {
                view.get().render(this.simulaton.getBodies(), this.vt, this.iter);
            }
        }
    }

    private void updateSimulationValues() {
        this.vt = this.vt + this.dt;
        this.iter++;
    }
}
