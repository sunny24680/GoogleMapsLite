package cmsc420.structure.PRQT;

import cmsc420.structure.*;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import cmsc420.geom.Shape2DDistanceCalculator;

public class PRQT {
	public final static Node white = new whiteNode();
	private static Node root;
	public Rectangle2D.Float dimensions;
	
	public PRQT(int x, int y) {
		root = white;
		dimensions = new Rectangle2D.Float(0.0f, 0.0f, (float)x, (float)y);
	}

	public void addCity(City newCity) {
		// TODO Auto-generated method stub
		//System.out.println("adding newCity "+newCity);
		root = root.addCity(newCity, dimensions);
	}

	public void removeCity(City city) {
		// TODO Auto-generated method stub
		root = root.removeCity(city);
	}
	
	public boolean contains(City city) {
		return root.contains(city);
	}
	
	public void clearAll() {
		root = PRQT.white;
	}
	
	public ArrayList<Node> printPRQT() {
		ArrayList<Node> r = new ArrayList<Node>();
		r.addAll(root.getCities());
		return r;
	}
	
	public boolean isEmpty() {
		if (root == white)
			return true;
		else 
			return false;
	}
	
	public ArrayList<City> distanceFrom(Point2D.Float loc, int radius){
		ArrayList<Node> temp = root.distanceFrom(loc, radius);
		ArrayList<City> results = new ArrayList<City>();
		for (int x = 0; x < temp.size(); x++) {
			results.add(((blackNode) temp.get(x)).getCity());
		}
		results.sort(new Comparator<City>() {
            @Override
            public int compare(City e1,
                    City e2) {
                return -e1.getName().compareTo(e2.getName());
            }
        });
		return results;
	}
	
	public City nearest(Point2D.Float loc){
		PriorityQueue<Node> results = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node e1,
                    Node e2) {
            	if (e1 instanceof grayNode) {
            		if (e2 instanceof grayNode) {
            			double dis1 = Shape2DDistanceCalculator.distance(loc, ((grayNode) e1).getDimension());
            			double dis2 = Shape2DDistanceCalculator.distance(loc, ((grayNode) e2).getDimension());
            			if (dis1 < dis2) {
            				return -1;
            			} else if (dis1 == dis2)
            				return 0;
            			else 
            				return 1;
            		} else if (e2 instanceof blackNode) {
            			double dis = Shape2DDistanceCalculator.distance(loc, ((grayNode) e1).getDimension());
            			double dis1 = ((blackNode) e2).getCity().distance(loc);
            			if (dis < dis1) {
            				return -1;
            			} else if (dis1 == dis)
            				return 0;
            			else 
            				return 1;
            		}
            			return -1;
            	} else if (e1 == PRQT.white) {
            		if (e2 == PRQT.white) {
            			return 0;
            		}
            		else return 1;
            	} else {
            		if (e2 instanceof blackNode) {
            			City c1 = ((blackNode) e1).getCity();
            			City c2 = ((blackNode) e2).getCity();
            			if (c1.distanceTo(loc) < c2.distanceTo(loc))
            				return -1;
            			else if (c1.distanceTo(loc) == c2.distanceTo(loc)) {
            				return c1.compareName(c2);
            			} else {
            				return 1;
            			}
            		} else if (e2 instanceof grayNode) { 
            			double dis = Shape2DDistanceCalculator.distance(loc, ((grayNode) e2).getDimension());
            			double dis1 = ((blackNode) e1).getCity().distance(loc);
            			if (dis < dis1) {
            				return 1;
            			} else if (dis1 == dis)
            				return 0;
            			else 
            				return -1;
            		} else 
            			return -1;
            	}
            		
            }
        });
		
		Node cur = root;
		while (cur != PRQT.white) {
			if (cur instanceof grayNode) {
				//System.out.println("adding gray node to PQ");
				results.add(((grayNode) cur).getChild(0));
				results.add(((grayNode) cur).getChild(1));
				results.add(((grayNode) cur).getChild(2));
				results.add(((grayNode) cur).getChild(3));
				//System.out.println(cur);
				cur = results.poll();
			} else {
//				for (int x = 0; x < results.size(); x++) {
//					System.out.println(results.toArray()[x]);
//				}
//				System.out.println(cur);
				return ((blackNode) cur).getCity();
			}
		}
		return null;
	}
	
	public Node getRoot() {
		return root;
	}
}
