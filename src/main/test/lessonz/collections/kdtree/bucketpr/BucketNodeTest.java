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

public class BucketNodeTest {

	private static final int TEST_BUCKET_SIZE = 31;
	private static final int TEST_BUCKET_SIZE_FOR_OVERFLOW = 2;
	private static final KDPoint TEST_ELEMENT_1 = new KDPoint(new double[] { 0.0, 0.0, 0.0 }, 1);
	private static final KDPoint TEST_ELEMENT_2 = new KDPoint(new double[] { 1.0, 1.0, 1.0 },
			TEST_BUCKET_SIZE_FOR_OVERFLOW);
	private static final KDPoint TEST_ELEMENT_3 = new KDPoint(new double[] { 2.0, 2.0, 2.0 }, 3);
	private static final List<KDPoint> TEST_ELEMENTS = new ArrayList<>();
	private static final int TEST_NUMBER_OF_DIMENSIONS = 3;

	static {
		TEST_ELEMENTS.add(TEST_ELEMENT_1);
		TEST_ELEMENTS.add(TEST_ELEMENT_2);
		TEST_ELEMENTS.add(TEST_ELEMENT_3);
	}

	private BucketNode<KDPoint> sut;

	@Before
	public void setup() {
		sut = new BucketNode<>(TEST_NUMBER_OF_DIMENSIONS, TEST_BUCKET_SIZE);
	}

	@Test
	public void testAdd() {
		assertEquals(0, sut.size());
		addTestElements();
		assertEquals(TEST_ELEMENTS.size(), sut.size());
	}

	@Test
	public void testAddWhenBucketOverflows() {
		sut = new BucketNode<>(TEST_NUMBER_OF_DIMENSIONS, TEST_BUCKET_SIZE_FOR_OVERFLOW);
		int i = 0;
		assertEquals(i++, sut.size());

		BucketPRKDTreeNode<KDPoint> node;
		for (final KDPoint point : TEST_ELEMENTS) {
			node = sut.add(point);
			assertEquals(i++, node.size());
		}
	}

	@Test
	public void testAddWhenBucketOverflowsButCantCreateSplittingPlane() {
		sut = new BucketNode<>(TEST_NUMBER_OF_DIMENSIONS, TEST_BUCKET_SIZE_FOR_OVERFLOW);

		BucketPRKDTreeNode<KDPoint> node;
		int i = 0;
		for (; i <= TEST_BUCKET_SIZE_FOR_OVERFLOW; i++) {
			assertEquals(i, sut.size());
			node = sut.add(TEST_ELEMENT_1);
		}
		assertEquals(i, sut.size());
	}

	@Test
	public void testIterator() {
		addTestElements();
		final List<KDPoint> allPoints = new ArrayList<>(TEST_ELEMENTS);
		assertFalse(allPoints.isEmpty());

		for (final Iterator<KDPoint> iterator = sut.iterator(); iterator.hasNext();) {
			allPoints.remove(iterator.next());
		}

		assertTrue(allPoints.isEmpty());
	}

	private void addTestElements() {
		for (final KDPoint point : TEST_ELEMENTS) {
			sut.add(point);
		}
	}

}
