package lessonz.collections.kdtree.distance;

/**
 * A {@link DistanceFunction} provides the ability determine the distance between two k-dimensional points.
 */
public interface DistanceFunction {

	/**
	 * Calculates the distance between two points. In order to provide the maximum performance possible many
	 * implementation will <strong>not</strong> implement any kind of error checking such as ensuring the two specified
	 * arrays have the same length.
	 * 
	 * @param coordinateSet1
	 *            k-dimensional point where each value is the point's position in that plane.
	 * @param coordinateSet2
	 *            k-dimensional point where each value is the point's position in that plane.
	 * @return the distance between the two points.
	 */
	double distance(double[] coordinateSet1, double[] coordinateSet2);

}
