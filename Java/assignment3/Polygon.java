package assignment3;

import java.awt.Color;

public class Polygon {
	
	public final Vector3D a;
	public final Vector3D b;
	public final Vector3D c;
	public final Color color;
	private Vector3D normal;

	
	public Polygon(Vector3D a, Vector3D b, Vector3D c, Color color, Vector3D normal) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.color = color;
		this.normal = normal;
	}
	
	public String toString() {
		String result = "";
		result = result + a.toString() + "\n";
		result = result + b.toString() + "\n";
		result = result + c.toString() + "\n";
		result = result + color.toString() + "\n";
		return result;
	}	

	public void setNormal(Vector3D normal) {
		this.normal = normal;
	}
	
	public Vector3D getNormal() {
		return normal;
	}


}
