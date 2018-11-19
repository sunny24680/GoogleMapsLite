package cmsc420.stucture.PMQT;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;
import cmsc420.geom.Geometry2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.meeshquest.part2.MeeshQuest;
import cmsc420.structure.City;
import cmsc420.structure.Road;

public abstract class PMQT {
	
	public Validator validator = null;
	public Node root;
	public final Node white = new whiteNode();
	public Rectangle2D dimensions;
	
	public PMQT(Validator v, int x, int y) {
		validator = v;
		root = white;
		dimensions = new Rectangle2D.Float(0.0f, 0.0f, (float)x, (float)y);
	}
	
	public boolean isEmpty() {
		if (root == white)
			return true;
		else 
			return false;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  Inner classes (White Node, Black Node, Gray Node)
	 */
    /////////////////////////////////////////////////////////////////////////////
	
	public class whiteNode extends Node{
		
		@Override
		public Node add(Geometry2D obj, Rectangle2D.Float dimensions) {
			blackNode res = new blackNode(dimensions);
			res.add(obj, dimensions);
			return res;
		}

		@Override
		public Node remove(Geometry2D shape) {
			System.out.println("SHOULD NOT REACH HERE");
			return null;
		}

	}
	
	public class blackNode extends Node{
		private TreeSet<Geometry2D> geo = new TreeSet<Geometry2D>(new CityRoadComparator());
		private Rectangle2D.Float dimension;
		
		public blackNode(Rectangle2D.Float dimensions) {
			dimension = dimensions;
		}
		
		// difference between PM1 and PM3 is checking for validity in Black node
		@Override
		public Node add(Geometry2D obj, Rectangle2D.Float dimensions) {
			geo.add(obj);
			if (obj instanceof City)
				MeeshQuest.canvas.addPoint(((City) obj).getName(), ((City) obj).x, ((City) obj).y, Color.BLACK);
			else 
				MeeshQuest.canvas.addLine(((Road) obj).x1, ((Road) obj).y1, ((Road) obj).x2, ((Road) obj).y2, Color.BLACK); 
			if (!validator.isValid(this)) {
				return partition();
			}
			return this;
		}

		@Override
		public Node remove(Geometry2D obj) {
			geo.remove(obj);
			if (obj instanceof City)
				MeeshQuest.canvas.removePoint(((City) obj).getName(), ((City) obj).x, ((City) obj).y, Color.BLACK);
			else 
				MeeshQuest.canvas.removeLine(((Road) obj).x1, ((Road) obj).y1, ((Road) obj).x2, ((Road) obj).y2, Color.BLACK); 
			
			if (validator.isValid(this))
				return this;
			else 
				return white;
		}
		
		public City getCity() {
			if (numCities() == 1)
				return (City) geo.first();
			else 
				return null;
		}
		
		public boolean isIsolated() {
			if (numCities() == 1 && numRoads() == 0)
				return true;
			else 
				return false;
		}
		
		public int numCities() {
			Iterator<Geometry2D> i = geo.iterator();
			int c = 0;
			while (i.next().getType() == Geometry2D.POINT)
				c++;
			return c;
		}
		
		public int numRoads() {
			Iterator<Geometry2D> i = geo.iterator();
			int c = 0;
			while (i.hasNext()) {
				if (i.next().getType() == Geometry2D.SEGMENT)
					c++;
			}
			return c;
		}
		
		private grayNode partition() {
			grayNode g = new grayNode(dimension);
			Iterator<Geometry2D> i = geo.iterator();
			while (i.hasNext()) {
				g.add(i.next(), dimension);
			}
			return g;
		}

	}
	
	public class grayNode extends Node{
		private Node [] children = new Node[4];
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
		
		@Override
		public Node add(Geometry2D obj, Rectangle2D.Float dimensions) {
			if (obj.getType() == Geometry2D.POINT){
				for (int x = 0; x < quad.length; x++) {
					if (Inclusive2DIntersectionVerifier.intersects(((City) obj).toPoint2D(), quad[x]))
						children[x].add(obj, quad[x]);
				}
			}
			if (obj.getType() == Geometry2D.SEGMENT){
				for (int x = 0; x < quad.length; x++) {
					if (Inclusive2DIntersectionVerifier.intersects(((Road) obj).toLine2D(), quad[x]))
						children[x].add(obj, quad[x]);
				}
			}
			return this;
		}

		//validity check for PM1/PM3 
		@Override
		public Node remove(Geometry2D obj) {
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
		
		private Node condense() {
			blackNode b = null;
			int pos = 0;
			int count = 0;
			for (final Node node : children) {
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
				for (Node n : children) {
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

	}
	
}
