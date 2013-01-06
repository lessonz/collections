package lessonz.collections.kdtree.bucketpr;

import java.util.Iterator;

import lessonz.collections.kdtree.KDPoint;

interface BucketPRKDTreeNode<E extends KDPoint> {

    BucketPRKDTreeNode<E> add(E e);

    Iterator<E> iterator();

    int size();

}
