package lessonz.collections.kdtree.bucketpr;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;

import lessonz.collections.kdtree.KDPoint;
import lessonz.collections.kdtree.distance.DistanceFunction;

/**
 * The {@link BucketPRKDTree} is a Bucket, Point-Region, K-Dimensional Tree, which is to say it is a k-d tree (<a
 * href="https://en.wikipedia.org/wiki/Kd-tree">Wikipedia Reference</a>) that splits nodes into point regions and stores
 * leaf entries in a bucket until the bucket overflows at which time the dimension with the greatest variance within the
 * bucket will be used to split the bucket into two point regions.
 * 
 * @param <E>
 *            In order for the {@link BucketPRKDTree} to properly place items within its k-dimensional space elements
 *            must be or extend {@link KDPoint}.
 */
public class BucketPRKDTree<E extends KDPoint<?>> extends AbstractCollection<E> {

	/**
	 * This bucket size was pretty much chosen at random.
	 */
	private static final int DEFAULT_BUCKET_SIZE = 31;
	private final int bucketSize;
	private BucketPRKDTreeNode<E> node;
	private final int numberOfDimensions;
	private final BucketPRKDKNearestNeighborSearcher<E> searcher = new BucketPRKDKNearestNeighborSearcher<E>(this);

	/**
	 * Constructs a {@link BucketPRKDTree} with the default bucket size.
	 * 
	 * @param numOfDimensions
	 *            the number of dimensions of this {@link BucketPRKDTree}.
	 */
	public BucketPRKDTree(final int numOfDimensions) {
		this(numOfDimensions, DEFAULT_BUCKET_SIZE);
	}

	/**
	 * Constructs a {@link BucketPRKDTree} with the specified number of dimensions and bucket size.
	 * 
	 * @param numberOfDimensions
	 *            the number of dimensions of this {@link BucketPRKDTree}.
	 * @param bucketSize
	 *            the bucket size of this {@link BucketPRKDTree}.
	 */
	public BucketPRKDTree(final int numberOfDimensions, final int bucketSize) {
		this.numberOfDimensions = numberOfDimensions;
		this.bucketSize = bucketSize;
		node = new BucketNode<E>(numberOfDimensions, bucketSize);
	}

	@Override
	public boolean add(final E e) {
		node = node.add(e);

		return true;
	}

	@Override
	public void clear() {
		/* TODO Ensure this doesn't cause a memory leak. */
		node = new BucketNode<E>(numberOfDimensions, bucketSize);
	}

	/**
	 * Finds up to the specified number of elements closest to the targeted coordinates. If there are at least k
	 * elements, k elements will be returned. If there are fewer, all elements will be returned. No ordering of the
	 * returned list is implied. The default function is used to determine point proximity.
	 * 
	 * @param k
	 *            the number of neighbors for which to search.
	 * @param targetCoordinates
	 *            the coordinates near which to search.
	 * @return the nearest neighbors found.
	 */
	public List<E> getKNearestNeighbors(final int k, final double[] targetCoordinates) {
		return getKNearestNeighbors(k, targetCoordinates,
				BucketPRKDKNearestNeighborSearcher.getDefaultDistanceFunction());
	}

	/**
	 * Finds up to the specified number of elements closest to the targeted coordinates. If there are at least k
	 * elements, k elements will be returned. If there are fewer, all elements will be returned. No ordering of the
	 * returned list is implied.
	 * 
	 * @param k
	 *            the number of neighbors for which to search.
	 * @param targetCoordinates
	 *            the coordinates near which to search.
	 * @param distanceFunction
	 *            the {@link DistanceFunction} to be used in determining proximity.
	 * @return the nearest neighbors found.
	 */
	public List<E> getKNearestNeighbors(final int k, final double[] targetCoordinates,
			final DistanceFunction distanceFunction) {
		searcher.setDistanceFunction(distanceFunction);
		return searcher.getKNearestNeighbors(k, targetCoordinates);
	}

	/**
	 * Finds up to the specified number of elements closest to the targeted coordinates. If there are at least k
	 * elements, k elements will be returned. If there are fewer, all elements will be returned. No ordering of the
	 * returned list is implied. The default function is used to determine point proximity.<br>
	 * <br>
	 * This is a convenience method and is the equivalent of calling BucketPRKDTree.getKNearestNeighbors(int,
	 * target.getCoordinates()). This also means whether or not the target element is in the {@link BucketPRKDTree} has
	 * no impact on the returned {@link List}.
	 * 
	 * @param k
	 *            the number of neighbors for which to search.
	 * @param target
	 *            the element near which to search.
	 * @return the nearest neighbors found.
	 */
	public List<E> getKNearestNeighbors(final int k, final E target) {
		return getKNearestNeighbors(k, target.getCoordinates());
	}

	/**
	 * Finds up to the specified number of elements closest to the targeted coordinates. If there are at least k
	 * elements, k elements will be returned. If there are fewer, all elements will be returned. No ordering of the
	 * returned list is implied.
	 * 
	 * @param k
	 *            the number of neighbors for which to search.
	 * @param target
	 *            the element near which to search.
	 * @param distanceFunction
	 *            the {@link DistanceFunction} to be used in determining proximity.
	 * @return the nearest neighbors found.
	 */
	public List<E> getKNearestNeighbors(final int k, final E target, final DistanceFunction distanceFunction) {
		return getKNearestNeighbors(k, target.getCoordinates(), distanceFunction);
	}

	@Override
	public Iterator<E> iterator() {
		return node.iterator();
	}

	@Override
	public int size() {
		return node.size();
	}

	BucketPRKDTreeNode<E> getNode() {
		return node;
	}
}
