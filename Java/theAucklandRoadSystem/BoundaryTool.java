package theAucklandRoadSystem;

public class BoundaryTool {

	private Double xMax, xMin, yMax, yMin;
	boolean found = false;

	public BoundaryTool() {
	}

	public void addBoundary(Double x, Double y) {
		found = true;

		if (xMax == null) {
			xMax = x;
			xMin = x;
			yMax = y;
			yMin = y;
			return;
		}

		if (x > xMax) {
			xMax = x;
		} else if (x < xMin) {
			xMin = x;
		}

		if (y > yMax) {
			yMax = y;
		} else if (y < yMin) {
			yMin = y;
		}
	}

	public Location calcCentre() {
		return new Location((xMax + xMin) / 2, (yMax + yMin) / 2);
	}

	public boolean foundCentre() {
		return found;
	}

	public String toString() {
		return xMax + " " + xMin + " " + yMax + " " + yMin;
	}

	public Double getXMin() {
		return xMin;
	}

	public Double getYMin() {
		return yMin;
	}

	public Double getXMax() {
		return xMax;
	}

	public Double getYMax() {
		return yMax;
	}
}
