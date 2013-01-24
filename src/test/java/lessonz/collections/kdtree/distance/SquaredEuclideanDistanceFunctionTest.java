package lessonz.collections.kdtree.distance;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * System Under Test: {@link SquaredEuclideanDistanceFunction}
 */
public class SquaredEuclideanDistanceFunctionTest {

	private SquaredEuclideanDistanceFunction sut;

	/**
	 * Prepare for each test.
	 */
	@Before
	public void setup() {
		sut = new SquaredEuclideanDistanceFunction();
	}

	/**
	 * Tests {@link SquaredEuclideanDistanceFunction#distance(double[], double[])}.
	 */
	@Test
	public void testDistance() {
		assertEquals(25.0, sut.distance(new double[] { 0.0, 0.0 }, new double[] { 3.0, 4.0 }), 0.0);
		assertEquals(25.0, sut.distance(new double[] { 10.0, 10.0 }, new double[] { 13.0, 14.0 }), 0.0);
		assertEquals(200.0, sut.distance(new double[] { 0.0, 0.0 }, new double[] { 10.0, 10.0 }), 0.0);
		assertEquals(3.0, sut.distance(new double[] { 0.0, 0.0, 0.0 }, new double[] { 1.0, 1.0, 1.0 }), 0.0);
	}

}
