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
			final NearestNeighborList nearestNeighborList, final double[] closestStillPossibleCoordinates) {
		if (node instanceof BucketNode) {
			return getNearestNeighborsFromBucketNode((BucketNode<E>) node, nearestNeighborList);
		} else if (node instanceof SplittingPlaneNode) {
			return getNearestNeighborsFromSplittingPlaneNode((SplittingPlaneNode<E>) node, k, nearestNeighborList,
					closestStillPossibleCoordinates);
		}

		throw new IllegalArgumentException("The provided BucketPRKDTreeNode is of an unsupported type.");
	}

	private NearestNeighborList getNearestNeighborFromSplittingPlanesTreesNodes(final int k,
			final NearestNeighborList previouslyFoundNearestNeighborList,
			final double[] closestStillPossibleCoordinates, NearestNeighborList nearestNeighborList,
			final double[] fartherClosestStillPossibleCoordinates, final E farthestNearNeighbor,
			final BucketPRKDTreeNode<E> closerNode, final BucketPRKDTreeNode<E> fartherNode) {
		if (currentElementIsCloser(fartherClosestStillPossibleCoordinates, farthestNearNeighbor)) {
			nearestNeighborList =
					getKNearestNeighbors(fartherNode, k, previouslyFoundNearestNeighborList,
							fartherClosestStillPossibleCoordinates);
		}

		return getKNearestNeighbors(closerNode, k, nearestNeighborList, closestStillPossibleCoordinates);
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

	private NearestNeighborList getNearestNeighborsFromSplittingPlaneNode(
			final SplittingPlaneNode<E> splittingPlaneNode, final int k,
			final NearestNeighborList previouslyFoundNearestNeighborList, final double[] closestStillPossibleCoordinates) {
		final NearestNeighborList nearestNeighborList = previouslyFoundNearestNeighborList;

		final int splitDimensionIndex = splittingPlaneNode.getSplitDimensionIndex();
		final double splitDimensionMedian = splittingPlaneNode.getSplitDimensionMedian();

		final double[] fartherClosestStillPossibleCoordinates = new double[closestStillPossibleCoordinates.length];
		System.arraycopy(closestStillPossibleCoordinates, 0, fartherClosestStillPossibleCoordinates, 0,
				closestStillPossibleCoordinates.length);
		fartherClosestStillPossibleCoordinates[splitDimensionIndex] = splitDimensionMedian;
		final E farthestNearNeighbor = nearestNeighborList.getFarthestNearNeighbor();
		if (splitDimensionMedian < closestStillPossibleCoordinates[splitDimensionIndex]) {
			return getNearestNeighborFromSplittingPlanesTreesNodes(k, previouslyFoundNearestNeighborList,
					closestStillPossibleCoordinates, nearestNeighborList, fartherClosestStillPossibleCoordinates,
					farthestNearNeighbor, splittingPlaneNode.getRightBucketPRKDTree().getNode(), splittingPlaneNode
							.getLeftBucketPRKDTree().getNode());
		} else {
			return getNearestNeighborFromSplittingPlanesTreesNodes(k, previouslyFoundNearestNeighborList,
					closestStillPossibleCoordinates, nearestNeighborList, fartherClosestStillPossibleCoordinates,
					farthestNearNeighbor, splittingPlaneNode.getLeftBucketPRKDTree().getNode(), splittingPlaneNode
							.getRightBucketPRKDTree().getNode());
		}
	}

	DistanceFunction getDefaultDistanceFunction() {
		return DEFAULT_DISTANCE_FUNCTION;
	}

	List<E> getKNearestNeighbors(final int k, final double[] targetCoordinates) {
		this.targetCoordinates = new double[targetCoordinates.length];
		System.arraycopy(targetCoordinates, 0, this.targetCoordinates, 0, targetCoordinates.length);
		return getKNearestNeighbors(tree.getNode(), k, new NearestNeighborList(k), this.targetCoordinates).toList();
	}

	void setDistanceFunction(final DistanceFunction distanceFunction) {
		this.distanceFunction = distanceFunction;
	}

	private final class NearestNeighborList {

		private final int capacity;
		private E farthestNearNeighbor;
		private final int lastIndex;
		private final List<E> nearestNeighbors;

		private NearestNeighborList(final int capacity) {
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
