package lessonz.collections.kdtree.bucketpr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lessonz.collections.kdtree.KDPoint;

import org.junit.Before;
import org.junit.Test;

/**
 * System Under Test: {@link SplittingPlaneNode}
 */
public class SplittingPlaneNodeTest {

	private static final int TEST_BUCKET_SIZE = 2;
	private static final KDPoint TEST_ELEMENT_1 = new KDPoint(new double[] { 0.0, 0.0, 0.0 }, 1);
	private static final KDPoint TEST_ELEMENT_2 = new KDPoint(new double[] { 1.0, 1.0, 1.0 }, 2);
	private static final KDPoint TEST_ELEMENT_3 = new KDPoint(new double[] { 2.0, 2.0, 2.0 }, 3);
	private static final List<KDPoint> TEST_ELEMENTS = new ArrayList<>();
	private static final int TEST_NUMBER_OF_DIMENSIONS = 3;

	static {
		TEST_ELEMENTS.add(TEST_ELEMENT_1);
		TEST_ELEMENTS.add(TEST_ELEMENT_2);
		TEST_ELEMENTS.add(TEST_ELEMENT_3);
	}

	private SplittingPlaneNode<KDPoint> sut;

	/**
	 * Setup before each test.
	 */
	@Before
	public void setup() {
		sut = new SplittingPlaneNode<KDPoint>(TEST_ELEMENTS, TEST_NUMBER_OF_DIMENSIONS, TEST_BUCKET_SIZE);
	}

	@Test
	public void testAdd() {
		assertEquals(TEST_ELEMENTS.size(), sut.size());
		sut.add(TEST_ELEMENT_1);
		assertEquals(TEST_ELEMENTS.size() + 1, sut.size());
	}

	@Test
	public void testGetSplitDimensionIndex() {
		assertEquals(0, sut.getSplitDimensionIndex());
	}

	@Test
	public void testGetSplitDimensionMedian() {
		assertEquals(1.0, sut.getSplitDimensionMedian(), 0.0);
	}

	@Test
	public void testIterator() {
		final List<KDPoint> allPoints = new ArrayList<>(TEST_ELEMENTS);
		assertFalse(allPoints.isEmpty());

		for (final Iterator<KDPoint> iterator = sut.iterator(); iterator.hasNext();) {
			allPoints.remove(iterator.next());
		}

		assertTrue(allPoints.isEmpty());
	}

}
