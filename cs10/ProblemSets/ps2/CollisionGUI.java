import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;						// all the blobs
	private List<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	public CollisionGUI() {
		super("super-collider", width, height);

		blobs = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}			
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		for (Blob blob : blobs) {
			if (colliders != null && colliders.contains(blob)) {
				// Draw collider blobs in red
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.BLUE);
			}
			blob.draw(g);  // Assumes Blob has a draw method
		}
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		colliders = new ArrayList<>();
		PointQuadtree<Blob> quadtree = new PointQuadtree<>(blobs.get(0), 0, 0, width, height);
		for (Blob blob : blobs) {
			quadtree.insert(blob);
		}

		for(Blob g: blobs) {
			for(Blob b:blobs) {
				if(g.contains(b.getX(), b.getY()) && !g.equals(b)) {
					if (collisionHandler=='c') {
						List<Blob> temp = new ArrayList<Blob>();
						temp = quadtree.findInCircle(b.getX(),b.getY(),5);
						for (Blob j: temp){
							colliders.add(j);
						}
					}
					if (collisionHandler=='d') {
						List<Blob> temp = new ArrayList<Blob>();
						temp = quadtree.findInCircle(b.getX(),b.getY(),5);
						for (Blob j: temp){
							colliders.add(j);
						}
					}
				}
			}
		}
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}



	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						CollisionGUI gui = new CollisionGUI();

						// test 1: Blobs that should collide
						Blob blob1 = new Bouncer(100, 100, width, height);
						Blob blob2 = new Bouncer(105, 105, width, height);
						gui.blobs.add(blob1);
						gui.blobs.add(blob2);

						gui.findColliders();
						if(gui.colliders.contains(blob1) && gui.colliders.contains(blob2)) {
							System.out.println("Test 1 passed: blobs correctly detected as colliding");
						}

						gui.blobs.clear();
						if(gui.colliders != null) {
							gui.colliders.clear();
						}

						// test 2: Blobs that should not collide
						Blob blob3 = new Bouncer(50, 50, width, height);
						Blob blob4 = new Bouncer(500, 500, width, height);
						gui.blobs.add(blob3);
						gui.blobs.add(blob4);

						gui.findColliders();
						if(!gui.colliders.contains(blob3) && !gui.colliders.contains(blob4)) {
							System.out.println("Test 2 passed: blobs correctly detected as not colliding");
						}

						// Clear the list after the test
						gui.blobs.clear();
						if(gui.colliders != null) {
							gui.colliders.clear();
						}
					}
				});
			}
		});
	}
}
