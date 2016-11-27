package assignment3;

public class xScan {
	private float xMin, zMin, xMax, zMax;
	
	public xScan() {
		xMin = Float.MAX_VALUE;
		xMax = Float.MIN_VALUE;
		zMin = Float.MAX_VALUE;
		zMax = Float.MAX_VALUE;
	}

	public float getxMin() {
		return xMin;
	}

	public void setxMin(float xMin) {
		this.xMin = xMin;
	}

	public float getzMin() {
		return zMin;
	}

	public void setzMin(float zMin) {
		this.zMin = zMin;
	}

	public float getxMax() {
		return xMax;
	}

	public void setxMax(float xMax) {
		this.xMax = xMax;
	}

	public float getzMax() {
		return zMax;
	}

	public void setzMax(float zMax) {
		this.zMax = zMax;
	}

}
