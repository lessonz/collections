package lessonz.collections.kdtree;

/**
 * The {@link KDPoint} class is little more than a container for an {@link Object}. It provides the coordinate data for
 * storing of an {@link Object} in k-dimensional space.
 */
public class KDPoint {

	private final double[] coordinates;
	private final Object data;
	private final int numberOfCoordinates;

	/**
	 * Creates a {@link KDPoint} that stores the specified {@link Object} with the specified coordinates.
	 * 
	 * @param coordinates
	 *            the {@link Object}'s position in k-dimensional space.
	 * @param data
	 *            the {@link Object} to be stored.
	 */
	public KDPoint(final double[] coordinates, final Object data) {
		numberOfCoordinates = coordinates.length;
		this.coordinates = new double[numberOfCoordinates];
		System.arraycopy(coordinates, 0, this.coordinates, 0, numberOfCoordinates);
		this.data = data;
	}

	/**
	 * Retrieves the coordinate for this {@link KDPoint} in the specified dimension.
	 * 
	 * @param index
	 *            the index of the specified dimension.
	 * @return this {@link KDPoint}'s location on the specified dimension.
	 */
	public double getCoordinate(final int index) {
		return coordinates[index];
	}

	/**
	 * Retrieve this {@link KDPoint}'s k-dimensional coordinates.
	 * 
	 * @return the {@link KDPoint}'s location.
	 */
	public double[] getCoordinates() {
		final double[] newArray = new double[numberOfCoordinates];
		System.arraycopy(coordinates, 0, newArray, 0, numberOfCoordinates);
		return newArray;
	}

	/**
	 * Retrieve this {@link KDPoint}'s stored object.
	 * 
	 * @return the {@link KDPoint}'s data.
	 */
	public Object getData() {
		return data;
	}

}
