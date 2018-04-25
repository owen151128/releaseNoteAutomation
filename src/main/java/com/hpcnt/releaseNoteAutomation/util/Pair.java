package com.hpcnt.releaseNoteAutomation.util;

import com.hpcnt.releaseNoteAutomation.util.Pair;

/**
 * 
 * @author owen151128
 *
 *         3 return to use method return type
 *
 * @param <A>
 * @param <B>
 * @param <C>
 */
public class Pair<A, B, C> {

	public final A a;
	public final B b;
	public final C c;

	public Pair(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public int hashCode() {
		return a.hashCode() ^ b.hashCode() ^ c.hashCode();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair))
			return false;
		Pair pair = (Pair) obj;

		return this.a.equals(pair.a) && this.b.equals(pair.b) && this.c.equals(pair.c);
	}

}
