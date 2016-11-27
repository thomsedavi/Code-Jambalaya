package theAucklandRoadSystem;

import java.util.HashSet;
import java.util.Set;

public class Trie {

	private Trie[] alpha;
	private Set<Street> streets;
	private String thisStreet;

	public Trie() {
		alpha = new Trie[38];
		streets = new HashSet<Street>();
	}

	public Trie addTrieBranch(int t) {
		if (alpha[t] != null) {
			return alpha[t];
		} else {
			return (alpha[t] = new Trie());
		}
	}

	public Trie search(int t) {
		return alpha[t];
	}

	public void printAll() {
		if (thisStreet != null) {
			TheARS.foundStreets.offer(thisStreet);
			for (Street S : streets) {
				S.activateColor();
			}
		}
		for (int a = 0; a < 38; a++) {
			if (alpha[a] != null) {
				alpha[a].printAll();
			}
		}
	}

	public Street addStreet(Street s) {
		thisStreet = s.getLabel();
		streets.add(s);
		return s;
	}

	public Set<Street> getStreets() {
		return streets;
	}

	public String getThisStreet() {
		return thisStreet;
	}

}
