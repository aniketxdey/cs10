import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * 
 * @author Aniket Dey
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		regions = new ArrayList<ArrayList<Point>>();
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		ArrayList<Point> toVisit = new ArrayList<Point>();
		for (int x = 0; x < image.getWidth(); x++) { // Loop over all the pixels
			for (int y = 0; y < image.getHeight(); y++) {
				Color color = new Color(image.getRGB(x, y));
				if ((visited.getRGB(x, y) == 0) && (colorMatch(color, targetColor))) { // If a pixel is unvisited and of the correct color
					ArrayList<Point> region = new ArrayList<Point>();
					Point startingPoint = new Point(x, y); //start with one point, first in array
					toVisit.add(startingPoint); //add it to the list of points that need to be visited
					while (!toVisit.isEmpty()) { //as long as there as still valid points to visit, continue the loop
						Point point = toVisit.remove(0);
						region.add(point);
						visited.setRGB(point.x, point.y, 1);
						for (int ny = Math.max(0, point.y - 1); ny < Math.min(image.getHeight(), point.y + 2); ny++) { //loop over all neighbors
							for (int nx = Math.max(0, point.x - 1); nx < Math.min(image.getWidth(), point.x + 2); nx++) {
								if (visited.getRGB(nx, ny) == 0 && (colorMatch(new Color(image.getRGB(nx, ny)), targetColor))) { //if hasn't been visited and is of same color
									toVisit.add(new Point(nx, ny));
									visited.setRGB(nx, ny, 1);
								}
							}
						}
					}
					if (region.size() >= minRegion) { //if region is big enough to be considered, add it to regions
						regions.add(region);
					}
				}
			}
		}
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		return (Math.abs(c1.getRed() - c2.getRed()) <= maxColorDiff && Math.abs(c1.getBlue() - c2.getBlue()) <= maxColorDiff && Math.abs(c1.getGreen() - c2.getGreen()) <= maxColorDiff); //check differences in RGB values in order to determine whether the colors of any two pixels match
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		ArrayList<Point> largest = null; //in case there is no largest
		if (regions.size() != 0) {
			int index;
			largest = regions.get(0);
			for (index = 0; index < regions.size(); index++) {
				if (largest.size() < regions.get(index).size()) {
					largest = regions.get(index); //compare initial value with every other value to find the largest
				}
			}
		}
		return largest;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		for (ArrayList<Point> region : regions) {
			Color color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)); //pick a random color
			for (Point point : region) {
				recoloredImage.setRGB(point.x, point.y, color.getRGB());}

		}
	}
}
