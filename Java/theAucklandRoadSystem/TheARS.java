package theAucklandRoadSystem;

import java.io.*;
import java.util.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TheARS extends GUI {

	public static Stack<Street> activeStreets; // all streets currently
												// highlighted red
	public static Queue<String> foundStreets; // List of streets found by
												// searching Trie
												// or by clicking

	public Map<Integer, NodeID> nodeMap;
	public Map<Integer, Street> streetMap;
	public Set<Polygons> poly0, poly1, poly2, poly3;
	public Trie myTrie;
	public Point click, dragPoint;
	public NodeID selectedNode;
	public boolean dragging = false;
	public double dragX = 0, dragY = 0, speed = 144.0;
	public QuadTreeNode quadRoot;

	public Location origin = new Location(0, 0);

	public double scale = 96.0;

	public TheARS() {
		nodeMap = new HashMap<Integer, NodeID>();
		streetMap = new HashMap<Integer, Street>();
		myTrie = new Trie();
		activeStreets = new Stack<Street>();
		foundStreets = new ArrayDeque<String>();

		poly0 = new HashSet<Polygons>();
		poly1 = new HashSet<Polygons>();
		poly2 = new HashSet<Polygons>();
		poly3 = new HashSet<Polygons>();
	}

	@Override
	protected void redraw(Graphics g) {

		for (Polygons pg : poly3) {
			drawPolygons(pg, g);
		}

		for (Polygons pg : poly2) {
			drawPolygons(pg, g);
		}

		for (Polygons pg : poly1) {
			drawPolygons(pg, g);
		}

		for (Polygons pg : poly0) {
			drawPolygons(pg, g);
		}

		for (Integer n : streetMap.keySet()) {

			streetMap.get(n).drawRoads(g, scale, origin,
					getDrawingAreaDimension());
		}

		for (Integer n : nodeMap.keySet()) {

			Point p = nodeMap
					.get(n)
					.getLoc()
					.asPoint(
							Location.newFromPoint(new Point(
									getDrawingAreaDimension().width / -2,
									getDrawingAreaDimension().height / -2),
									origin, scale), scale);
			g.setColor(Color.BLACK);
			g.fillRect(p.x - 1, p.y - 1, 3, 3);
		}
	}

	private void drawPolygons(Polygons pg, Graphics g) {
		Set<PolyData> polyD = pg.getCoords();
		g.setColor(pg.getColor());
		for (PolyData pd : polyD) {

			Polygon vista = new Polygon();

			Point pd1 = pd.getStart().asPoint(
					Location.newFromPoint(new Point(
							getDrawingAreaDimension().width / -2,
							getDrawingAreaDimension().height / -2), origin,
							scale), scale);
			vista.addPoint(pd1.x, pd1.y);

			while (pd.hasNext()) {

				Point pd2 = pd
						.getNext()
						.getStart()
						.asPoint(
								Location.newFromPoint(new Point(
										getDrawingAreaDimension().width / -2,
										getDrawingAreaDimension().height / -2),
										origin, scale), scale);

				vista.addPoint(pd2.x, pd2.y);

				pd = pd.getNext();
			}
			g.fillPolygon(vista);
		}
	}

	protected void onDrag(MouseEvent e) {

		if (!dragging) {
			dragX = (double) -e.getX() / scale - origin.x;
			dragY = (double) e.getY() / scale - origin.y;
			dragging = true;
		}
		origin = new Location(-e.getX() / scale - dragX, e.getY() / scale
				- dragY);
	}

	protected void onScroll(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0)
			onMove(Move.ZOOM_IN);
		if (e.getWheelRotation() > 0)
			onMove(Move.ZOOM_OUT);
	}

	@Override
	protected void onClick(MouseEvent e) {
		click = new Point(e.getX(), e.getY());
		dragging = false;

		/* Old code in case my Quad Tree stop working:
		 *
		 * for (NodeID N : nodeMap.values()) { Point p = N.getLoc().asPoint(
		 * Location.newFromPoint(new Point( getDrawingAreaDimension().width /
		 * -2, getDrawingAreaDimension().height / -2), origin, scale), scale);
		 * if (p.x > click.x - 6 && p.x < click.x + 6 && p.y > click.y - 6 &&
		 * p.y < click.y + 6) { selectedNode = N; } }
		 */

		Location clickLoc = Location.newFromPoint(click, Location.newFromPoint(
				new Point(getDrawingAreaDimension().width / -2,
						getDrawingAreaDimension().height / -2), origin, scale),
				scale);

		Set<NodeID> temp = quadRoot.searchMap(clickLoc);

		double minDist = Double.MAX_VALUE;

		for (NodeID N : temp) {
			double checkDist = N.getLoc().distance(clickLoc);
			if (checkDist < minDist) {
				minDist = checkDist;
				selectedNode = N;
			}
		}

		for (double degrees = 0; degrees < 10; degrees += 0.5) {
			double dx = Math.sin(degrees);
			double dy = Math.cos(degrees);

			Location tempLoc = clickLoc.moveBy(dx * minDist, dy * minDist);
			Set<NodeID> temp2 = quadRoot.searchMap(tempLoc);
			temp.addAll(temp2);
			tempLoc = clickLoc.moveBy(dx * 0.05, dy * 0.05);
			temp2 = quadRoot.searchMap(tempLoc);
			temp.addAll(temp2);
		}

		for (NodeID N : temp) {
			double checkDist = N.getLoc().distance(clickLoc);
			if (checkDist < minDist) {
				minDist = checkDist;
				selectedNode = N;
			}
		}

		while (!activeStreets.isEmpty()) {
			activeStreets.pop().deactivateColor();
		}
		if (selectedNode != null) {
			selectedNode.printStreets();
			selectedNode.activateStreets();
			getTextOutputArea().setText("Streets on this intersection:\n");
			while (!foundStreets.isEmpty()) {
				getTextOutputArea().append(foundStreets.poll());
				if (foundStreets.size() != 0) {
					getTextOutputArea().append(", ");
				} else {
					getTextOutputArea().append(".");
				}
			}

			System.out.println();
			System.out.println(selectedNode.toString());
			System.out.println("From Nodes:");
			for (NodeID N : selectedNode.getFromStreets()) {
				System.out.println(N.toString());
			}

			System.out.println("To Nodes:");
			for (NodeID N : selectedNode.getToStreets()) {
				System.out.println(N.toString());
			}

			selectedNode = null;
		}
	}

	@Override
	protected void onSearch() {
		String roadz = getSearchBox().getText().toLowerCase();

		foundStreets.clear();
		Trie temp = myTrie;
		BoundaryTool bTool = new BoundaryTool();

		while (!activeStreets.isEmpty()) {
			activeStreets.pop().deactivateColor();
		}

		for (int x = 0; x < roadz.length(); x++) {

			int r = (int) roadz.charAt(x);

			if (r >= 97 && r <= 122) {
				temp = temp.search(r - 97);
			} // characters = 0 -> 25
			else if (r > 47 && r < 58) {
				temp = temp.search(r - 22);
			} // numbers = 26 -> 35
			else if (r == 32) {
				temp = temp.search(36);
			} // space = 36
			else {
				temp = temp.search(37);
			} // everything else = 37
			if (temp == null) {
				getTextOutputArea().setText(
						"No streets found. Please try again.");
				return;
			}
		}

		Set<Street> listStreets = temp.getStreets();
		Set<String> cityNames = new HashSet<String>();

		for (Street s : listStreets) {
			cityNames.add(s.city);
		}
		if (cityNames.size() > 1) {
			JFrame cityQuery = new JFrame();
			String result = null;
			result = (String) JOptionPane.showInputDialog(cityQuery,
					"Please choose a city:", "Multiple roads found",
					JOptionPane.YES_NO_OPTION, null, cityNames.toArray(),
					result);
			Set<Street> tempStreets = new HashSet<Street>();
			for (Street s : listStreets) {
				if (s.city.equals(result)) {
					tempStreets.add(s);
				}
			}
			listStreets = tempStreets;
		}

		if (listStreets.isEmpty()) {
			temp.printAll();
			getTextOutputArea().setText("No street found. Suggestions:\n");
			int foundCount = 0;

			while (!foundStreets.isEmpty() && foundCount < 10) {
				getTextOutputArea().append(foundStreets.poll());
				foundCount++;
				if (foundStreets.size() != 0 && foundCount != 10) {
					getTextOutputArea().append(", ");
				} else {
					getTextOutputArea().append(".");
				}
			}

		} else {
			for (Street s : listStreets) {
				activeStreets.push(s);
				streetMap.get(s.getRoadID()).activateColor();
				for (NodeID N : s.getNodeID()) {
					bTool.addBoundary(N.getLoc().x, N.getLoc().y);
				}
			}

			if (bTool.found) {
				getTextOutputArea().setText(temp.getThisStreet() + " found!");
				origin = new Location(bTool.calcCentre().x,
						bTool.calcCentre().y);
			}

			scale = 1640.25;

			double leftMin;
			double topMin;

			do {
				onMove(Move.ZOOM_OUT);
				leftMin = Location.newFromPoint(new Point(
						0 - (getDrawingAreaDimension().width / 2), 0), origin,
						scale).x;
				topMin = Location.newFromPoint(new Point(0,
						0 + (getDrawingAreaDimension().height / 2)), origin,
						scale).y;

			} while (bTool.getXMin() < leftMin || bTool.getYMin() < topMin);
		}
	}

	@Override
	protected void onMove(Move m) {
		if (m == Move.NORTH) {
			origin = origin.moveBy(0, 1 * (speed / scale));
		}
		if (m == Move.SOUTH) {
			origin = origin.moveBy(0, -1 * (speed / scale));
		}
		if (m == Move.EAST) {
			origin = origin.moveBy(1 * (speed / scale), 0);
		}
		if (m == Move.WEST) {
			origin = origin.moveBy(-1 * (speed / scale), 0);
		} else if (m == Move.ZOOM_IN) {
			if (scale < 1100) {
				scale *= 1.5;
			}
		} else if (m == Move.ZOOM_OUT) {
			if (scale > 1.5) {
				scale /= 1.5;
			}
		}
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {

		try {
			BufferedReader roadsR = new BufferedReader(new FileReader(roads));
			roadsR.readLine();
			String line;
			streetMap.clear();
			while ((line = roadsR.readLine()) != null) {
				String[] details = line.split("\t");
				Trie temp = myTrie;
				for (int x = 0; x < details[2].length(); x++) {
					int c = (int) details[2].charAt(x);
					if (c >= 97 && c <= 122) {
						temp = temp.addTrieBranch(c - 97);
					} // alphabet
					else if (c >= 48 && c <= 57) {
						temp = temp.addTrieBranch(c - 22);
					} // numbers
					else if (c == 32) {
						temp = temp.addTrieBranch(36);
					} // space
					else {
						temp = temp.addTrieBranch(37);
					} // everything else
				}
				Street temp2 = temp.addStreet(new Street(Integer
						.parseInt(details[0]), Integer.parseInt(details[1]),
						details[2], details[3], Integer.parseInt(details[4]),
						Integer.parseInt(details[5]), Integer
								.parseInt(details[6]), Integer
								.parseInt(details[7]), Integer
								.parseInt(details[8]), Integer
								.parseInt(details[9])));
				streetMap.put(Integer.parseInt(details[0]), temp2);
			}
			roadsR.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader nodeR = new BufferedReader(new FileReader(nodes));
			nodeMap.clear();
			String nodeSt;
			BoundaryTool bTool = new BoundaryTool();

			while ((nodeSt = nodeR.readLine()) != null) {
				String[] nodeC = nodeSt.split("\t");

				Location tempLoc = Location.newFromLatLon(
						Double.parseDouble(nodeC[1]),
						Double.parseDouble(nodeC[2]));

				nodeMap.put(Integer.parseInt(nodeC[0]), new NodeID(tempLoc,
						nodeC[0]));

				bTool.addBoundary(tempLoc.x, tempLoc.y);

			}

			nodeR.close();

			origin = new Location(bTool.calcCentre().x, bTool.calcCentre().y);

			scale = 1093.5;

			double leftMin;

			do {
				onMove(Move.ZOOM_OUT);
				leftMin = Location.newFromPoint(new Point(
						0 - (getDrawingAreaDimension().width / 2), 0), origin,
						scale).x;

			} while (bTool.getXMin() < leftMin);

			quadRoot = new QuadTreeNode(bTool.getXMin(), bTool.getXMax(),
					bTool.getYMin(), bTool.getYMax());

			for (int N : nodeMap.keySet()) {
				quadRoot.addNodeID(nodeMap.get(N));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader segmentsR = new BufferedReader(new FileReader(
					segments));
			segmentsR.readLine();
			String line;
			while ((line = segmentsR.readLine()) != null) {
				String[] coords = line.split("\t");

				nodeMap.get(Integer.parseInt(coords[2])).addToNode(
						nodeMap.get((Integer.parseInt(coords[3]))));
				nodeMap.get(Integer.parseInt(coords[3])).addFromNode(
						nodeMap.get((Integer.parseInt(coords[2]))));
				if (streetMap.get(Integer.parseInt(coords[0])).oneway == 0) {
					nodeMap.get(Integer.parseInt(coords[2])).addFromNode(
							nodeMap.get((Integer.parseInt(coords[3]))));
					nodeMap.get(Integer.parseInt(coords[3])).addToNode(
							nodeMap.get((Integer.parseInt(coords[2]))));
				}

				streetMap.get(Integer.parseInt(coords[0])).addNodeID(
						nodeMap.get(Integer.parseInt(coords[2])));
				streetMap.get(Integer.parseInt(coords[0])).addNodeID(
						nodeMap.get(Integer.parseInt(coords[3])));

				nodeMap.get(Integer.parseInt(coords[2])).addRoad(
						streetMap.get(Integer.parseInt(coords[0])).getLabel());
				nodeMap.get(Integer.parseInt(coords[3])).addRoad(
						streetMap.get(Integer.parseInt(coords[0])).getLabel());
				nodeMap.get(Integer.parseInt(coords[2])).addStreet(
						streetMap.get(Integer.parseInt(coords[0])));
				nodeMap.get(Integer.parseInt(coords[3])).addStreet(
						streetMap.get(Integer.parseInt(coords[0])));

				RoadID temp = streetMap.get(Integer.parseInt(coords[0]))
						.addRoad(
								new RoadID(Location.newFromLatLon(
										Double.parseDouble(coords[4]),
										Double.parseDouble(coords[5]))));
				int remaining = coords.length - 8;
				int nextA = 6, nextB = 7;
				while (remaining >= 0) {
					temp = temp.addV(Location.newFromLatLon(
							Double.parseDouble(coords[nextA]),
							Double.parseDouble(coords[nextB])));
					remaining -= 2;
					nextA += 2;
					nextB += 2;

				}
			}
			segmentsR.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		poly0.clear();
		poly1.clear();
		poly2.clear();
		poly3.clear();

		if (polygons != null) {

			try {
				BufferedReader polygonsR = new BufferedReader(new FileReader(
						polygons));
				String line;
				while ((line = polygonsR.readLine()) != null) {
					String endTemp = "0";
					String typeTemp = "";
					if (line.charAt(0) == '[') {
						line = polygonsR.readLine();
					}
					if (line.charAt(0) == 'T') {
						String[] type = line.split("=");
						typeTemp = type[1];
						line = polygonsR.readLine();
					}
					if (line.charAt(0) == 'L') {
						line = polygonsR.readLine();
					}
					if (line.charAt(0) == 'E') {
						String[] end = line.split("=");
						endTemp = end[1];
						line = polygonsR.readLine();
					}
					if (line.charAt(0) == 'C') {
						line = polygonsR.readLine();
					}

					Polygons polyTemp = new Polygons(typeTemp);
					if (endTemp.equals("0")) {
						poly0.add(polyTemp);
					}
					if (endTemp.equals("1")) {
						poly1.add(polyTemp);
					}
					if (endTemp.equals("2")) {
						poly2.add(polyTemp);
					}
					if (endTemp.equals("3")) {
						poly3.add(polyTemp);
					}

					while (line.charAt(0) == 'D') {
						int xPoly = 2, yPoly = 3;
						line = line.substring(6);
						String[] data = line.split(",");

						PolyData pdTemp = polyTemp.addPolyData(Location
								.newFromLatLon(Double.parseDouble(data[0]
										.substring(1)), Double
										.parseDouble(data[1].substring(0, 9))));

						while (yPoly < data.length) {
							pdTemp.addNext(Location.newFromLatLon(Double
									.parseDouble(data[xPoly].substring(1)),
									Double.parseDouble(data[yPoly].substring(0,
											9))));
							pdTemp = pdTemp.getNext();
							xPoly += 2;
							yPoly += 2;
						}
						line = polygonsR.readLine();
					}
					line = polygonsR.readLine();
					line = polygonsR.readLine();

				}
				polygonsR.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new TheARS();

	}
}
