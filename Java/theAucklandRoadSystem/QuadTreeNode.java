package theAucklandRoadSystem;

import java.util.HashSet;
import java.util.Set;

public class QuadTreeNode {

	private Set<NodeID> intersections;
	private final int MAX_NODES = 10;
	private boolean split;
	private double xMin, xMax, yMin, yMax;
	private QuadTreeNode T_L, T_R, B_L, B_R;

	public QuadTreeNode(double xMin, double xMax, double yMin, double yMax) {
		intersections = new HashSet<NodeID>();
		split = false;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

	public void addNodeID(NodeID node) {
		if (split == true) {
			if (node.getLoc().x <= (xMin + xMax) / 2) {
				if (node.getLoc().y <= (yMin + yMax / 2)) {
					T_L.addNodeID(node);
				} else
					B_L.addNodeID(node);
			} else {
				if (node.getLoc().y <= (yMin + yMax / 2)) {
					T_R.addNodeID(node);
				} else
					B_R.addNodeID(node);
			}
		}

		if (split == false) {
			intersections.add(node);
			if (intersections.size() >= MAX_NODES) {
				splitQuad();
				split = true;
				intersections.clear();
			}
		}
	}

	public void splitQuad() {
		split = true;
		T_L = new QuadTreeNode(xMin, (xMin + xMax) / 2, yMin, (yMin + yMax) / 2);
		T_R = new QuadTreeNode((xMin + xMax) / 2, xMax, yMin, (yMin + yMax) / 2);
		B_L = new QuadTreeNode(xMin, (xMin + xMax) / 2, (yMin + yMax) / 2, yMax);
		B_R = new QuadTreeNode((xMin + xMax) / 2, xMax, (yMin + yMax) / 2, yMax);

		for (NodeID N : intersections) {
			findQuad(N.getLoc()).addNodeID(N);
		}

		intersections.clear();
		split = true;
	}

	public boolean isSplit() {
		return split;
	}

	public int getCount() {
		return intersections.size();
	}

	public Set<NodeID> searchMap(Location click) {
		if (split == true) {
			return findQuad(click).searchMap(click);
		}
		return intersections;
	}

	public QuadTreeNode findQuad(Location loc) {
		if (loc.x <= (xMin + xMax) / 2) {
			if (loc.y <= (yMin + yMax / 2)) {
				return T_L;
			} else if (loc.y > (yMin + yMax / 2)) {
				return B_L;
			}
		} else if (loc.x > (xMin + xMax) / 2) {
			if (loc.y <= (yMin + yMax / 2)) {
				return T_R;
			} else if (loc.y > (yMin + yMax / 2)) {
				return B_R;
			}
		}
		return T_L;
	}
}