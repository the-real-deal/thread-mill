package threadmill.simulation.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;

import threadmill.simulation.model.Body;
import threadmill.simulation.model.Simulation;
import threadmill.simulation.model.multithreadedsimulation.MultiThreadedSimulation;
import threadmill.simulation.view.View;

public class ThreadController extends Thread implements SimulationController {

    private Optional<View> view;
    private final SharedState state;
    private final Simulation simulaton;

    private final Semaphore stepper = new Semaphore(0);

    /* Simulation variables */
    private double vt;
    private final double dt;
    private int iter = 0;
    private final int maxIter;

    /**
     * Initializes the simulation with a random initial state and a predefined
     * number of bodies
     * 
     * @param view    the attached view
     * @param nBodies the number of bodies in the simulation
     */
    public ThreadController(int nBodies, int iterations) {
        this.view = Optional.empty();
        this.state = new SharedState();
        this.dt = 0.01;
        this.vt = 0;
        this.iter = 0;
        this.simulaton = new MultiThreadedSimulation();
        this.simulaton.initRandom(nBodies);
        this.maxIter = iterations;
    }

    /**
     * Initializes the simulation with a random initial state and a predefined
     * number of bodies
     * 
     * @param view    the attached view
     * @param nBodies the number of bodies in the simulation
     */
    public ThreadController(List<Body> bodies, int iterations) {
        this.view = Optional.empty();
        this.state = new SharedState();
        this.dt = 0.01;
        this.vt = 0;
        this.iter = 0;
        this.simulaton = new MultiThreadedSimulation();
        this.simulaton.init(bodies);
        this.maxIter = iterations;
    }

    @Override
    public void setView(View v) {
        this.view = Optional.of(v);
    }

    @Override
    public void init() {
        this.state.setState(SimulationState.RUNNING);
        this.start();
    }

    @Override
    public void pause() {
        this.state.setState(SimulationState.SUSPENDED);
    }

    @Override
    public void restart() {
        this.state.setState(SimulationState.RUNNING);
        this.stepper.release();
    }

    @Override
    public void step() {
        this.state.setState(SimulationState.STEP);
        this.stepper.release();
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
                    try {
                        this.stepper.acquire();
                        this.simulaton.computeStep(this.dt);
                        updateSimulationValues();
                    } catch (InterruptedException ex) {
                        System.getLogger(ThreadController.class.getName()).log(
                                System.Logger.Level.ERROR,
                                "Error while computing simulation step",
                                ex);
                    }
                }
                case SimulationState.SUSPENDED -> {
                    try {
                        this.stepper.acquire();
                    } catch (InterruptedException ex) {
                        System.getLogger(ThreadController.class.getName()).log(
                                System.Logger.Level.ERROR,
                                "Error while suspending simulation",
                                ex);
                    }
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
        this.simulaton.shutdown();
    }

    private void updateSimulationValues() {
        this.vt = this.vt + this.dt;
        this.iter++;
    }
}
