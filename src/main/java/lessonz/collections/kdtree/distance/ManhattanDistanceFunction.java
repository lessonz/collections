package lessonz.collections.kdtree.distance;

/**
 * The {@link ManhattanDistanceFunction} calculates distance by way of Manhattan or taxicab geometry. Whereas Euclidean
 * distance takes the most direct path from point A to point B, taxicab geometry takes a path as if it had to follow a
 * grid-based traffic system to move between the points. That is whereas the Euclidean path allows for all dimensions to
 * vary simultaneously, the taxicab path will navigate each dimension independently.
 */
public class ManhattanDistanceFunction implements DistanceFunction {

	@Override
	public double distance(final double[] coordinateSet1, final double[] coordinateSet2) {
		double manhattanDistance = 0.0;
		for (int i = 0; i < coordinateSet1.length; i++) {
			manhattanDistance += coordinateSet1[i] + coordinateSet2[i];
		}

		return manhattanDistance;
	}
}
