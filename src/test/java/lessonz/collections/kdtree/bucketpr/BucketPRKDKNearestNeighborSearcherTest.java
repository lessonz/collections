package lessonz.collections.kdtree.bucketpr;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lessonz.collections.kdtree.KDPoint;
import lessonz.collections.kdtree.distance.DistanceFunction;
import lessonz.collections.kdtree.distance.SquaredEuclideanDistanceFunction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BucketPRKDKNearestNeighborSearcherTest {

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

	private BucketPRKDKNearestNeighborSearcher<KDPoint> sut;
	private BucketPRKDTree<KDPoint> tree = new BucketPRKDTree<>(TEST_NUMBER_OF_DIMENSIONS, TEST_BUCKET_SIZE);

	@After
	public void cleanup() {
		tree.remove(TEST_ELEMENT_4);
	}

	@Before
	public void setup() {
		for (final KDPoint point : TEST_ELEMENTS) {
			tree.add(point);
		}

		sut = new BucketPRKDKNearestNeighborSearcher<>(tree);
	}

	@Test
	public void testGet1NearestNeighborFromTreeWith3Elements() {
		List<KDPoint> nearestNeighbors = sut.getKNearestNeighbors(1, TEST_ELEMENT_1.getCoordinates());
		assertEquals(1, nearestNeighbors.size());
		assertArrayEquals(TEST_ELEMENT_1.getCoordinates(), nearestNeighbors.get(0).getCoordinates(), 0.0);
		nearestNeighbors = sut.getKNearestNeighbors(1, TEST_ELEMENT_2.getCoordinates());
		assertEquals(1, nearestNeighbors.size());
		assertArrayEquals(TEST_ELEMENT_2.getCoordinates(), nearestNeighbors.get(0).getCoordinates(), 0.0);
		nearestNeighbors = sut.getKNearestNeighbors(1, TEST_ELEMENT_3.getCoordinates());
		assertEquals(1, nearestNeighbors.size());
		assertArrayEquals(TEST_ELEMENT_3.getCoordinates(), nearestNeighbors.get(0).getCoordinates(), 0.0);
	}

	@Test
	public void testGet3NearestNeighborFromTreeWith3ElementsAllInABucketNode() {
		tree = new BucketPRKDTree<>(TEST_NUMBER_OF_DIMENSIONS, TEST_ELEMENTS.size());
		for (final KDPoint point : TEST_ELEMENTS) {
			tree.add(point);
		}
		sut = new BucketPRKDKNearestNeighborSearcher<>(tree);

		final Set<KDPoint> points = new HashSet<KDPoint>(TEST_ELEMENTS);
		final List<KDPoint> nearestNeighbors = sut.getKNearestNeighbors(3, TEST_ELEMENT_4.getCoordinates());
		assertEquals(3, nearestNeighbors.size());
		for (final KDPoint kdPoint : nearestNeighbors) {
			points.remove(kdPoint);
		}

		assertTrue(points.isEmpty());
	}

	@Test
	public void testGet4NearestNeighborFromTreeWith4ElementsWithASplittingPlaneNode() {
		tree.add(TEST_ELEMENT_4);
		final Set<KDPoint> points = new HashSet<KDPoint>(TEST_ELEMENTS);
		points.add(TEST_ELEMENT_4);
		final List<KDPoint> nearestNeighbors = sut.getKNearestNeighbors(4, TEST_ELEMENT_4.getCoordinates());
		assertEquals(4, nearestNeighbors.size());
		for (final KDPoint kdPoint : nearestNeighbors) {
			points.remove(kdPoint);
		}

		assertTrue(points.isEmpty());
	}

	@Test
	public void testGetNearestNeighborReturnsCorrectNeighbor() {
		tree.add(TEST_ELEMENT_4);

		List<KDPoint> nearestNeighbors = sut.getKNearestNeighbors(1, TEST_ELEMENT_1.getCoordinates());
		assertEquals(1, nearestNeighbors.size());
		assertEquals(TEST_ELEMENT_1, nearestNeighbors.get(0));

		nearestNeighbors = sut.getKNearestNeighbors(1, TEST_ELEMENT_2.getCoordinates());
		assertEquals(1, nearestNeighbors.size());
		assertEquals(TEST_ELEMENT_2, nearestNeighbors.get(0));

		nearestNeighbors = sut.getKNearestNeighbors(1, TEST_ELEMENT_3.getCoordinates());
		assertEquals(1, nearestNeighbors.size());
		assertEquals(TEST_ELEMENT_3, nearestNeighbors.get(0));

		nearestNeighbors = sut.getKNearestNeighbors(1, TEST_ELEMENT_4.getCoordinates());
		assertEquals(1, nearestNeighbors.size());
		assertEquals(TEST_ELEMENT_4, nearestNeighbors.get(0));

		nearestNeighbors = sut.getKNearestNeighbors(3, TEST_ELEMENT_3.getCoordinates());
		assertEquals(3, nearestNeighbors.size());
		assertEquals(TEST_ELEMENT_1, nearestNeighbors.get(0));
		assertEquals(TEST_ELEMENT_2, nearestNeighbors.get(1));
		assertEquals(TEST_ELEMENT_3, nearestNeighbors.get(2));
	}

	@Test
	public void testGetNearestNeighborsAreReturnedInOrder() {
		tree.add(TEST_ELEMENT_4);
		final List<KDPoint> nearestNeighbors = sut.getKNearestNeighbors(4, TEST_ELEMENT_4.getCoordinates());
		assertEquals(4, nearestNeighbors.size());

		final DistanceFunction distanceFunction = new SquaredEuclideanDistanceFunction();
		double previousDistance = Double.MAX_VALUE;
		double distance = 0.0;
		for (final KDPoint neighbor : nearestNeighbors) {
			distance = distanceFunction.distance(TEST_ELEMENT_4.getCoordinates(), neighbor.getCoordinates());
			assertTrue(previousDistance > distance);
			previousDistance = distance;
		}
	}

}
