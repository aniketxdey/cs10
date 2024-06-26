import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * A blob that moves randomly and pulsates (radius increases/decreases).
 */
public class WanderingPulsar extends Wanderer {
	public WanderingPulsar(double x, double y) {
		super(x, y);
		dr = 1 + Math.random();
	}
	
	@Override
	public void step() {
		// Do the wanderer step.
		super.step();
		// Now increment radius and make sure it is within bounds.
		r += dr;
		if (r < 1 || r > 10) {
			dr = -dr;
			r += dr;
		}
	}

    /**
     * A class demonstrating manipulation of image pixels.
     *
     * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
     * @author CBK, Winter 2014, rewritten for BufferedImage
     * @author CBK, Spring 2015, refactored to separate GUI from operations
     * @author Tim Pierson, Winter 2023, fixed draw square
     */
    public static class ImageProcessor {
        private BufferedImage image;		// the current image being processed

        /**
         * @param image		the original
         */
        public ImageProcessor(BufferedImage image) {
            this.image = image;
        }

        public BufferedImage getImage() {
            return image;
        }

        public void setImage(BufferedImage image) {
            this.image = image;
        }

        /**
         * Returns a value that is one of val (if it's between min or max) or min or max (if it's outside that range).
         * @param val
         * @param min
         * @param max
         * @return constrained value
         */
        public static double constrain(double val, double min, double max) {
            if (val < min) {
                return min;
            }
            else if (val > max) {
                return max;
            }
            return val;
        }

        /**
         * Returns an image that's the same size & type as the current, but blank
         */
        private BufferedImage createBlankResult() {
            // Create a new image into which the resulting pixels will be stored.
            return new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        /**
         * Returns an image that's a copy of the current one
         */
        private BufferedImage createCopyResult() {
            return new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
        }

        /**
         * Blurs the current image by setting each pixel's values to the average of those in a radius-sized box around it.
         * Use a smallish box (e.g., 1) unless the image is small or you're willing to wait a while.
         * @param radius		size of box; e.g., 1 indicates +-1 around the pixel
         */
        public void average(int radius) {
            // Create a new image into which the resulting pixels will be stored.
            BufferedImage result = createBlankResult();

            // Nested loop over every pixel
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int sumR = 0, sumG = 0, sumB = 0;
                    int n = 0;
                    // Nested loop over neighbors
                    // but be careful not to go outside image (max, min stuff).
                    for (int ny = Math.max(0, y - radius);
                            ny < Math.min(image.getHeight(), y + 1 + radius);
                            ny++) {
                        for (int nx = Math.max(0, x - radius);
                                nx < Math.min(image.getWidth(), x + 1 + radius);
                                nx++) {
                            // Add all the neighbors (& self) to the running totals
                            Color c = new Color(image.getRGB(nx, ny));
                            sumR += c.getRed();
                            sumG += c.getGreen();
                            sumB += c.getBlue();
                            n++;
                        }
                    }
                    Color newColor = new Color(sumR/n, sumG/n, sumB/n);
                    result.setRGB(x, y, newColor.getRGB());
                }
            }

            // Make the current image be this new image.
            image = result;
        }

        /**
         * Brightens the current image by scaling up pixel values.
         */
        public void brighten() {
            // Nested loop over every pixel
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    // Get current color; scale each channel (but don't exceed 255); put new color
                    Color color = new Color(image.getRGB(x, y));
                    int red = Math.min(255, color.getRed()*4/3);
                    int green = Math.min(255, color.getGreen()*4/3);
                    int blue = Math.min(255, color.getBlue()*4/3);
                    Color newColor = new Color(red, green, blue);
                    image.setRGB(x, y, newColor.getRGB());
                }
            }
        }

        /**
         * Dims the current image by scaling down pixel values.
         */
        public void dim() {
            // Nested loop over every pixel
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    // Get current color; scale each channel; put new color
                    Color color = new Color(image.getRGB(x, y));
                    int red = color.getRed() * 3/4;
                    int green = color.getGreen() * 3/4;
                    int blue = color.getBlue() * 3/4;
                    Color newColor = new Color(red, green, blue);
                    image.setRGB(x, y, newColor.getRGB());
                }
            }
        }

        /**
         * Dims/brightens the current image by scaling each pixel value by the specified amount.
         * @param scaleR/B/G		how much to scale the pixel values
         */
        public void scaleColor(double scaleR, double scaleG, double scaleB) {
            // Nested loop over every pixel
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    // Get current color; scale each channel (but don't exceed 255); put new color
                    Color color = new Color(image.getRGB(x, y));
                    int red = (int)(Math.min(255, color.getRed()*scaleR));
                    int green = (int)(Math.min(255, color.getGreen()*scaleG));
                    int blue = (int)(Math.min(255, color.getBlue()*scaleB));
                    Color newColor = new Color(red, green, blue);
                    image.setRGB(x, y, newColor.getRGB());
                }
            }
        }

        /**
         * Flips the current image upside down.
         */
        public void flip() {
            // Create a new image into which the resulting pixels will be stored.
            BufferedImage result = createBlankResult();

            // Nested loop over every pixel
            for (int y = 0; y < image.getHeight(); y++) { //row to pick up color
                int y2 = image.getHeight() - 1 - y; // row to write result, note that indices go 0..height-1
                for (int x = 0; x < image.getWidth(); x++) { //column to pick up color
                    int color = image.getRGB(x, y); //get color from x,y
                    result.setRGB(x, y2, color); //set color to x,y2
                }
            }

            // Make the current image be this new image.
            image = result;
        }

        /**
         * flips image without a second copy of the image (like flip() does)
         */
        public void flip2() {
            // Nested loop over every pixel
            for (int y = 0; y <= image.getHeight()/2; y++) { //row to pick up color
                int y2 = image.getHeight() - 1 - y; // row to write result, note that indices go 0..height-1
                for (int x = 0; x < image.getWidth(); x++) { //column to pick up color
                    int a = image.getRGB(x, y); //get color from x,y; a
                    int b = image.getRGB(x,y2); //get color from x,y2; b
                    int temp = a; //temp = a
                    image.setRGB(x, y, b); //set color at x,y to b; a=b
                    image.setRGB(x,y2,temp); //set color at x,y2 to temp=a
                }
            }

        }

        /**
         * Computes the luminosity of an rgb value by one standard formula.
         * @param r		red value (0-255)
         * @param g		green value (0-255)
         * @param b		blue value (0-255)
         * @return		luminosity (0-255)
         */
        public static int luminosity(int r, int g, int b) {
            return (int)(0.299 * r + 0.587 * g + 0.114 * b);
        }

        /**
         * Makes the current image look grayscale (though still represented as RGB).
         */
        public void gray() {
            // Nested loop over every pixel
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    // Get current color; set each channel to luminosity
                    Color color = new Color(image.getRGB(x, y));
                    int gray = luminosity(color.getRed(), color.getGreen(), color.getBlue());
                    // Put new color
                    Color newColor = new Color(gray, gray, gray);
                    image.setRGB(x, y, newColor.getRGB());
                }
            }
        }

        /**
         * Sharpens the current image by setting each pixel's values to subtract out those in a radius-sized box around it.
         * Use a smallish box (e.g., 1) unless the image is small or you're willing to wait a while.
         * @param radius		size of box; e.g., 1 indicates +-1 around the pixel
         */
        public void sharpen(int radius) {
            // Create a new image into which the resulting pixels will be stored.
            BufferedImage result = createBlankResult();

            // Nested loop over every pixel
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int sumR = 0, sumG = 0, sumB = 0;
                    int n = 0;
                    // Nested loop over neighbors
                    // but be careful not to go outside image (max, min stuff).
                    for (int ny = Math.max(0, y - radius);
                            ny < Math.min(image.getHeight(), y + radius);
                            ny++) {
                        for (int nx = Math.max(0, x - radius);
                                nx < Math.min(image.getWidth(), x + radius);
                                nx++) {
                            // Add all the neighbors (but not self) to the running totals
                            if (nx != x || ny != y) {
                                Color c = new Color(image.getRGB(nx, ny));
                                sumR += c.getRed();
                                sumG += c.getGreen();
                                sumB += c.getBlue();
                                n++;
                            }
                        }
                    }
                    // Weighted center pixel minus sum of neighbors
                    Color c = new Color(image.getRGB(x, y));
                    int red = (int)constrain(c.getRed()*(n+1) - sumR, 0, 255);
                    int green = (int)constrain(c.getGreen()*(n+1) - sumG, 0, 255);
                    int blue = (int)constrain(c.getBlue()*(n+1) - sumB, 0, 255);
                    Color newColor = new Color(red, green, blue);
                    result.setRGB(x, y, newColor.getRGB());
                }
            }

            // Make the current image be this new image.
            image = result;
        }

        /**
         * Adds random noise to each pixel.
         * @param scale		maximum value of the noise to be added
         */
        public void noise(double scale) {
            // Nested loop over every pixel
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    // Get current color; add noise to each channel
                    Color color = new Color(image.getRGB(x, y));
                    int red = (int)(constrain(color.getRed() + scale * (2*Math.random() - 1), 0, 255));
                    int green = (int)(constrain(color.getGreen() + scale * (2*Math.random() - 1), 0, 255));
                    int blue = (int)(constrain(color.getBlue() + scale * (2*Math.random() - 1), 0, 255));
                    // Put new color
                    Color newColor = new Color(red, green, blue);
                    image.setRGB(x, y, newColor.getRGB());
                }
            }
        }

        /**
         * Scrambles the current image by setting each pixel from some nearby pixel.
         * @param radius		maximum distance (+- that amount in x and y) of "nearby"
         */
        public void scramble(int radius) {
            // Create a new image into which the resulting pixels will be stored.
            BufferedImage result = createBlankResult();

            // Nested loop over every pixel
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    // Random neighbors in x and y; constrain to image size
                    int nx = (int) constrain(x + radius * (2*Math.random() - 1), 0, image.getWidth() - 1);
                    int ny = (int) constrain(y + radius * (2*Math.random() - 1), 0, image.getHeight() - 1);
                    result.setRGB(x, y, image.getRGB(nx, ny));
                }
            }

            // Make the current image be this new image.
            image = result;
        }

        /**
         * Updates the image with a square at the location, colored the given color.
         *
         * @param cx	  center x of square
         * @param cy	  center y of square
         * @param r   radius of square
         * @param color  fill
         */
        public void drawSquare(int cx, int cy, int r, Color color) {
            // Nested loop over nearby pixels
            for (int y = Math.max(0, cy-r); y < Math.min(image.getHeight(), cy+r+1); y++) {
                for (int x = Math.max(0, cx-r); x < Math.min(image.getWidth(), cx+r+1); x++) {
                    image.setRGB(x, y, color.getRGB());
                }
            }
        }

        /**
         * Updates the image with a lens magnifying effect
         *
         * @param cx		center x of lens
         * @param cy		center y of lens
         * @param r  	radius of lens
         */
        public void lens(int cx, int cy, int r) {
            // Create a copy of the current image - need to be able to look at current as modify it
            BufferedImage result = createCopyResult();

            // Nested loop over every pixel
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    // Only do lens out to specified radius.
                    double dist = 0.1* Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy)) / r;
                    if (dist <= 1) {
                        // Determine source pixel by lens function, but constrain to image size.
                        int sx = (int) constrain(x + ((x - cx) * dist), 0, image.getWidth() - 1);
                        int sy = (int) constrain(y + ((y - cy) * dist), 0, image.getHeight() - 1);
                        result.setRGB(x, y, image.getRGB(sx, sy));
                    }
                }
            }

            // Make the current image be this new image.
            image = result;
        }

        public void drawCircle(int x, int y, int r, Color color) {
            double pi = 3.1415926535;
            double angle, x1, y1;
            for(double i = 0; i < 360; i += 0.1)
            {
                angle = i;
                x1 = r * Math.cos(angle * pi / 180);
                y1 = r * Math.sin(angle * pi / 180);
                image.setRGB((int)(x + x1), (int)(y + y1), color.getRGB());
                }
            }

    }

    /**
     * A simple JFrame containing a single component for drawing (via the draw method)
     *
     * @author Chris Bailey-Kellogg, Dartmouth CS 10, Winter 2014
     * @author CBK, Spring 2015, revised to factor out the image, provide simple event handling
     */
    public static class DrawingGUI extends JFrame {
        public JComponent canvas;						// handles graphics display
        protected int width, height;					// the size of the drawing window
        protected Timer timer;							// one delay-driven event
        private static final int delay = 100;			// default delay for the timer (milliseconds)

        /**
         * Creates an empty, title-less frame.
         * Call initWindow to finish it off.
         */
        public DrawingGUI() {
            super("");
        }

        /**
         * Creates an empty frame.
         * Call initWindow to finish it off.
         */
        public DrawingGUI(String title) {
            super(title);
        }

        /**
         * Creates a frame and finishes initializing it.
         *
         * @param title		displayed in window title bar
         * @param width		window size
         * @param height	window size
         */
        public DrawingGUI(String title, int width, int height) {
            super(title);
            initWindow(width, height);
        }

        /**
         * Finishes initializing the GUI for the fixed size.
         *
         * @param width		window size
         * @param height	window size
         */
        protected void initWindow(int width, int height) {
            this.width = width;
            this.height = height;

            // Create a canvas for drawing into, paintComponent() is called on repaint, add our object's draw function
            canvas = new JComponent() {
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    draw(g);  //our object's drawing method call
                }
            };

            // Listen for events
            canvas.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent event) {
                    handleMousePress(event.getPoint().x, event.getPoint().y);
                }
            });
            canvas.addMouseMotionListener(new MouseAdapter() {
                public void mouseMoved(MouseEvent event) {
                    handleMouseMotion(event.getPoint().x, event.getPoint().y);
                }
            });
            addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent event) {
                    handleKeyPress(event.getKeyChar());
                }
            });
            timer = new Timer(delay, new AbstractAction("update") {
                public void actionPerformed(ActionEvent e) {
                    handleTimer();
                }
            });

            // Boilerplate to finish initializing the GUI to the specified size
            setSize(width, height);
            canvas.setPreferredSize(new Dimension(width, height));
            getContentPane().add(canvas);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setVisible(true);
        }

        /**
         * Start the timer running.
         */
        public void startTimer() {
            timer.start();
        }

        /**
         * Stops the timer.
         */
        public void stopTimer() {
            timer.stop();
        }

        /**
         * Set the delay of the timer.
         * @param delay
         */
        public void setTimerDelay(int delay) {
            timer.setDelay(delay);
        }

        /**
         * Method to draw in the canvas, to be overridden by subclasses.
         * @param g
         */
        public void draw(Graphics g) {
        }

        /**
         * Method to handle a mouse press, to be overridden by subclasses.
         * @param x		x coordinate of mouse press
         * @param y		y coordinate of mouse press
         */
        public void handleMousePress(int x, int y) {
        }

        /**
         * Method to handle mouse motion, to be overridden by subclasses.
         * @param x		x coordinate of mouse
         * @param y		y coordinate of mouse
         */
        public void handleMouseMotion(int x, int y) {
        }

        /**
         * Method to handle a key press, to be overridden by subclasses.
         * @param key	the key that was pressed
         */
        public void handleKeyPress(char key) {
        }

        /**
         * Method to respond to the timer going off, to be overridden by subclasses.
         */
        public void handleTimer() {
        }

        /**
         *
         * @param filename	for the image
         */
        public static BufferedImage loadImage(String filename) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(new File(filename));
            }
            catch (Exception e) {
                System.err.println("Couldn't load image from `"+filename+"' -- make sure the file exists in that folder");
                System.exit(-1);
            }
            return image;
        }

        public static void saveImage(BufferedImage image, String filename, String format) {
            try {
                ImageIO.write(image, format, new File(filename));
                System.out.println("Saved a snapshot in "+filename);
            }
            catch (Exception e) {
                System.err.println("Couldn't save snapshot in `"+filename+"' -- make sure the folder exists");
            }
        }
    }
}
