import java.awt.Color;
import java.awt.Point;

public class Message {
    private Shape curr = null; // Current shape being drawn
    private Sketch sketch;
    String[] tokens; // Array to store parts of the split message
    String msg; // Message string

    public Message() {
    }

    // Constructor that takes in a command
    public Message(Editor e, String msg, Sketch sketch) {
        this.msg = msg;
        this.sketch = sketch;
    }

    // Method to edit an input sketch with a command
    public int update(String msg, Sketch sketch) {
        Sketch temp = new Sketch();
        // Split the message into tokens
        tokens = msg.split(" ");

        // Check if the first token equals the command
        if (tokens[0].equals("draw")) {
            if(tokens[1].equals("polyline")){
                String[]points = tokens[2].split(",");
                System.out.println(points);

                curr=new Polyline(new Point(Integer.parseInt(points[0]),Integer.parseInt(points[1])),new Color(Integer.parseInt(tokens[3])));
                for(int i=0;i<points.length;i=i+2) {
                    ((Polyline) curr).addPoint(new Point(Integer.parseInt(points[i]),Integer.parseInt(points[i+1])));
                }
            }
            else if (tokens[1].equals("ellipse")) {
                curr = new Ellipse(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), new Color(Integer.parseInt(tokens[6])));
            }
            else if (tokens[1].equals("rectangle")) {
                curr = new Rectangle(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), new Color(Integer.parseInt(tokens[6])));
            }
            else if (tokens[1].equals("segment")) {
                curr = new Segment(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), new Color(Integer.parseInt(tokens[6])));
            }
            sketch.addShape(curr);
        }

        else if (tokens[0].equals("move")) {
            curr = sketch.getShape(Integer.parseInt(tokens[1]));
            if (curr != null) {
                curr.moveBy(Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));
            }
        }

        else if (tokens[0].equals("delete")) {
            int id = Integer.parseInt(tokens[1]);
            curr = sketch.getShape(id);
            sketch.removeShape(id);
        }

        else if (tokens[0].equals("recolor")) {
            curr = sketch.getShape(Integer.parseInt(tokens[1]));
            curr.setColor(new Color(Integer.parseInt(tokens[2])));
        }

        else if (tokens[0].equals("update")) {
            if(tokens[2].equals("polyline")){
                String[]points = tokens[3].split(",");
                System.out.println(points);

                curr=new Polyline(new Point(Integer.parseInt(points[0]),Integer.parseInt(points[1])),new Color(Integer.parseInt(tokens[4])));
                for(int i=0;i<points.length;i=i+2) {
                    ((Polyline) curr).addPoint(new Point(Integer.parseInt(points[i]),Integer.parseInt(points[i+1])));
                }
                sketch.addShape(Integer.parseInt(tokens[5]),curr);
            }
            else if (tokens[2].equals("ellipse")) {
                curr = new Ellipse(Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]), new Color(Integer.parseInt(tokens[7])));
                sketch.addShape(Integer.parseInt(tokens[8]), curr);
            }

            else if (tokens[2].equals("rectangle")) {
                curr = new Rectangle(Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]), new Color(Integer.parseInt(tokens[7])));
                sketch.addShape(Integer.parseInt(tokens[8]), curr);
            }

            else if (tokens[2].equals("segment")) {
                curr = new Segment(Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]), new Color(Integer.parseInt(tokens[7])));
                sketch.addShape(Integer.parseInt(tokens[8]), curr);
            }
        }

        else if (tokens[0].equals("start")) {
            temp = new Sketch();
            sketch = temp;
        }

        return sketch.getShapeId(curr);
    }
}
