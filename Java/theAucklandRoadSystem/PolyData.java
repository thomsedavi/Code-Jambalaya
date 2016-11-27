package theAucklandRoadSystem;

public class PolyData {

	private Location start;
	private PolyData next;

	public PolyData(Location start) {
		this.start = start;
		next = null;
	}

	public PolyData addNext(Location here) {
		next = new PolyData(here);
		return next;
	}

	public Location getStart() {
		return start;
	}

	public PolyData getNext() {
		return next;
	}

	public boolean hasNext() {
		return (next != null);
	}

}
