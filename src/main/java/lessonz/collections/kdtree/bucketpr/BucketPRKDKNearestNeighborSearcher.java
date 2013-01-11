package lessonz.collections.kdtree.bucketpr;

import java.util.ArrayList;
import java.util.List;

import lessonz.collections.kdtree.KDPoint;
import lessonz.collections.kdtree.distance.DistanceFunction;
import lessonz.collections.kdtree.distance.SquaredEuclideanDistanceFunction;

class BucketPRKDKNearestNeighborSearcher<E extends KDPoint> {

	private static final DistanceFunction DEFAULT_DISTANCE_FUNCTION = new SquaredEuclideanDistanceFunction();
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

	private NearestNeighborList getKNearestNeighbors(final BucketPRKDTreeNode<E> node, final int k,
			NearestNeighborList nearestNeighborList, final double[] closestStillPossibleCoordinates) {
		if (node instanceof BucketNode) {
			nearestNeighborList = getNearestNeighborsFromBucketNode((BucketNode<E>) node, nearestNeighborList);
		} else if (node instanceof SplittingPlaneNode) {
			nearestNeighborList =
					getNearestNeighborsFromSplittingPlaneNode((SplittingPlaneNode<E>) node, k, nearestNeighborList,
							closestStillPossibleCoordinates);
		}

		return nearestNeighborList;
	}

	private NearestNeighborList getNearestNeighborsFromBucketNode(final BucketNode<E> bucketNode,
			final NearestNeighborList nearestNeighborList) {
		for (final E e : bucketNode.getElements()) {
			if (currentElementIsCloser(e.getCoordinates(), nearestNeighborList.getFarthestNearNeighbor())) {
				nearestNeighborList.add(e);
			}
		}

		return nearestNeighborList;
	}

	private final NearestNeighborList getNearestNeighborsFromSplittingPlaneNode(
			final SplittingPlaneNode<E> splittingPlaneNode, final int k, NearestNeighborList nearestNeighborList,
			final double[] closestStillPossibleCoordinates) {
		final int splitDimensionIndex = splittingPlaneNode.getSplitDimensionIndex();
		final double splitDimensionMedian = splittingPlaneNode.getSplitDimensionMedian();

		final double[] splittingPlaneCoordinates = new double[closestStillPossibleCoordinates.length];
		System.arraycopy(closestStillPossibleCoordinates, 0, splittingPlaneCoordinates, 0,
				closestStillPossibleCoordinates.length);
		splittingPlaneCoordinates[splitDimensionIndex] = splitDimensionMedian;
		final E farthestNearNeighbor = nearestNeighborList.getFarthestNearNeighbor();
		if (splitDimensionMedian < closestStillPossibleCoordinates[splitDimensionIndex]) {
			if (currentElementIsCloser(splittingPlaneCoordinates, farthestNearNeighbor)) {
				nearestNeighborList =
						getKNearestNeighbors(splittingPlaneNode.getLeftBucketPRKDTree().getNode(), k,
								nearestNeighborList, splittingPlaneCoordinates);
			}

			nearestNeighborList =
					getKNearestNeighbors(splittingPlaneNode.getRightBucketPRKDTree().getNode(), k, nearestNeighborList,
							closestStillPossibleCoordinates);
		} else {
			nearestNeighborList =
					getKNearestNeighbors(splittingPlaneNode.getLeftBucketPRKDTree().getNode(), k, nearestNeighborList,
							closestStillPossibleCoordinates);

			if (currentElementIsCloser(splittingPlaneCoordinates, farthestNearNeighbor)) {
				nearestNeighborList =
						getKNearestNeighbors(splittingPlaneNode.getRightBucketPRKDTree().getNode(), k,
								nearestNeighborList, splittingPlaneCoordinates);
			}
		}

		return nearestNeighborList;
	}

	DistanceFunction getDefaultDistanceFunction() {
		return DEFAULT_DISTANCE_FUNCTION;
	}

	List<E> getKNearestNeighbors(final int k, final double[] targetCoordinates) {
		this.targetCoordinates = targetCoordinates;
		return getKNearestNeighbors(tree.getNode(), k, new NearestNeighborList(k, distanceFunction), targetCoordinates)
				.toList();
	}

	void setDistanceFunction(final DistanceFunction distanceFunction) {
		this.distanceFunction = distanceFunction;
	}

	private class NearestNeighborList {

		private final int capacity;
		private E farthestNearNeighbor;
		private final int lastIndex;
		private final List<E> nearestNeighbors;

		private NearestNeighborList(final int capacity, final DistanceFunction distanceFunction) {
			this.capacity = capacity;
			nearestNeighbors = new ArrayList<>(capacity);
			this.lastIndex = capacity - 1;

		}

		private void add(final E e) {
			if (nearestNeighbors.size() >= capacity) {
				nearestNeighbors.remove(lastIndex);
			}

			boolean addedElement = false;
			E nearestNeighbor;
			for (int i = 0; i < nearestNeighbors.size(); i++) {
				nearestNeighbor = nearestNeighbors.get(i);
				if (nearestNeighbor == null
						|| distanceFunction.distance(e.getCoordinates(), targetCoordinates) < distanceFunction
								.distance(nearestNeighbor.getCoordinates(), targetCoordinates)) {
					nearestNeighbors.add(i, e);
					addedElement = true;
					break;
				}
			}

			if (!addedElement) {
				nearestNeighbors.add(e);
			}

			if (nearestNeighbors.size() == capacity) {
				farthestNearNeighbor = nearestNeighbors.get(lastIndex);
			}
		}

		private E getFarthestNearNeighbor() {
			return farthestNearNeighbor;
		}

		private List<E> toList() {
			return nearestNeighbors;
		}

	}
}
