package lessonz.collections.kdtree.bucketpr;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import lessonz.collections.kdtree.KDPoint;

class SplittingPlaneNode<E extends KDPoint> implements BucketPRKDTreeNode<E> {

	private final BucketPRKDTree<E> left, right;
	private int splitDimensionIdex;
	private double splitDimensionMedian;

	public SplittingPlaneNode(final List<E> elements, final int numberOfDimensions, final int bucketSize) {
		left = new BucketPRKDTree<>(numberOfDimensions, bucketSize);
		right = new BucketPRKDTree<>(numberOfDimensions, bucketSize);

		createSplit(numberOfDimensions, elements);
		addAll(elements);
	}

	@Override
	public BucketPRKDTreeNode<E> add(final E e) {
		addSingleElement(e);

		return this;
	}

	@Override
	public Iterator<E> iterator() {
		return new SplitPlaneIterator();
	}

	@Override
	public int size() {
		return left.size() + right.size();
	}

	private void addAll(final Collection<E> elements) {
		for (final E e : elements) {
			addSingleElement(e);
		}
	}

	private void addSingleElement(final E e) {
		BucketPRKDTreeNode<E> node = this;
		BucketPRKDTree<E> tree = null;
		while (node instanceof SplittingPlaneNode) {
			if (e.getCoordinate(((SplittingPlaneNode<E>) node).splitDimensionIdex) < ((SplittingPlaneNode<E>) node).splitDimensionMedian) {
				tree = ((SplittingPlaneNode<E>) node).left;
			} else {
				tree = ((SplittingPlaneNode<E>) node).right;
			}

			node = tree.getNode();
		}

		tree.add(e);
	}

	private void createSplit(final int numberOfDimensions, final List<E> elements) {
		double maxVariance = Double.NEGATIVE_INFINITY;
		double maxValue, minValue, value, variance;
		for (int i = 0; i < numberOfDimensions; i++) {
			maxValue = elements.get(0).getCoordinate(i);
			minValue = maxValue;
			for (int j = 1; j < elements.size(); j++) {
				value = elements.get(j).getCoordinate(i);

				if (value < minValue) {
					minValue = value;
				} else if (value > maxValue) {
					maxValue = value;
				}
			}

			variance = maxValue - minValue;
			if (variance > maxVariance) {
				maxVariance = variance;
				splitDimensionIdex = i;
				splitDimensionMedian = variance / 2.0 + minValue;
			}
		}
	}

	BucketPRKDTree<E> getLeftBucketPRKDTree() {
		return left;
	}

	BucketPRKDTree<E> getRightBucketPRKDTree() {
		return right;
	}

	int getSplitDimensionIndex() {
		return splitDimensionIdex;
	}

	double getSplitDimensionMedian() {
		return splitDimensionMedian;
	}

	boolean isUnbalanced() {
		return left.isEmpty() || right.isEmpty();
	}

	private class SplitPlaneIterator implements Iterator<E> {

		private final Iterator<E> leftIterator = left.iterator();
		private final Iterator<E> rightIterator = right.iterator();
		private boolean startedRight = false;

		@Override
		public boolean hasNext() {
			return leftIterator.hasNext() || rightIterator.hasNext();
		}

		@Override
		public E next() {
			if (leftIterator.hasNext()) {
				return leftIterator.next();
			} else {
				startedRight = true;
				return rightIterator.next();
			}
		}

		@Override
		public void remove() {
			if (startedRight) {
				rightIterator.remove();
			} else {
				leftIterator.remove();
			}
		}

	}

}
