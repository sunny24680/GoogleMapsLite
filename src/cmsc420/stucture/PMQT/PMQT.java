package cmsc420.stucture.PMQT;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeSet;

import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.geom.Shape2DDistanceCalculator;
import cmsc420.meeshquest.part2.MeeshQuest;
import cmsc420.structure.City;
import cmsc420.structure.Road;
import cmsc420.stucture.PMQT.PMNode;

public abstract class PMQT {
	
	/***************************************************************************
	 * METHODS TO WRITE
	 * REMOVE CITY
	 ***************************************************************************/
	
	public Validator validator = null;
	public final PMNode white = new whiteNode();
	public PMNode root = white;
	public Rectangle2D.Float dimensions;
	private Point2D.Float loc = null;
	
	public PMQT(Validator v, int x, int y) {
		validator = v;
		root = white;
		dimensions = new Rectangle2D.Float(0.0f, 0.0f, (float)x, (float)y);
	}
	
	public void clearAll() {
		root = white;
	}
	
	public boolean isEmpty() {
		if (root == white)
			return true;
		else 
			return false;
	}
	
	public ArrayList<PMNode> getMap(){
		return root.getMap();
	}
	
	public void add(Geometry2D geo) {
		root = root.add(geo, dimensions);
	}
	
	public TreeSet<City> citiesIn(Circle2D.Float cir){
		return root.citiesIn(cir);
	}
	
	public TreeSet<Road> roadsIn(Circle2D.Float cir){
		TreeSet<Road> temp = root.roadsIn(cir);
		//MeeshQuest.canvas.addCircle(cir.getCenterX(), cir.getCenterY(), cir.getRadius(), Color.BLUE, true);
		TreeSet<Road> results = new TreeSet<Road>(new Comparator<Road>() {
            @Override
            public int compare(Road e1,
                    Road e2) {
                if (e1.getStartCity().getName().compareTo(e2.getStartCity().getName()) != 0)
                	return -e1.getStartCity().getName().compareTo(e2.getStartCity().getName());
                else if (e1.getEndCity().getName().compareTo(e2.getEndCity().getName()) != 0)
                	return -e1.getEndCity().getName().compareTo(e2.getEndCity().getName());
                else 
                	return 0;
            }
        });
		for (Object r : temp.toArray()) {
			results.add((Road) r);
		}
		return results;
	}
	
	public City nearestCity(Point2D.Float loc){
		this.loc = loc;
		PriorityQueue<PMNode> results = new PriorityQueue<PMNode>(new PMNodeComparator());
		PMNode cur = root;
		while (cur != null && cur != white) {
			//System.out.println("CUR = "+cur);
			if (cur instanceof grayNode) {
				for (int x = 0; x < 4; x++) {
					if (((grayNode) cur).getChild(x) instanceof blackNode) {
						City c = ((blackNode) ((grayNode) cur).getChild(x)).getCity();
						if (c != null)
							if (c.isIsolated() == false) {
								//System.out.println("non iso = "+c+c.getName());
								results.add(((grayNode) cur).getChild(x));
							} else {
								//System.out.println("iso = "+c+c.getName());
							}
					} else {
						results.add(((grayNode) cur).getChild(x));
					}
				}
				cur = results.poll();
			} else {
				if (((blackNode) cur).getCity().isIsolated() == false)
					return ((blackNode) cur).getCity();
				else 
					cur = results.poll();
			}
		}
		return null;
	}
	
	public City nearestIsolatedCity(Point2D.Float loc){
		this.loc = loc;
		PriorityQueue<PMNode> results = new PriorityQueue<PMNode>(new PMNodeComparator());
		PMNode cur = root;
		while (cur != white) {
			if (cur instanceof grayNode) {
				for (int x = 0; x < 4; x++) {
					if (((grayNode) cur).getChild(x) instanceof blackNode) {
						City c = ((blackNode) ((grayNode) cur).getChild(x)).getCity();
						if (c != null)
							if (c.isIsolated() == true) {
								results.add(((grayNode) cur).getChild(x));
							}
					} else {
						results.add(((grayNode) cur).getChild(x));
					}
				}
				cur = results.poll();
			} else {
				return ((blackNode) cur).getCity();
			}
		}
		return null;
	}
	
	public Road nearestRoad(Point2D.Float loc){
		this.loc = loc;
		PriorityQueue<PMNode> results = new PriorityQueue<PMNode>(new PMNodeComparator());
		PMNode cur = root;
		while (cur != white) {
			if (cur instanceof grayNode) {
				for (int x = 0; x < 4; x++) {
					if (((grayNode) cur).getChild(x) instanceof blackNode) {
						City c = ((blackNode) ((grayNode) cur).getChild(x)).getCity();
						if (c != null)
							if (c.isIsolated() == false) {
								results.add(((grayNode) cur).getChild(x));
							}
					} else {
						results.add(((grayNode) cur).getChild(x));
					}
				}
				cur = results.poll();
			} else {
				ArrayList<Road> roads = ((blackNode) cur).getRoads();
				Road t = roads.get(0);
				for (int x = 1; x < roads.size(); x++) {
					double rdis = roads.get(x).ptSegDist(loc);
					double tdis = t.ptSegDist(loc);
					if (rdis < tdis)
						t = roads.get(x);
					else if (rdis == tdis)
						if (roads.get(x).getStartCity().compareName(t.getStartCity()) < 0) {
							t = roads.get(x);
						} else if (roads.get(x).getStartCity().compareName(t.getStartCity()) == 0) {
							if (roads.get(x).getEndCity().compareName(t.getEndCity()) < 0) {
								t = roads.get(x);
							}
						}
				}
				return t;
			}
		}
		return null;
	}
	
	public City nearestCityToRoad(Road road){
		ArrayList<City> cities = getAllCities();
		cities.remove(road.getStartCity());
		cities.remove(road.getEndCity());
		if (cities.isEmpty())
			return null;
		City min = cities.get(0);
		for (City c : cities) {
			//System.out.println("CITIES IN ArrayList = "+c+c.getName());
			if (road.ptSegDist(c) < road.ptSegDist(min))
				min = c;
			if (road.ptSegDist(c) == road.ptSegDist(min))
				if (c.compareName(min) < 0)
					min = c;
		}
		//System.out.println("RETURNING "+min+min.getName());
		return min;
	}
	
	private ArrayList<City> getAllCities(){
		ArrayList<PMNode> m = getMap();
		ArrayList<City> cities = new ArrayList<City>();
		for (PMNode n : m) {
			if (n instanceof blackNode) {
				City c = ((blackNode) n).getCity();
				if (c != null) {
					if (!cities.contains(c)) {
						cities.add(c);
					}
				}
			}
		}
		return cities;
	}
	
	public boolean containsCity(City c) {
		return root.containsCity(c);
	}
	
	public boolean containsRoad(Road r) {
		return root.containsRoad(r);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  Inner classes White Node
	 */
    /////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("serial")
	public class whiteNode extends PMNode{
		
		@Override
		public PMNode add(Geometry2D obj, Rectangle2D.Float dimensions) {
			blackNode res = new blackNode(dimensions);
			res.add(obj, dimensions);
			return res;
		}

		@Override
		public PMNode remove(Geometry2D shape) {
			System.out.println("SHOULD NOT REACH HERE");
			return null;
		}
		
		public ArrayList<PMNode> getMap(){
			ArrayList<PMNode> t = new ArrayList<>();
			t.add(white);
			return t;
		}
		
		public TreeSet<City> citiesIn(Circle2D.Float cir) {
			return new TreeSet<City>();
		}
		
		public TreeSet<Road> roadsIn(Circle2D.Float cir) {
			return new TreeSet<Road>();
		}

		public boolean containsCity(City c) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean containsRoad(Road r) {
			// TODO Auto-generated method stub
			return false;
		}

	}
	
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  Inner Class BlackNode
	 */
    /////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("serial")
	public class blackNode extends PMNode{
		private TreeSet<Geometry2D> geo = new TreeSet<Geometry2D>(new CityRoadComparator());
		private Rectangle2D.Float dimension;
		
		public String toString() {
			return "BLACK NODE @ "+dimension;
		}
		
		public blackNode(Rectangle2D.Float dimensions) {
			dimension = dimensions;
		}
		
		// difference between PM1 and PM3 is checking for validity in Black node
		@Override
		public PMNode add(Geometry2D obj, Rectangle2D.Float dimensions) {
			geo.add(obj);
			if (obj instanceof City)
				MeeshQuest.canvas.addPoint(((City) obj).getName(), ((City) obj).x, ((City) obj).y, Color.BLACK);
			else {
				MeeshQuest.canvas.addLine(((Road) obj).x1, ((Road) obj).y1, ((Road) obj).x2, ((Road) obj).y2, Color.BLACK); 
			}
			if (!validator.isValid(this)) {
				return partition();
			} else 
				return this;
		}
		
		//used to partition the black node to create a grayNode
		private grayNode partition() {
			grayNode g = new grayNode(dimension);
			for (Geometry2D ge : geo) {
				g.add(ge, dimension);
			}
			return g;
		}

		@Override
		public PMNode remove(Geometry2D obj) {
			geo.remove(obj);
			if (obj instanceof City)
				MeeshQuest.canvas.removePoint(((City) obj).getName(), ((City) obj).x, ((City) obj).y, Color.BLACK);
			else  {
				System.out.println("removing line");
				MeeshQuest.canvas.removeLine(((Road) obj).x1, ((Road) obj).y1, ((Road) obj).x2, ((Road) obj).y2, Color.BLACK); 
			}
			if (validator.isValid(this))
				return this;
			else 
				return white;
		}
		
		//used to make the printPMQT
		public ArrayList<PMNode> getMap(){
			ArrayList<PMNode> t = new ArrayList<PMNode>();
			t.add(this);
			return t;
		}
		
		//gets cardinality
		public int getCard() {
			return numCities()+numRoads();
		}
		
		public City getCity() {
			if (numCities() == 1)
				return (City) geo.first();
			else 
				return null;
		}
		
		public ArrayList<Road> getRoads() {
			ArrayList<Road> t = new ArrayList<Road>();
			for (Geometry2D thing : geo) {
				if (thing.getType() == Geometry2D.SEGMENT)
					t.add((Road) thing);
			}
			return t;
		}
		
		public int numCities() {
			int c = 0;
			for (Geometry2D ge : geo) {
				if (ge.getType() == Geometry2D.POINT)
					c++;
			}
			return c;
		}
		
		public int numRoads() {
			int c = 0;
			for (Geometry2D ge : geo) {
				if (ge.getType() == Geometry2D.SEGMENT)
					c++;
			}
			return c;
		}
		
		public TreeSet<City> citiesIn(Circle2D.Float cir) {
			TreeSet<City> t = new TreeSet<City>();
			City c = getCity();
			if (c != null)
				if (Inclusive2DIntersectionVerifier.intersects(c.toPoint2D(), cir))
					t.add(c);
			return t;
		}
		
		public TreeSet<Road> roadsIn(Circle2D.Float cir) {
			TreeSet<Road> t = new TreeSet<Road>(new Road.nameComparator());
			ArrayList<Road> roads = getRoads();
			for (int x = 0; x < roads.size(); x++) {
				if (roads.get(x).intersects(cir)) {
					//System.out.println(roads.get(x));
					t.add(new Road (roads.get(x).getStartCity(), roads.get(x).getEndCity()));
				}
			}
			return t;
		}

		public boolean containsCity(City c) {
			//System.out.println("C = "+c);
			if (getCity() == null) {
				return false;
			}
			else if (getCity().equals(c))
				return true;
			else {
				return false;
			}
				
		}

		public boolean containsRoad(Road r) {
			ArrayList<Road> roads = getRoads();
			for (int x = 0; x < roads.size(); x++) {
				if (roads.get(x).equals(r))
					return true;
			}
			return false;
		}

	}
	
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  Inner Class GrayNode
	 */
    /////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("serial")
	public class grayNode extends PMNode{
		private PMNode [] children = new PMNode[4];
		private Rectangle2D.Float [] quad = new Rectangle2D.Float[4];
		// 0 = NW
		// 1 = NE
		// 2 = SW
		// 3 = SE
		
		private Rectangle2D.Float dimension;
		private Point2D.Float center;
		
		public grayNode(Rectangle2D.Float dimensions) {
			center = new Point2D.Float((float) dimensions.getCenterX(), (float) dimensions.getCenterY());
			Arrays.fill(children, white);
			dimension = dimensions;
			float h = (float) (dimension.getHeight()/2);
			float w = (float) (dimension.getWidth()/2);
			quad[0] = new Rectangle2D.Float(dimension.x, center.y, w, h);
			quad[1] = new Rectangle2D.Float(center.x, center.y, w, h);
			quad[2] = new Rectangle2D.Float(dimension.x, dimension.y, w, h);
			quad[3] = new Rectangle2D.Float(center.x, dimension.y, w, h);
			MeeshQuest.canvas.addRectangle(dimension.x, dimension.y, dimension.getWidth()/2, dimension.getHeight()/2, Color.GRAY, false);
			MeeshQuest.canvas.addRectangle(center.x, dimension.y, dimension.getWidth()/2, dimension.getHeight()/2, Color.GRAY, false);
			MeeshQuest.canvas.addRectangle(dimension.x, center.y, dimension.getWidth()/2, dimension.getHeight()/2, Color.GRAY, false);
			MeeshQuest.canvas.addRectangle(center.x, center.y, dimension.getWidth()/2, dimension.getHeight()/2, Color.GRAY, false);
		}
		
		public Point2D.Float getCenter(){
			return center;
		}
		
		public Rectangle2D.Float getDimension(){
			return dimension;
		}
		
		@Override
		public PMNode add(Geometry2D obj, Rectangle2D.Float dimensions) {
			if (obj.getType() == Geometry2D.POINT){
				for (int x = 0; x < quad.length; x++) {
					if (Inclusive2DIntersectionVerifier.intersects(((City) obj).toPoint2D(), quad[x])) 
						children[x] = children[x].add(obj, quad[x]);
				}
			}
			if (obj.getType() == Geometry2D.SEGMENT){
				for (int x = 0; x < quad.length; x++) {
					if (Inclusive2DIntersectionVerifier.intersects(((Road) obj).toLine2D(), quad[x]))
						children[x] = children[x].add(obj, quad[x]);
				}
			}
			return this;
		}

		//validity check for PM1/PM3 
		@Override
		public PMNode remove(Geometry2D obj) {
			if (obj.getType() == Geometry2D.POINT){
				for (int x = 0; x < quad.length; x++) {
					if (Inclusive2DIntersectionVerifier.intersects(((City) obj).toPoint2D(), quad[x]))
						children[x].remove(obj);
				}
			}
			if (obj.getType() == Geometry2D.SEGMENT){
				for (int x = 0; x < quad.length; x++) {
					if (Inclusive2DIntersectionVerifier.intersects(((Road) obj).toLine2D(), quad[x]))
						children[x].remove(obj);
				}
			}
			
			return condense();
		}
		
		private PMNode condense() {
			blackNode b = null;
			int pos = 0;
			int count = 0;
			for (final PMNode node : children) {
				if (node instanceof blackNode) {
					b = (blackNode) node;
					pos = count;
					count++;
				}
			}
			if (count == 0)
				return white;
			else if (count == 1) {
				return b;
			} else {
				blackNode res = new blackNode(dimension);
				for (PMNode n : children) {
					if (n instanceof blackNode) {
						for (Geometry2D obj : ((blackNode) n).geo) {
							res.add(obj, quad[pos]);
						}
					}	
				}
				if (validator.isValid(res))
					return res;
				else 
					return this;
			}
		}
		
		public ArrayList<PMNode> getMap(){
			ArrayList<PMNode> t = new ArrayList<PMNode>();
			t.add(this);
			t.addAll(children[0].getMap());
			t.addAll(children[1].getMap());
			t.addAll(children[2].getMap());
			t.addAll(children[3].getMap());
			return t;
		}
		
		public TreeSet<City> citiesIn(Circle2D.Float cir) {
			TreeSet<City> t = new TreeSet<City>();
			for (int x = 0; x < children.length; x++) {
				if (Inclusive2DIntersectionVerifier.intersects(quad[x], cir)) {
					t.addAll(children[x].citiesIn(cir));
				}
			}
			return t;
		}

		@Override
		public TreeSet<Road> roadsIn(cmsc420.geom.Circle2D.Float cir) {
			TreeSet<Road> r = new TreeSet<Road>();
			r.addAll(children[0].roadsIn(cir));
			r.addAll(children[1].roadsIn(cir));
			r.addAll(children[2].roadsIn(cir));
			r.addAll(children[3].roadsIn(cir));
			return r;
		}
		
		public PMNode getChild(int x) {
			return children[x];
		}

		public boolean containsCity(City c) {
			for (int x = 0; x < 4; x++) {
				if (children[x].containsCity(c))
					return true;
			}
			return false;
		}

		public boolean containsRoad(Road r) {
			for (int x = 0; x < 4; x++) {
				if (children[x].containsRoad(r))
					return true;
			}
			return false;
		}
	}
	
	public class PMNodeComparator implements Comparator<PMNode> {
        public int compare(PMNode e1, PMNode e2) {
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
        	} else if (e1 == white) {
        		if (e2 == white) {
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
    }
}
