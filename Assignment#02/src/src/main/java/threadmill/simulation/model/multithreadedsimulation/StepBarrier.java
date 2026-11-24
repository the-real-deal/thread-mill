package threadmill.simulation.model.multithreadedsimulation;

import java.util.concurrent.Semaphore;

public class StepBarrier {

    private final int numWorkers;
    private final Semaphore startSem = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore velBarrier = new Semaphore(0);
    private final Semaphore stepDone = new Semaphore(0);

    private int velCompleted = 0;
    private volatile double dt;
    private volatile boolean stopped = false;

    public StepBarrier(int numWorkers) {
        this.numWorkers = numWorkers;
    }

    /* Called by ThreadController */
    public void executeStep(double dt) throws InterruptedException {
        this.dt = dt;
        this.startSem.release(numWorkers);
        this.stepDone.acquire(numWorkers);
    }

    /* Called by Workers at the beginning of each iteration */
    public boolean waitForStepStart() throws InterruptedException {
        this.startSem.acquire();
        return !this.stopped;
    }

    public double getDt() {
        return this.dt;
    }

    /* Blocks until all workers have updated velocities */
    public void awaitVelocitiesDone() throws InterruptedException {
        this.mutex.acquire();
        this.velCompleted++;
        if (this.velCompleted == this.numWorkers) {
            this.velCompleted = 0;
            this.velBarrier.release(this.numWorkers);
        }
        this.mutex.release();
        this.velBarrier.acquire();
    }

    /* Called by Workers after updating positions */
    public void notifyStepDone() {
        this.stepDone.release();
    }

    public void shutdown() {
        this.stopped = true;
        this.startSem.release(this.numWorkers);
    }
}