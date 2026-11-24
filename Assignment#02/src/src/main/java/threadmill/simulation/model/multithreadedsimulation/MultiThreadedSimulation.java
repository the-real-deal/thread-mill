package threadmill.simulation.model.multithreadedsimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import threadmill.simulation.exceptions.InfiniteForceException;
import threadmill.simulation.model.Body;
import threadmill.simulation.model.Simulation;
import threadmill.simulation.model.geometry.P2d;
import threadmill.simulation.model.geometry.V2d;

public class MultiThreadedSimulation implements Simulation {

    private List<Body> bodies;
    private List<BodyWorker> workers;
    private StepBarrier barrier;

    @Override
    public void init(List<Body> bodies) {
        shutdown();
        this.bodies = bodies;
        initWorkers();
    }

    @Override
    public void initRandom(int nBodies) {
        shutdown();
        Random rand = new Random(System.currentTimeMillis());
        bodies = new ArrayList<>();
        for (int i = 0; i < nBodies; i++) {
            double x = -1.0 + 2.0 * rand.nextDouble();
            double y = -1.0 + 2.0 * rand.nextDouble();
            double vx = -0.1 + 0.2 * rand.nextDouble();
            double vy = -0.1 + 0.2 * rand.nextDouble();
            double m = 1 + rand.nextDouble() * 9;
            Body b = new Body(i, new P2d(x, y), new V2d(vx, vy), 0.01, m);
            bodies.add(b);
        }
        initWorkers();
    }

    private void initWorkers() {
        int n = bodies.size();
        if (n == 0) {
            this.workers = List.of();
            return;
        }

        int numWorkers = Math.min(n, Runtime.getRuntime().availableProcessors());
        this.barrier = new StepBarrier(numWorkers);
        this.workers = new ArrayList<>(numWorkers);

        for (int i = 0; i < numWorkers; i++) {
            int start = (( i * n) / numWorkers);
            int end = (((i + 1) * n) / numWorkers);
            BodyWorker worker = new BodyWorker(this, bodies, start, end, barrier);
            worker.setDaemon(true);
            workers.add(worker);
            worker.start();
        }
    }

    @Override
    public void computeStep(double dt) {
        if (bodies == null || bodies.isEmpty()) {
            return;
        }
        try {
            barrier.executeStep(dt);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /* Package-private so BodyWorker (and later Executor tasks) can reuse it */
    V2d computeTotalGForceOnBody(Body b) {
        V2d totalForce = new V2d(0, 0);
        for (int j = 0; j < bodies.size(); j++) {
            Body otherBody = bodies.get(j);
            if (!b.equals(otherBody)) {
                try {
                    V2d forceByOtherBody = b.computeGForceBy(otherBody);
                    totalForce.sum(forceByOtherBody);
                } catch (InfiniteForceException ex) {
                }
            }
        }
        return totalForce;
    }

    @Override
    public List<Body> getBodies() {
        return this.bodies;
    }

    @Override
    public void shutdown() {
        if (barrier != null) {
            barrier.shutdown();
        }
    }
}