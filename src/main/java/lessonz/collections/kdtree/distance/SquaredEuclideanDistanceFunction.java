package lessonz.collections.kdtree.distance;

/**
 * The {@link SquaredEuclideanDistanceFunction} calculates distance by use of the Pythagorean formula, but neglects to
 * take the final square root of the sum of the squares. This value is sometimes referred to as the quadrance. Because
 * {@link DistanceFunction}s are often used solely to determine <i>relative</i> proximity, the square of distances is
 * equally useful and is much more performant to calculate. While one such calculation may not be an issue, many such
 * calculations can become an issue.
 */
public class SquaredEuclideanDistanceFunction implements DistanceFunction {

	private static double square(final double base) {
		return base * base;
	}

	@Override
	public double distance(final double[] coordinateSet1, final double[] coordinateSet2) {
		double distanceSq = 0.0;

		for (int i = 0; i < coordinateSet1.length; i++) {
			distanceSq += square(coordinateSet1[i] - coordinateSet2[i]);
		}

		return distanceSq;
	}

}
