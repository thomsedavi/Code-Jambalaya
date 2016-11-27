package assignment3;

public class centreMag {

	Float minX, maxX, minY, maxY, minZ, maxZ;

	public centreMag() {
		minX = maxX = minY = maxY = minZ = maxZ = null;
	}

	public void addVector(Vector3D vector) {
		if (minX == null) {
			minX = vector.x;
			maxX = vector.x;
			minY = vector.y;
			maxY = vector.y;
			minZ = vector.z;
			maxZ = vector.z;
		}
		else {
			if (vector.x < minX) minX = vector.x;
			if (vector.x > maxX) maxX = vector.x;
			if (vector.y < minY) minY = vector.y;
			if (vector.y > maxY) maxY = vector.y;
			if (vector.z < minZ) minZ = vector.z;
			if (vector.z > maxZ) maxZ = vector.z;
		}
	}

	public Vector3D getCentre() {
		return new Vector3D((minX + maxX)/2, (minY + maxY)/2, (minZ + maxZ)/2);
	}

	public Float getMaxFromCentre() {

		Float xAndY = maxX - minX;
		if (xAndY < maxY - minY) {xAndY = maxY - minY;}
		if (xAndY < maxZ - minZ) {xAndY = maxZ - minZ;}
		return xAndY;
	}

}
