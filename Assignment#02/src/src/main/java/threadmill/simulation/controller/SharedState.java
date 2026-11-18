package threadmill.simulation.controller;

public class SharedState {
    private SimulationState state = SimulationState.SUSPENDED;

    public synchronized SimulationState getState() {
        return this.state;
    }

    public synchronized void setState(SimulationState currentState) {
        this.state = currentState;
    }
}
