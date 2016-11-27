package theAucklandRoadSystem;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class Polygons {

	private Color polyColor;
	private Set<PolyData> coords;

	public Polygons(String type) {
		if (type.equals("0xb")) {
			polyColor = Color.BLACK; // University
		}
		else if (type.equals("0xa")) {
			polyColor = Color.BLACK; // Secondary School
		}
		else if (type.equals("0xe")) {
			polyColor = Color.DARK_GRAY; // Airport Tarmac
		}
		else if (type.equals("0x7")) {
			polyColor = Color.LIGHT_GRAY; // Airport Zone
		}
		else if (type.equals("0x1a")) {
			polyColor = Color.GREEN; // Cemetary
		}
		else if (type.equals("0x5")) {
			polyColor = Color.BLACK; // Shopping Mall
		}
		else if (type.equals("0x1e")) {
			polyColor = Color.GREEN; // Park
		}
		else if (type.equals("0x3c")) {
			polyColor = Color.BLUE; // Reservoir
		}
		else if (type.equals("0x2")) {
			polyColor = Color.LIGHT_GRAY; // Residential
		}
		else if (type.equals("0x19")) {
			polyColor = Color.BLACK; // Sport
		}
		else if (type.equals("0x3e")) {
			polyColor = Color.BLUE; // Lake
		}
		else if (type.equals("0x40")) {
			polyColor = Color.RED; // Unknown
		}
		else if (type.equals("0x18")) {
			polyColor = Color.GREEN; // Golf Course
		}
		else if (type.equals("0x41")) {
			polyColor = Color.RED; // Unknown
		}
		else if (type.equals("0x17")) {
			polyColor = Color.GREEN; // Park
		}
		else if (type.equals("0x16")) {
			polyColor = Color.GREEN; // Forest
		}
		else if (type.equals("0x50")) {
			polyColor = Color.GREEN; // Reserve
		}
		else if (type.equals("0x13")) {
			polyColor = Color.GRAY; // Commercial
		}
		else if (type.equals("0x8")) {
			polyColor = Color.DARK_GRAY; // CBD
		}
		else if (type.equals("0x48")) {
			polyColor = Color.BLUE; // River
		}
		else if (type.equals("0x45")) {
			polyColor = Color.BLUE; // Lake
		}
		else if (type.equals("0x28")) {
			polyColor = Color.BLUE; // Ocean
		}
		else if (type.equals("0x47")) {
			polyColor = Color.BLUE; // Stream
		}

		coords = new HashSet<PolyData>();
	}

	public Color getColor() {
		return polyColor;
	}

	public PolyData addPolyData(Location loc) {
		PolyData temp = new PolyData(loc);
		coords.add(temp);
		return temp;
	}

	public Set<PolyData> getCoords() {
		return coords;
	}

}
