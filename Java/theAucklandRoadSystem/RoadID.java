package theAucklandRoadSystem;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Dimension;

public class RoadID {

	private Color color;
	private Location start;
	private RoadID next;

	public RoadID(Location start) {
		color = Color.BLACK;
		this.start = start;
		next = null;
	}

	public RoadID addV(Location V) {
		next = new RoadID(V);
		return next;
	}

	public void colorActive() {
		color = Color.RED;
		if (next != null) {
			next.colorActive();
		}
	}

	public void colorInactive() {
		color = Color.BLACK;
		if (next != null) {
			next.colorInactive();
		}
	}

	public void drawLine(Graphics g, Double scale, Location origin,
			Dimension area) {
		if (next != null) {
			Point a = start.asPoint(Location.newFromPoint(new Point(area.width
					/ -2, area.height / -2), origin, scale), scale);
			;
			Point b = next.returnStart().asPoint(
					Location.newFromPoint(new Point(area.width / -2,
							area.height / -2), origin, scale), scale);
			;
			g.setColor(color);
			g.drawLine(a.x, a.y, b.x, b.y);
			next.drawLine(g, scale, origin, area);
		}
	}

	public Location returnStart() {
		return start;
	}

}
