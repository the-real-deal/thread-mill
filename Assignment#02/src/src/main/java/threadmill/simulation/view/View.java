package threadmill.simulation.view;

import java.util.List;

import threadmill.simulation.model.Body;

public interface View {

    /**
     * Initializes the view
     */
    void init();

    /**
     * Renders the bodies and updates the text
     * 
     * @param bodies the new bodies list
     * @param vt     the current virtual time
     * @param iter   the current simulation iteration
     */
    void render(List<Body> bodies, double vt, long iter);
}
