package lessonz.collections.kdtree.bucketpr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lessonz.collections.kdtree.KDPoint;

class BucketNode<E extends KDPoint> implements BucketPRKDTreeNode<E> {

	private final int bucketSize;
	private final List<E> elements;
	private final int numberOfDimensions;

	BucketNode(final int numberOfDimensions, final int bucketSize) {
		this.numberOfDimensions = numberOfDimensions;
		this.bucketSize = bucketSize;
		elements = new ArrayList<>(bucketSize);
	}

	@Override
	public BucketPRKDTreeNode<E> add(final E e) {
		final BucketPRKDTreeNode<E> node;
		if (elements.size() > bucketSize) {
			node = new SplittingPlaneNode<E>(elements, numberOfDimensions, bucketSize);
			node.add(e);
		} else {
			elements.add(e);
			node = this;
		}

		return node;
	}

	@Override
	public Iterator<E> iterator() {
		return elements.iterator();
	}

	@Override
	public int size() {
		return elements.size();
	}

	List<E> getElements() {
		return elements;
	}

}
