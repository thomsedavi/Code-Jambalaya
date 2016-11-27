package theAucklandRoadSystem;

import java.util.*;

public class NodeID {
	private Set<NodeID> fromNodes;
	private Set<NodeID> toNodes;
	private Set<String> allRoadNames;
	private Location loc;
	private Set<Street> allStreets;
	private String nodeCode;

	public NodeID(Location loc, String nodeCode) {
		this.loc = loc;
		this.nodeCode = nodeCode;
		toNodes = new HashSet<NodeID>();
		fromNodes = new HashSet<NodeID>();
		allRoadNames = new HashSet<String>();
		allStreets = new HashSet<Street>();

	}

	public Location getLoc() {
		return loc;
	}

	public void addToNode(NodeID node) {
		toNodes.add(node);
	}

	public void addFromNode(NodeID node) {
		fromNodes.add(node);
	}

	public void addRoad(String R) {
		allRoadNames.add(R);
	}

	public void addStreet(Street S) {
		allStreets.add(S);
	}

	public void activateStreets() {
		for (Street S : allStreets) {
			S.activateColor();
		}
	}

	public void printStreets() {
		for (String s : allRoadNames) {
			TheARS.foundStreets.offer(s);
		}
	}

	public Set<NodeID> getToStreets() {
		return toNodes;
	}

	public Set<NodeID> getFromStreets() {
		return fromNodes;
	}

	public String toString() {
		return nodeCode;
	}

}
