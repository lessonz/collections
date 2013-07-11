package lessonz.collections.kdtree.bucketpr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

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
	private double farthestNearNeighborDistance = Double.POSITIVE_INFINITY;
	private double[] targetCoordinates = new double[0];
	private final BucketPRKDTree<E> tree;
	Queue<E> nearestNeighborList = new PriorityQueue<>(1, new FarthestComparator());

	BucketPRKDKNearestNeighborSearcher(final BucketPRKDTree<E> tree) {
		this.tree = tree;
	}

	List<E> getKNearestNeighbors(final int k, final double[] targetCoordinates) {
		capacity = k;
		this.targetCoordinates = Arrays.copyOf(targetCoordinates, targetCoordinates.length);
		farthestNearNeighborDistance = Double.POSITIVE_INFINITY;

		nearestNeighborList = new PriorityQueue<>(capacity, new FarthestComparator());
		findNearestNeighbors();

		return new ArrayList<E>(nearestNeighborList);
	}

	void setDistanceFunction(final DistanceFunction distanceFunction) {
		this.distanceFunction = distanceFunction;
	}

	private void addNearestNeighbor(final Queue<E> nearestNeighborList, final E e) {
		if (nearestNeighborList.size() >= capacity) {
			nearestNeighborList.poll();
		}

		nearestNeighborList.add(e);

		if (nearestNeighborList.size() >= capacity) {
			farthestNearNeighborDistance =
					distanceFunction.distance(targetCoordinates, nearestNeighborList.peek().getCoordinates());
		} else {
			farthestNearNeighborDistance = Double.POSITIVE_INFINITY;
		}
	}

	private void findNearestNeighbors() {
		final BucketPRKDTreeNode<E> node = tree.getNode();
		if (node instanceof BucketNode) {
			findNearestNeighborsInBucketNode((BucketNode<E>) node);
		} else if (node instanceof SplittingPlaneNode) {
			findNearestNeighborsInSplittingPlaneNode((SplittingPlaneNode<E>) node);
		} else {
			throw new IllegalArgumentException("The provided BucketPRKDTreeNode is of an unsupported type.");
		}
	}

	private void findNearestNeighborsInBucketNode(final BucketNode<E> bucketNode) {
		for (final E e : bucketNode.getElements()) {
			if (testCoordinatesAreCloserThanFarthestNearNeighbor(e.getCoordinates())) {
				addNearestNeighbor(nearestNeighborList, e);
			}
		}
	}

	private void findNearestNeighborsInSplittingPlaneNode(final SplittingPlaneNode<E> parentNode) {
		final Queue<KDPoint<BucketPRKDTreeNode<E>>> fartherNodes =
				new PriorityQueue<KDPoint<BucketPRKDTreeNode<E>>>(capacity, new ClosestComparator());

		double[] closestStillPossibleCoordinates = Arrays.copyOf(targetCoordinates, targetCoordinates.length);
		double[] fartherClosestStillPossibleCoordinates = Arrays.copyOf(targetCoordinates, targetCoordinates.length);
		BucketPRKDTreeNode<E> closerNode = parentNode;
		BucketPRKDTreeNode<E> fartherNode;
		int splitDimensionIndex;
		double splitDimensionMedian;
		SplittingPlaneNode<E> splittingPlaneNode;
		KDPoint<BucketPRKDTreeNode<E>> closestKDPoint;
		while (testCoordinatesAreCloserThanFarthestNearNeighbor(closestStillPossibleCoordinates)) {
			while (closerNode instanceof SplittingPlaneNode) {
				splittingPlaneNode = (SplittingPlaneNode<E>) closerNode;
				splitDimensionIndex = splittingPlaneNode.getSplitDimensionIndex();
				splitDimensionMedian = splittingPlaneNode.getSplitDimensionMedian();

				if (splitDimensionMedian < closestStillPossibleCoordinates[splitDimensionIndex]) {
					closerNode = splittingPlaneNode.getRightBucketPRKDTree().getNode();
					fartherNode = splittingPlaneNode.getLeftBucketPRKDTree().getNode();
				} else {
					closerNode = splittingPlaneNode.getLeftBucketPRKDTree().getNode();
					fartherNode = splittingPlaneNode.getRightBucketPRKDTree().getNode();
				}

				fartherClosestStillPossibleCoordinates =
						Arrays.copyOf(closestStillPossibleCoordinates, closestStillPossibleCoordinates.length);
				fartherClosestStillPossibleCoordinates[splitDimensionIndex] = splitDimensionMedian;
				/*
				 * Direct use of the fartherClosestStillPossibleCoordinates array is only safe because the KDPoint
				 * constructor creates a copy of the coordinate array.
				 */
				fartherNodes
						.add(new KDPoint<BucketPRKDTreeNode<E>>(fartherClosestStillPossibleCoordinates, fartherNode));
			}

			if (closerNode instanceof BucketNode) {
				findNearestNeighborsInBucketNode((BucketNode<E>) closerNode);
			}

			closestKDPoint = fartherNodes.poll();
			if (closestKDPoint != null) {
				closerNode = closestKDPoint.getData();
				closestStillPossibleCoordinates = closestKDPoint.getCoordinates();
			} else {
				return;
			}
		}
	}

	private boolean testCoordinatesAreCloserThanFarthestNearNeighbor(final double[] testCoordinates) {
		boolean isCloser = false;

		if (nearestNeighborList.size() < capacity
				|| distanceFunction.distance(testCoordinates, targetCoordinates) < farthestNearNeighborDistance) {
			isCloser = true;
		}

		return isCloser;
	}

	/**
	 * The {@link ClosestComparator} is used for returning things in order from closest to farthest away from the target
	 * coordinates. That is to say {@link ClosestComparator#compare(KDPoint, KDPoint)} will return as the least element
	 * the one which is closest to the target coordinates.
	 */
	private class ClosestComparator implements Comparator<KDPoint<BucketPRKDTreeNode<E>>> {

		private final FarthestComparator greatestDistanceComparator = new FarthestComparator();

		@Override
		public int compare(final KDPoint<BucketPRKDTreeNode<E>> o1, final KDPoint<BucketPRKDTreeNode<E>> o2) {
			return greatestDistanceComparator.compareCoordinates(o2.getCoordinates(), o1.getCoordinates());
		}

	}

	/**
	 * The {@link FarthestComparator} is used for returning things in order from farthest away to closest to the target
	 * coordinates. That is to say {@link FarthestComparator#compare(KDPoint, KDPoint)} will return as the least element
	 * the one which is farthest away from the target coordinates.
	 */
	private class FarthestComparator implements Comparator<E> {

		@Override
		public int compare(final E o1, final E o2) {
			return compareCoordinates(o1.getCoordinates(), o2.getCoordinates());
		}

		private int compareCoordinates(final double[] coordinates1, final double[] coordinates2) {
			final double distance1 = distanceFunction.distance(targetCoordinates, coordinates1);
			final double distance2 = distanceFunction.distance(targetCoordinates, coordinates2);

			final double difference = distance2 - distance1;
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
