package lessonz.collections.kdtree.bucketpr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import lessonz.collections.kdtree.KDPoint;
import lessonz.collections.kdtree.distance.SquaredEuclideanDistanceFunction;

import org.junit.Before;
import org.junit.Test;

/**
 * System Under Test: {@link BucketPRKDTree}
 */
public class BucketPRKDTreeTest {

	private static final int NUMBER_OF_ELEMENTS_TO_ADD = 1000;
	private static final int TEST_BUCKET_SIZE = 2;
	private static final KDPoint TEST_ELEMENT_1 = new KDPoint(new double[] { 0.0, 0.0, 0.0 }, 1);
	private static final KDPoint TEST_ELEMENT_2 = new KDPoint(new double[] { 1.0, 1.0, 1.0 }, 2);
	private static final KDPoint TEST_ELEMENT_3 = new KDPoint(new double[] { 2.0, 2.0, 2.0 }, 3);
	private static final KDPoint TEST_ELEMENT_4 = new KDPoint(new double[] { -2.0, -2.0, -2.0 }, 4);
	private static final List<KDPoint> TEST_ELEMENTS = new ArrayList<>();
	private static final int TEST_NUMBER_OF_DIMENSIONS = 3;

	static {
		TEST_ELEMENTS.add(TEST_ELEMENT_1);
		TEST_ELEMENTS.add(TEST_ELEMENT_2);
		TEST_ELEMENTS.add(TEST_ELEMENT_3);
	}

	private BucketPRKDTree<KDPoint> sut;

	/**
	 * Prepare for each test.
	 */
	@Before
	public void setup() {
		sut = new BucketPRKDTree<>(TEST_NUMBER_OF_DIMENSIONS, TEST_BUCKET_SIZE);
	}

	/**
	 * Tests {@link BucketPRKDTree#clear()}.
	 */
	@Test
	public void testClear() {
		assertTrue(sut.isEmpty());
		addABunchOfElements();
		assertFalse(sut.isEmpty());

		sut.clear();
		assertTrue(sut.isEmpty());
	}

	/**
	 * Tests {@link BucketPRKDTree#getKNearestNeighbors(int, double[])}.
	 */
	@Test
	public void testGetKNearestNeighborsIntDoubleArray() {
		addTestElements();
		final List<KDPoint> allPoints = new ArrayList<>(TEST_ELEMENTS);

		List<KDPoint> kNearestNeighbors = sut.getKNearestNeighbors(allPoints.size(), TEST_ELEMENT_4.getCoordinates());
		assertEquals(allPoints.size(), kNearestNeighbors.size());
		allPoints.removeAll(kNearestNeighbors);
		assertTrue(allPoints.isEmpty());

		kNearestNeighbors = sut.getKNearestNeighbors(1, TEST_ELEMENT_1.getCoordinates());
		assertEquals(1, kNearestNeighbors.size());
		assertEquals(TEST_ELEMENT_1, kNearestNeighbors.get(0));
	}

	/**
	 * Test
	 * {@link BucketPRKDTree#getKNearestNeighbors(int, double[], lessonz.collections.kdtree.distance.DistanceFunction)}
	 */
	@Test
	public void testGetKNearestNeighborsIntDoubleArrayDistanceFunction() {
		addTestElements();
		final List<KDPoint> allPoints = new ArrayList<>(TEST_ELEMENTS);

		List<KDPoint> kNearestNeighbors =
				sut.getKNearestNeighbors(allPoints.size(), TEST_ELEMENT_4.getCoordinates(),
						new SquaredEuclideanDistanceFunction());
		assertEquals(allPoints.size(), kNearestNeighbors.size());
		allPoints.removeAll(kNearestNeighbors);
		assertTrue(allPoints.isEmpty());

		kNearestNeighbors =
				sut.getKNearestNeighbors(1, TEST_ELEMENT_1.getCoordinates(), new SquaredEuclideanDistanceFunction());
		assertEquals(1, kNearestNeighbors.size());
		assertEquals(TEST_ELEMENT_1, kNearestNeighbors.get(0));
	}

	/**
	 * Tests {@link BucketPRKDTree#getKNearestNeighbors(int, KDPoint)}.
	 */
	@Test
	public void testGetKNearestNeighborsIntE() {
		addTestElements();
		final List<KDPoint> allPoints = new ArrayList<>(TEST_ELEMENTS);

		List<KDPoint> kNearestNeighbors = sut.getKNearestNeighbors(allPoints.size(), TEST_ELEMENT_4);
		assertEquals(allPoints.size(), kNearestNeighbors.size());
		allPoints.removeAll(kNearestNeighbors);
		assertTrue(allPoints.isEmpty());

		kNearestNeighbors = sut.getKNearestNeighbors(1, TEST_ELEMENT_1);
		assertEquals(1, kNearestNeighbors.size());
		assertEquals(TEST_ELEMENT_1, kNearestNeighbors.get(0));
	}

	/**
	 * Tests
	 * {@link BucketPRKDTree#getKNearestNeighbors(int, KDPoint, lessonz.collections.kdtree.distance.DistanceFunction)}
	 */
	@Test
	public void testGetKNearestNeighborsIntEDistanceFunction() {
		addTestElements();
		final List<KDPoint> allPoints = new ArrayList<>(TEST_ELEMENTS);

		List<KDPoint> kNearestNeighbors =
				sut.getKNearestNeighbors(allPoints.size(), TEST_ELEMENT_4, new SquaredEuclideanDistanceFunction());
		assertEquals(allPoints.size(), kNearestNeighbors.size());
		allPoints.removeAll(kNearestNeighbors);
		assertTrue(allPoints.isEmpty());

		kNearestNeighbors = sut.getKNearestNeighbors(1, TEST_ELEMENT_1, new SquaredEuclideanDistanceFunction());
		assertEquals(1, kNearestNeighbors.size());
		assertEquals(TEST_ELEMENT_1, kNearestNeighbors.get(0));
	}

	/**
	 * Tests {@link BucketPRKDTree#iterator()}.
	 */
	@Test
	public void testIterator() {
		addTestElements();
		final List<KDPoint> allPoints = new ArrayList<>(TEST_ELEMENTS);
		assertFalse(allPoints.isEmpty());

		for (final KDPoint kdPoint : sut) {
			allPoints.remove(kdPoint);
		}

		assertTrue(allPoints.isEmpty());
	}

	/**
	 * Tests {@link BucketPRKDTree#remove(Object)}.
	 */
	@Test
	public void testRemove() {
		assertTrue(sut.isEmpty());
		addABunchOfElements();
		assertFalse(sut.isEmpty());

		int numPointsRemoved = 0;
		for (final KDPoint point : TEST_ELEMENTS) {
			while (sut.remove(point)) {
				numPointsRemoved++;
			}
		}
		assertEquals(NUMBER_OF_ELEMENTS_TO_ADD, numPointsRemoved);

		assertTrue(sut.isEmpty());
	}

	private void addABunchOfElements() {
		for (int i = 0; i < NUMBER_OF_ELEMENTS_TO_ADD; i++) {
			sut.add(TEST_ELEMENTS.get(i % TEST_ELEMENTS.size()));
		}
	}

	private void addTestElements() {
		for (final KDPoint point : TEST_ELEMENTS) {
			sut.add(point);
		}
	}

}
