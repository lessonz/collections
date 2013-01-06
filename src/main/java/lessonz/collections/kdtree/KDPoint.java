package lessonz.collections.kdtree;

/**
 * The {@link KDPoint} class is little more than a container for an {@link Object}. It provides the coordinate data for
 * storing of an {@link Object} in k-dimensional space.
 */
public class KDPoint {

	private final double[] coordinates;
	private final Object data;
	private final int numberOfCoordinates;

	public KDPoint(final double[] coordinates, final Object data) {
		numberOfCoordinates = coordinates.length;
		this.coordinates = new double[numberOfCoordinates];
		System.arraycopy(coordinates, 0, this.coordinates, 0, numberOfCoordinates);
		this.data = data;
	}

	public double getCoordinate(final int index) {
		return coordinates[index];
	}

	public double[] getCoordinates() {
		final double[] newArray = new double[numberOfCoordinates];
		System.arraycopy(coordinates, 0, newArray, 0, numberOfCoordinates);
		return newArray;
	}

	public Object getData() {
		return data;
	}

}
