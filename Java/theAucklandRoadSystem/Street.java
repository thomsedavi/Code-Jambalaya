package theAucklandRoadSystem;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Street {

	public final int roadid, type, oneway, speed, roadclass, notforcar,
			notforpede, notforbicy;
	public final String label, city;
	private List<RoadID> roadList;
	private Set<NodeID> nodeList;

	public Street(int roadid, int type, String label, String city, int oneway,
			int speed, int roadclass, int notforcar, int notforpede,
			int notforbicy) {
		this.roadid = roadid;
		this.label = label;
		this.city = city;
		this.type = type;
		this.oneway = oneway;
		this.speed = speed;
		this.roadclass = roadclass;
		this.notforcar = notforcar;
		this.notforpede = notforpede;
		this.notforbicy = notforbicy;
		roadList = new LinkedList<RoadID>();
		nodeList = new HashSet<NodeID>();
	}

	public int getRoadID() {
		return roadid;
	}

	public String getLabel() {
		return label;
	}

	public RoadID addRoad(RoadID R) {
		roadList.add(R);
		return R;
	}

	public void drawRoads(Graphics g, Double scale, Location origin,
			Dimension area) {
		for (RoadID r : roadList) {
			r.drawLine(g, scale, origin, area);
		}
	}

	public void activateColor() {
		TheARS.activeStreets.push(this);
		for (RoadID r : roadList) {
			r.colorActive();
		}
	}

	public void deactivateColor() {
		for (RoadID r : roadList) {
			r.colorInactive();
		}
	}

	public List<RoadID> getRoads() {
		return roadList;
	}

	public void addNodeID(NodeID N) {
		nodeList.add(N);
	}

	public Set<NodeID> getNodeID() {
		return nodeList;
	}

	public String getCity() {
		return city;
	}

}
