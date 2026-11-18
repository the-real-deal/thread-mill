package threadmill.simulation.controller;

import java.util.ArrayList;

import threadmill.simulation.model.Body;
import threadmill.simulation.model.geometry.P2d;
import threadmill.simulation.model.geometry.V2d;

/**
 * 
 * Some body sets to test.
 * 
 */
public class TestBodySets {

	public static ArrayList<Body> bodySet_two_bodies() {
		ArrayList<Body> bodies;
		bodies = new ArrayList<>();
		bodies.add(new Body(0, new P2d(-0.3, 0), new V2d(0, 0), 0.1, 1));
		bodies.add(new Body(1, new P2d(0.3, 0), new V2d(0, 0), 0.1, 1));
		return bodies;
	}

	public static ArrayList<Body> bodySet_three_bodies() {
		ArrayList<Body> bodies;
		bodies = new ArrayList<>();
		bodies.add(new Body(0, new P2d(0, 0), new V2d(0, 0), 0.1, 20));
		bodies.add(new Body(1, new P2d(0.3, 0), new V2d(0, 0.25), 0.1, 1));
		bodies.add(new Body(2, new P2d(-0.5, 0), new V2d(0, -0.15), 0.1, 1));
		return bodies;
	}

}
