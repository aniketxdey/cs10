/**
 * Refactored test case for the Blob class
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 */
public class BlobDriver2 {
	private Blob blob1, blob2;

	public BlobDriver2(Blob blob1, Blob blob2) {
		this.blob1 = blob1;
		this.blob2 = blob2;
	}

	/**
	 * One test case
	 */
	public void test1() {
		blob1.setR(2 * blob2.getR());
		blob2.setVelocity(3, 4);
		blob2.step();
		blob1.setGrowth(10);
		blob1.step();
		System.out.println("blob1: at "+blob1.getX()+","+blob1.getY()+"; sized "+blob1.getR());
		System.out.println("blob2: at "+blob2.getX()+","+blob2.getY()+"; sized "+blob2.getR());		
	}
	
	public static void main(String[] args) {
		// Create a couple of blobs
		Blob bob = new Blob(10,20);
		Blob alice = new Blob(30,40);
		// Create the test driver with them
		BlobDriver2 driver = new BlobDriver2(bob, alice);
		// Test
		driver.test1();
		// Could insert more test cases....
	}
}
