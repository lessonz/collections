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
public class BucketPRKDTree<E extends KDPoint> extends AbstractCollection<E> {

	/**
	 * This bucket size was pretty much chosen at random.
	 */
	private static final int DEFAULT_BUCKET_SIZE = 31;
	private final int bucketSize;
	private BucketPRKDTreeNode<E> node;
	private final int numberOfDimensions;
	private final BucketPRKDKNearestNeighborSearcher<E> searcher = new BucketPRKDKNearestNeighborSearcher<E>(this);

	public BucketPRKDTree(final int numOfDimensions) {
		this(numOfDimensions, DEFAULT_BUCKET_SIZE);
	}

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

	public List<E> getKNearestNeighbors(final int k, final double[] targetCoordinates) {
		return getKNearestNeighbors(k, targetCoordinates, searcher.getDefaultDistanceFunction());
	}

	public List<E> getKNearestNeighbors(final int k, final double[] targetCoordinates,
			final DistanceFunction distanceFunction) {
		searcher.setDistanceFunction(distanceFunction);
		return searcher.getKNearestNeighbors(k, targetCoordinates);
	}

	public List<E> getKNearestNeighbors(final int k, final E target) {
		return getKNearestNeighbors(k, target.getCoordinates());
	}

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
