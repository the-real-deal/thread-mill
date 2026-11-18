package threadmill.simulation.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import threadmill.simulation.model.Body;
import threadmill.simulation.model.geometry.P2d;

/**
 * 
 * Simulation view
 *
 */
public class SimulationView implements View {

	private final VisualiserFrame frame;
	private long nFramesRendered;
	private long startTime;

	public SimulationView(int w, int h) {
		this.frame = new VisualiserFrame(w, h);
	}

	@Override
	public void init() {
		this.startTime = System.currentTimeMillis();
		this.nFramesRendered = 0;
	}

	@Override
	public void render(List<Body> bodies, double vt, long iter) {
		nFramesRendered++;
		long dt = System.currentTimeMillis() - startTime;
		int framePerSec = (int) ((((double) nFramesRendered) / dt) * 1000);
		frame.display(bodies, vt, iter, framePerSec);
	}

	public static class VisualiserFrame extends JFrame {

		private final VisualiserPanel panel;
		private final JButton startButton = new JButton("Start");
		private final JButton stopButton = new JButton("Stop");
		private final JButton resumeButton = new JButton("Resume");
		private final JButton stepButton = new JButton("Step");

		public VisualiserFrame(int w, int h) {
			setTitle("Bodies Simulation");
			setSize(w, h);
			setResizable(false);
			panel = new VisualiserPanel(w, h);

			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
			buttonPanel.add(startButton);
			buttonPanel.add(stopButton);
			buttonPanel.add(resumeButton);
			buttonPanel.add(stepButton);

			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(panel, BorderLayout.CENTER);
			getContentPane().add(buttonPanel, BorderLayout.SOUTH);

			// Initial state: only Start is available
			stopButton.setEnabled(false);
			resumeButton.setEnabled(false);
			stepButton.setEnabled(false);

			startButton.addActionListener(e -> {
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				resumeButton.setEnabled(false);
				stepButton.setEnabled(false);
				// control.onStart();
			});

			stopButton.addActionListener(e -> {
				stopButton.setEnabled(false);
				resumeButton.setEnabled(true);
				stepButton.setEnabled(true);
				// control.onStop();
			});

			resumeButton.addActionListener(e -> {
				resumeButton.setEnabled(false);
				stepButton.setEnabled(false);
				stopButton.setEnabled(true);
				// control.onResume();
			});

			stepButton.addActionListener(e -> {
			}/* control.onStep() */);

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent ev) {
					System.exit(-1);
				}

				@Override
				public void windowClosed(WindowEvent ev) {
					System.exit(-1);
				}
			});
			this.setVisible(true);
		}

		public void display(List<Body> bodies, double vt, long iter, int framePerSec) {
			try {
				SwingUtilities.invokeAndWait(() -> {
					panel.display(bodies, vt, iter, framePerSec);
					repaint();
				});
			} catch (InterruptedException | InvocationTargetException ex) {
			}
		};

		public void updateScale(double k) {
			panel.updateScale(k);
		}
	}

	public static class VisualiserPanel extends JPanel implements KeyListener {

		private List<Body> bodies;

		private long nIter;
		private int framePerSec = 0;
		private double vt;
		private double scale = 1;

		private final long dx;
		private final long dy;

		public VisualiserPanel(int w, int h) {
			setSize(w, h);
			dx = w / 2 - 20;
			dy = h / 2 - 20;
			this.addKeyListener(this);
			setFocusable(true);
			setFocusTraversalKeysEnabled(false);
			requestFocusInWindow();
		}

		@Override
		public void paint(Graphics g) {
			if (bodies != null) {
				Graphics2D g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				g2.clearRect(0, 0, this.getWidth(), this.getHeight());

				bodies.forEach(b -> {
					P2d p = b.getPos();
					int radius = (int) (b.getRadius() * dx * scale);
					if (radius < 1) {
						radius = 1;
					}
					g2.drawOval(getXcoord(p.getX()), getYcoord(p.getY()), radius, radius);
				});
				String time = String.format("%.2f", vt);
				g2.drawString("Bodies: " + bodies.size() + " - vt: " + time + " - nIter: " + nIter + " - frames/sec: "
						+ framePerSec, 2, 20);
				g2.drawString("Commands: UP for zoom in, DOWN for zoom out", 2, 40);
			}
		}

		private int getXcoord(double x) {
			return (int) (dx + x * dx * scale);
		}

		private int getYcoord(double y) {
			return (int) (dy - y * dy * scale);
		}

		public void display(List<Body> bodies, double vt, long iter, int framePerSec) {
			this.bodies = bodies;
			this.vt = vt;
			this.nIter = iter;
			this.framePerSec = framePerSec;
		}

		public void updateScale(double k) {
			scale *= k;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == 38) { /* KEY UP */
				scale *= 1.1;
			} else if (e.getKeyCode() == 40) { /* KEY DOWN */
				scale *= 0.9;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
}
