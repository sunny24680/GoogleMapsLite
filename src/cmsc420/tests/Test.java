package cmsc420.tests;

import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import cmsc420.sortedmap.AvlGTree;
import cmsc420.structure.AdjacencyList;
import cmsc420.structure.City;
import cmsc420.structure.Road;

import java.util.Random;
import java.util.TreeMap;

public class Test {
	
	public static void main (String args[]) {
		AdjacencyList l = new AdjacencyList();
		City A = new City("A", 0, 0, 1, "black", false);
		City B = new City("B", 5, 0, 1, "black", false);
		City C = new City("C", 1, 5, 1, "black", false);
		l.add(A, B);
		l.add(A, C);
		l.printList();
		System.out.println("ANSWERS");
		ArrayList<Road> r = l.shortestPath(A, A);
		System.out.println(r.isEmpty());
		for (int x = 0; x < r.size()-1; x++) {
			Road f = r.get(x);
			Road s = r.get(x+1);
			City p1 = null, p2 = null, p3 = null;
			if (f.getStartCity() == s.getStartCity()) {
				p1 = f.getEndCity();
				p2 = f.getStartCity();
				p3 = s.getEndCity();
			}
			if (f.getEndCity() == s.getEndCity()) {
				p1 = f.getStartCity();
				p2 = f.getEndCity();
				p3 = s.getStartCity();
			}
			if (f.getStartCity() == s.getEndCity()){
				p1 = f.getEndCity();
				p2 = f.getStartCity();
				p3 = s.getStartCity();
			}
			if (f.getEndCity() == s.getStartCity()) {
				p1 = f.getStartCity();
				p2 = f.getEndCity();
				p3 = s.getEndCity();
			}
			Arc2D.Double arc = new Arc2D.Double();
			arc.setArcByTangent(p1.toPoint2D(), p2.toPoint2D(), p3.toPoint2D(), 1);
			double a = p2.distance(p3);
			double b = p2.distance(p1);
			double c = p1.distance(p3);
			double val = ((a*a)+(b*b)-(c*c))/(2*a*b);
			System.out.println("p1 = "+p1);
			System.out.println("p2 = "+p2);
			System.out.println("p3 = "+p3);
	        System.out.println("val = "+val);
	        System.out.println("angle = "+Math.toDegrees(Math.acos(val)));
			System.out.println("Angle between Road "+f+" : "+s+" = "+arc.getAngleExtent());
			
		}
	}
}
