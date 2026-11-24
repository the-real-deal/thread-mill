package threadmill.simulation.model.multithreadedsimulation;

import java.util.List;

import threadmill.simulation.model.Body;
import threadmill.simulation.model.geometry.V2d;

public class BodyWorker extends Thread {

    private final MultiThreadedSimulation simulation;
    private final List<Body> bodies;
    private final int startIndex;
    private final int endIndex;
    private final StepBarrier barrier;

    public BodyWorker(
            MultiThreadedSimulation simulation,
            List<Body> bodies,
            int startIndex,
            int endIndex,
            StepBarrier barrier) {
        this.simulation = simulation;
        this.bodies = bodies;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            while (barrier.waitForStepStart()) {
                double dt = barrier.getDt();

                for (int i = startIndex; i < endIndex; i++) {
                    Body b = bodies.get(i);
                    V2d totalForce = simulation.computeTotalGForceOnBody(b);
                    V2d acc = new V2d(totalForce).scalarMul(1.0 / b.getMass());
                    b.updateVelocity(acc, dt);
                }

                /* Barrier before position updates */
                barrier.awaitVelocitiesDone();
                
                for (int i = startIndex; i < endIndex; i++) {
                    bodies.get(i).updatePos(dt);
                }

                /* Signal step completion to Master */
                barrier.notifyStepDone();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}