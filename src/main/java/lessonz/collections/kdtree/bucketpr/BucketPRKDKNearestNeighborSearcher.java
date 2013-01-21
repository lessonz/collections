package lessonz.collections.kdtree.bucketpr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import lessonz.collections.kdtree.KDPoint;
import lessonz.collections.kdtree.distance.DistanceFunction;
import lessonz.collections.kdtree.distance.SquaredEuclideanDistanceFunction;

class BucketPRKDKNearestNeighborSearcher<E extends KDPoint> {

	private static final DistanceFunction DEFAULT_DISTANCE_FUNCTION = new SquaredEuclideanDistanceFunction();

	static DistanceFunction getDefaultDistanceFunction() {
		return DEFAULT_DISTANCE_FUNCTION;
	}

	private int capacity = 0;
	private DistanceFunction distanceFunction = DEFAULT_DISTANCE_FUNCTION;
	private double[] targetCoordinates = new double[0];
	private final BucketPRKDTree<E> tree;

	BucketPRKDKNearestNeighborSearcher(final BucketPRKDTree<E> tree) {
		this.tree = tree;
	}

	private boolean currentElementIsCloser(final double[] testCoordinates, final E farthestNearNeighbor) {
		boolean isCloser = false;

		if (farthestNearNeighbor == null
				|| distanceFunction.distance(testCoordinates, targetCoordinates) < distanceFunction.distance(
						farthestNearNeighbor.getCoordinates(), targetCoordinates)) {
			isCloser = true;
		}

		return isCloser;
	}

	private E getFarthestNearNeighbor(final PriorityQueue<E> nearestNeighborList) {
		E farthestNearNeighbor = null;

		if (nearestNeighborList.size() >= capacity) {
			farthestNearNeighbor = nearestNeighborList.peek();
		}

		return farthestNearNeighbor;
	}

	private PriorityQueue<E> getKNearestNeighbors(final BucketPRKDTreeNode<E> node,
			final PriorityQueue<E> nearestNeighborList, final double[] closestStillPossibleCoordinates) {
		if (node instanceof BucketNode) {
			return getNearestNeighborsFromBucketNode((BucketNode<E>) node, nearestNeighborList);
		} else if (node instanceof SplittingPlaneNode) {
			return getNearestNeighborsFromSplittingPlaneNode((SplittingPlaneNode<E>) node, nearestNeighborList,
					closestStillPossibleCoordinates);
		}

		throw new IllegalArgumentException("The provided BucketPRKDTreeNode is of an unsupported type.");
	}

	private PriorityQueue<E> getNearestNeighborsFromBucketNode(final BucketNode<E> bucketNode,
			final PriorityQueue<E> nearestNeighborList) {
		for (final E e : bucketNode.getElements()) {
			if (currentElementIsCloser(e.getCoordinates(), getFarthestNearNeighbor(nearestNeighborList))) {
				nearestNeighborList.add(e);

				if (nearestNeighborList.size() > capacity) {
					nearestNeighborList.poll();
				}
			}
		}

		return nearestNeighborList;
	}

	private PriorityQueue<E> getNearestNeighborsFromSplittingPlaneNode(final SplittingPlaneNode<E> splittingPlaneNode,
			final PriorityQueue<E> previouslyFoundNearestNeighborList, final double[] closestStillPossibleCoordinates) {
		final int splitDimensionIndex = splittingPlaneNode.getSplitDimensionIndex();
		final double splitDimensionMedian = splittingPlaneNode.getSplitDimensionMedian();

		final double[] fartherClosestStillPossibleCoordinates =
				Arrays.copyOf(closestStillPossibleCoordinates, closestStillPossibleCoordinates.length);
		fartherClosestStillPossibleCoordinates[splitDimensionIndex] = splitDimensionMedian;
		final E farthestNearNeighbor = getFarthestNearNeighbor(previouslyFoundNearestNeighborList);
		if (splitDimensionMedian < closestStillPossibleCoordinates[splitDimensionIndex]) {
			return getNearestNeighborsFromSplittingPlanesTreesNodes(previouslyFoundNearestNeighborList,
					closestStillPossibleCoordinates, fartherClosestStillPossibleCoordinates, farthestNearNeighbor,
					splittingPlaneNode.getRightBucketPRKDTree().getNode(), splittingPlaneNode.getLeftBucketPRKDTree()
							.getNode());
		} else {
			return getNearestNeighborsFromSplittingPlanesTreesNodes(previouslyFoundNearestNeighborList,
					closestStillPossibleCoordinates, fartherClosestStillPossibleCoordinates, farthestNearNeighbor,
					splittingPlaneNode.getLeftBucketPRKDTree().getNode(), splittingPlaneNode.getRightBucketPRKDTree()
							.getNode());
		}
	}

	private PriorityQueue<E> getNearestNeighborsFromSplittingPlanesTreesNodes(
			final PriorityQueue<E> previouslyFoundNearestNeighborList, final double[] closestStillPossibleCoordinates,
			final double[] fartherClosestStillPossibleCoordinates, final E farthestNearNeighbor,
			final BucketPRKDTreeNode<E> closerNode, final BucketPRKDTreeNode<E> fartherNode) {
		PriorityQueue<E> nearestNeighborList = previouslyFoundNearestNeighborList;

		// TODO Should do closer one first and then get the farthestNearNeighbor for comparison.
		if (currentElementIsCloser(fartherClosestStillPossibleCoordinates, farthestNearNeighbor)) {
			nearestNeighborList =
					getKNearestNeighbors(fartherNode, previouslyFoundNearestNeighborList,
							fartherClosestStillPossibleCoordinates);
		}

		return getKNearestNeighbors(closerNode, nearestNeighborList, closestStillPossibleCoordinates);
	}

	List<E> getKNearestNeighbors(final int k, final double[] targetCoordinates) {
		capacity = k;
		this.targetCoordinates = targetCoordinates;
		return new ArrayList<E>(getKNearestNeighbors(tree.getNode(), new PriorityQueue<>(k, new DistanceComparator()),
				this.targetCoordinates));
	}

	void setDistanceFunction(final DistanceFunction distanceFunction) {
		this.distanceFunction = distanceFunction;
	}

	private class DistanceComparator implements Comparator<E> {

		@Override
		public int compare(final E o1, final E o2) {
			final double distanceO1 = distanceFunction.distance(targetCoordinates, o1.getCoordinates());
			final double distanceO2 = distanceFunction.distance(targetCoordinates, o2.getCoordinates());

			final double difference = distanceO2 - distanceO1;
			if (difference < 0.0) {
				return -1;
			} else if (difference > 0.0) {
				return 1;
			} else {
				return 0;
			}
		}

	}

}
