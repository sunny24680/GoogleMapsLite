package cmsc420.tests;

import java.util.Iterator;
import java.util.Map.Entry;

import cmsc420.sortedmap.AvlGTree;

import java.util.Random;
import java.util.TreeMap;

public class Test {
	
	public static void main (String args[]) {
		AvlGTree<Integer, Integer> t = new AvlGTree<>(2);
		TreeMap<Integer, Integer> t2 = new TreeMap<>();
		System.out.println(t.put(5, 5));
		System.out.println(t.put(5, 6));
		System.out.println();
		System.out.println();
		int count = 0;
		Random r = new Random();
		System.out.println(t2.isEmpty());
		for (int x = 50; x < 51; x++) {
			int y = r.nextInt(100);
			System.out.println("adding "+y);
			t.put(y, count);
			t2.put(y, count);
			count++;
			//t.print(t.root);
		}
		Iterator i = t.entrySet().iterator();
		t.print(t.root);
		System.out.println(t2.isEmpty());
		count = 0;
		for (Entry<Integer, Integer> e2: t2.entrySet()) {
			Entry<Integer, Integer> e = (Entry<Integer, Integer>) i.next();
			System.out.println("e = "+e);
			System.out.println("e2 = "+e2);
			System.out.println(e2.equals(e));
			System.out.println(e.equals(e2));
		}
		System.out.println(t.equals(t2));

	}
}
