import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a drawing canvas that manages and renders shapes.
 * Shapes are stored in a TreeMap to maintain order and facilitate rendering.
 */
public class Sketch {
    private TreeMap<Integer, Shape> shapes;
    private int nextId;

    public Sketch() {
        shapes = new TreeMap<>();
        nextId = 0;
    }

    /**
     * Adds a shape to the sketch without a predefined ID.
     */
    public synchronized int addShape(Shape shape) {
        shapes.put(nextId, shape);
        return nextId++;
    }

    /**
     * Adds a shape with a specific ID.
     */
    public synchronized void addShape(int id, Shape shape) {
        shapes.put(id, shape);
    }

    /**
     * Retrieves a shape by its ID.
     */
    public synchronized Shape getShape(int id) {
        return shapes.get(id);
    }

    /**
     * Determines which shape, if any, contains the specified point.
     */
    public synchronized Shape contains(Point p) {
        for (Integer id : shapes.descendingKeySet()) {
            if (shapes.get(id).contains(p.x, p.y)) {
                return shapes.get(id);
            }
        }
        return null;
    }

    /**
     * Draws all the shapes in the sketch.
     */
    public synchronized void draw(Graphics g) {
        for (Shape shape : shapes.values()) {
            shape.draw(g);
        }
    }

    /**
     * Removes a shape by its ID.
     */
    public synchronized void removeShape(int id) {
        shapes.remove(id);
    }

    /**
     * Gets the ID of a given shape.
     */
    public synchronized int getShapeId(Shape shape) {
        for (Map.Entry<Integer, Shape> entry : shapes.entrySet()) {
            if (entry.getValue().equals(shape)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns all the shapes in the sketch.
     */
    public synchronized TreeMap<Integer, Shape> getShapes() {
        return new TreeMap<>(shapes);
    }

    /**
     * Empties all the shapes from the sketch.
     */
    public synchronized void clear() {
        shapes.clear();
    }
}
