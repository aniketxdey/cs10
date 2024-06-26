import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.ArrayList;

/**
 * Animated blobs.
 * Extended from earlier version to handle a WanderingImage.
 * Additions marked with ***
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016, based on animated agents from previous terms
 */
public class BlobsGUI2 extends WanderingPulsar.DrawingGUI {
	private static final int width=800, height=600;		// setup: size of the world

	private ArrayList<Blob> blobs;						// list of all the blobs to handle
	private char blobType = '0';						// what type of blob to create
	private int delay = 100;							// for the timer
	private BufferedImage blobImage;					// *** for WanderingImages
	
	public BlobsGUI2() {
		super("Animated Blobs", width, height);
		
		blobImage = loadImage("pictures/smiley.png"); // *** Load an image for WanderingImages

		// Initialize empty list of blobs. What happens if we forget to do this?
		// (You will run into that situation in the future, I guarantee.)
		blobs = new ArrayList<Blob>();
		
		// Timer drives the animation.
		startTimer();
	}
	
	/**
	 * WanderingPulsar.DrawingGUI method, here either detecting which blob was clicked,
	 * or creating a new blob.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// Check if hit a blob
		for (Blob blob : blobs) {
			if (blob.contains(x, y)) {
				System.out.println("back off!");
				return;
			}
		}
		
		// Create a new blob
		if (blobType=='0') {
			blobs.add(new Blob(x,y));
		}
		else if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='i') { // this and next couple lines ***
			blobs.add(new WanderingImage(x,y,blobImage)); 
		}
		else if (blobType=='p') {
			blobs.add(new Pulsar(x,y));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else if (blobType=='u') {
			blobs.add(new WanderingPulsar(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
		
		// Redraw with added blob
		repaint();
	}

	/**
	 * WanderingPulsar.DrawingGUI method, here just remembering the type of blob to create
	 */
	@Override
	public void handleKeyPress(char k) {
		System.out.println("Handling key '"+k+"'");
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
		else { // blob type
			blobType = k;			
		}
	}
	
	/**
	 * WanderingPulsar.DrawingGUI method, here just drawing all the blobs
	 */
	@Override
	public void draw(Graphics g) {
		// Ask all the blobs to draw themselves.
		for (Blob blob : blobs) {
			blob.draw(g);
		}		
	}
	
	/**
	 * WanderingPulsar.DrawingGUI method, here having all the blobs take a step
	 */
	@Override
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Now update the GUI.
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new BlobsGUI2();
			}
		});
	}
}
