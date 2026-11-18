package threadmill.simulation.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import threadmill.simulation.exceptions.InfiniteForceException;
import threadmill.simulation.model.geometry.P2d;
import threadmill.simulation.model.geometry.V2d;

public class MultiThreadedSimulation implements Simulation {

    /* bodies in the field */
    private List<Body> bodies;

    @Override
    public void init(List<Body> bodies) {
        this.bodies = bodies;
    }

    @Override
    public void initRandom(int nBodies) {
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
    }

    @Override
    public void computeStep(double dt) {
        /* update bodies velocity */
        for (int i = 0; i < bodies.size(); i++) {
            Body b = bodies.get(i);

            /* compute total force on bodies */
            V2d totalForce = computeTotalGForceOnBody(b);

            /* compute instant acceleration */
            V2d acc = new V2d(totalForce).scalarMul(1.0 / b.getMass());

            /* update velocity */
            b.updateVelocity(acc, dt);
        }

        /* compute bodies new pos */
        for (Body b : bodies) {
            b.updatePos(dt);
        }
    }

    /*
     * Compute the total G force exterted on a body by the other bodies
     */
    private V2d computeTotalGForceOnBody(Body b) {
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
}
