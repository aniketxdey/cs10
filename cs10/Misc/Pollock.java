import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Painting random colors with WanderingPixels
 * Based on Pollock Template for SA-2, Dartmouth CS 10 - original author: Chris Bailey-Kellogg
 * author:Aniket Dey
 */
public class Pollock extends WanderingPulsar.DrawingGUI {
	private static final int width = 800, height = 600; // setup: window size
	private static final int numBlobs = 20000;			// setup: how many blobs
	private static final int numToMove = 5000;			// setup: how many blobs to animate each frame

	private BufferedImage result;						// the picture being painted
	private ArrayList<WanderingPixel> blobs;						// the blobs representing the picture
	
	public Pollock() {
		super("Pollock", width, height);
		
		result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		// Create a bunch of random blobs.
		blobs = new ArrayList<WanderingPixel>();
		for (int i=0; i<numBlobs; i++) {
			int x = (int)(width*Math.random());
			int y = (int)(height*Math.random());
			// Create a blob with a random color
			int c = (int)(Math.random() * 16777216); //exclusive of 16777216
			blobs.add(new WanderingPixel(x, y, 1, new Color(c))); //uses WanderingPixel() constructor to add color assignment
		}

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * WanderingPulsar.DrawingGUI method, here just drawing all the blobs
	 */
	@Override
	public void draw(Graphics g) {
		g.drawImage(result, 0, 0, null);
		for (Blob blob : blobs) {
			blob.draw(g);
		}		
	}

	/**
	 * WanderingPulsar.DrawingGUI method, here moving some of the blobs
	 */
	@Override
	public void handleTimer() {
		for (int b = 0; b < numToMove; b++) {
			// Pick a random blob, leave a trail where it is, and ask it to move.
			WanderingPixel blob = blobs.get((int)(Math.random()*blobs.size()));
			int x = (int)blob.getX(), y = (int)blob.getY();
			// Careful to stay within the image
			if (x>=0 && x<width && y>=0 && y<height) {
				Color color = blob.getColor(); //new method added in WanderingPixel Class
				result.setRGB(x, y, color.getRGB());				
			}
			blob.step();
		}
		// Now update the drawing
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Pollock();
			}
		});
	}
}
