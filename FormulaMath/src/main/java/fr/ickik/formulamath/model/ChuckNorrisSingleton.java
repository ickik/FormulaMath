package fr.ickik.formulamath.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Construct a Chuck Norris (www.chucknorrisfact.com) but it is a
 * singleton because Chuck Norris unique is. Easteregg!!!
 * @author Patrick Allgeyer.
 * @version 0.1.000, 13 Jul. 2011.
 */
public final class ChuckNorrisSingleton {

	private final List<String> norrisFactList = new ArrayList<String>();
	private static ChuckNorrisSingleton chuchNorrisSingleton = new ChuckNorrisSingleton();
	private final Random r;
	
	private ChuckNorrisSingleton() {
		r = new Random();
		norrisFactList.add("Chuck Norris can divide by zero");
		norrisFactList.add("Chuck Norris doesn't use a computer because a computer does everything slower than Chuck Norris");
		norrisFactList.add("All browsers support the hex definitions #chuck and #norris for the colors black and blue");
		norrisFactList.add("Chuck Norris can unit test an entire application with a single assert");
		norrisFactList.add("Chuck Norris knows the last digit of PI");
		norrisFactList.add("Chuck norris breaks RSA 128-bit encrypted codes in milliseconds");
		norrisFactList.add("Chuck Norris can compile syntax errors");
		norrisFactList.add("Chuck Norris burst the dot com bubble");
		norrisFactList.add("Chuck Norris can write multi-threaded applications with a single thread");
		norrisFactList.add("ChuckNorrisClass is a singleton because there is only one Chuck Norris");
		norrisFactList.add("Chuck Norris programs occupy 150% of CPU, even when they are not executing");
		norrisFactList.add("Chuck Norris can write infinite recursion functions... and have them return");
		norrisFactList.add("Chuck Norris solved the Travelling Salesman problem in O(1) time");
		norrisFactList.add("Whiteboards are white because Chuck Norris scared them that way");
		norrisFactList.add("Chuck Norris doesn't use web standards as the web will conform to him");
		norrisFactList.add("When a bug sees Chuck Norris, it flees screaming in terror, and then immediately self-destructs to avoid being roundhouse-kicked");
		norrisFactList.add("Chuck Norris can't test for equality because he has no equal");
		norrisFactList.add("Chuck Norris's keyboard doesn't have a Ctrl key because nothing controls Chuck Norris");
		norrisFactList.add("Chuck Norris hosting is 101% uptime guaranteed");
		norrisFactList.add("Chuck Norris rewrote the Google search engine from scratch");
		norrisFactList.add("Chuck Norris can solve the Towers of Hanoi in one move");
	}
	
	/**
	 * Return a random fact from Chuck Norris (www.chucknorrisfact.com). 
	 * @return a random fact from Chuck Norris (www.chucknorrisfact.com).
	 */
	public String getRandomFact() {
		return norrisFactList.get(r.nextInt(norrisFactList.size()));
	}
	
	/**
	 * Return a singleton of Chuck Norris (www.chucknorrisfact.com)
	 * because it is only one Chuck Norris. 
	 * @return a singleton of Chuck Norris (www.chucknorrisfact.com).
	 */
	public static ChuckNorrisSingleton getInstance() {
		return chuchNorrisSingleton;
	}
}
