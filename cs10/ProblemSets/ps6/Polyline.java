import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-segment shape, with straight lines connecting joint points.
 * For example, it connects (x1,y1) to (x2,y2) to (x3,y3), and so on.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 */
public class Polyline implements Shape {
	private Color color; // Color of the polyline
	private ArrayList<Point> points; // List of joint points

	public Polyline() {
		this.points = new ArrayList<>();
		this.color = Color.BLACK; // Default color
	}

	public Polyline(Point p, Color color) {
		this();
		this.points.add(p);
		this.color = color;
	}

	/**
	 * Moves the polyline by a specified distance in x and y direction.
	 */
	@Override
	public void moveBy(int dx, int dy) {
		for (Point point : points) {
			point.setLocation(point.x + dx, point.y + dy);
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Checks if a given point is near any segment of the polyline.
	 */
	@Override
	public boolean contains(int x, int y) {
		final double threshold = 10.0;
		for (int i = 0; i < points.size() - 1; i++) {
			if (Segment.pointToSegmentDistance(x, y, points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y) <= threshold) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Draws the polyline by connecting consecutive points.
	 */
	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		for (int i = 0; i < points.size() - 1; i++) {
			g.drawLine(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
		}
	}

	/**
	 * Returns a string representation of the polyline.
	 */
	@Override
	public String toString() {
		String s = "";

		for(Point p:points) {
			s += p.x+","+p.y+",";
		}
		return "polyline"+" "+s+" "+color.getRGB();


	}

	/**
	 * Adds a new point to the end of the polyline.
	 */
	public void addPoint(Point p) {
		points.add(p);
	}
}
