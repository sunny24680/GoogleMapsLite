package cmsc420.commands;
//change package to where meeshquest is located
import cmsc420.meeshquest.part2.MeeshQuest;
import cmsc420.sortedmap.AvlGTree;
import cmsc420.structure.City;
import cmsc420.structure.Dictionary;
import cmsc420.structure.Road;

import java.awt.geom.Arc2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cmsc420.structure.PRQT.*;
import cmsc420.stucture.PMQT.*;

//Deals with all the XML code 
public class XMLHandler {
	
	Document results = MeeshQuest.results;
	private final static String[][] params = {{},{"name", "x", "y", "radius", "color"}, 
			{"sortBy"}, {"start", "end"}, {"name"}, {"x", "y", "radius", "saveMap"},
			{"x", "y"}, {"start", "end", "saveMap", "saveHTML"}
	};
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	//																						   //
	//							Basic creating methods										   //
	//																						   //
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	//creates root node to the results tag
	private Element createNode(String name) {
		Element e = results.createElement(name);
		//makes sures to add it to the right node
		if(results.getFirstChild() != null) {
			Element res = (Element) results.getFirstChild();
			res.appendChild(e);
		} else {
			results.appendChild(e);
		}
		return e;
	}
	
	private String[] getParams(Element command) {
		switch (command.getNodeName()){
			//PART 1 COMMANDS
			case CommandParser.CREATE_CITY : return params[1];
			case CommandParser.DELETE_CITY : return params[4];			
			case CommandParser.CLEAR_ALL : return params[0];			
			case CommandParser.LIST_CITIES : return params[2];			
			case CommandParser.MAP_CITY : return params[4];			
			case CommandParser.UNMAP_CITY : return params[4];			
			case CommandParser.PRINT_PRQT : return params[0];			
			case CommandParser.SAVE_MAP : return params[4];			
			case CommandParser.RANGE_CITIES : return params[5];			
			case CommandParser.NEAREST_CITY : return params[6];	
			// PART 2 COMMANDS
			case CommandParser.PRINT_AVL : return params[0];			
			case CommandParser.PRINT_PM : return params[0];			
			case CommandParser.MAP_ROAD : return params[3];			
			case CommandParser.RANGE_ROADS : return params[5];			
			case CommandParser.NEAREST_ROAD : return params[6];			
			case CommandParser.NEAREST_ISO_CITY : return params[6];			
			case CommandParser.NEAREST_CITY_ROAD : return params[3];			
			case CommandParser.SHORTEST_PATH : return params[7];
			default : return params[0];
		}
	}
	
	private Element createParams(Element command) {
		Element parameters = results.createElement("parameters");
		String[] p = getParams(command);
		for (String parameter : p) {
			if (!command.getAttribute(parameter).isEmpty()) {
				Element attr = results.createElement(parameter);
				attr.setAttribute("value", command.getAttribute(parameter));
				parameters.appendChild(attr);
			}
			
		}
		return parameters;
	}
	
	//creates "city" node params, output and nodes
	public Element cityXML(City newCity, Element root) {
		Element x = results.createElement("x");
		Element y = results.createElement("y");
		Element name = results.createElement("name");
		Element radius = results.createElement("radius");
		Element color = results.createElement("color");
		name.setAttribute("value", newCity.getName());
		x.setAttribute("value", String.valueOf((int) newCity.getX()));
		y.setAttribute("value", String.valueOf((int) newCity.getY()));
		radius.setAttribute("value", String.valueOf(newCity.getRadius()));
		color.setAttribute("value", newCity.getColor());
		root.appendChild(x);
		root.appendChild(y);
		root.appendChild(name);
		root.appendChild(radius);
		root.appendChild(color);
		return root;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	//																						   //
	//									Success Calls										   //
	//																						   //
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	//no output 
	public void success(String method, Element input) {
		Element root = createNode("success");
		Element command = results.createElement("command");
		command.setAttribute("name", method);
		if (!input.getAttribute("id").isEmpty()) {
			command.setAttribute("id", input.getAttribute("id"));
		}
		Element parameters = createParams(input);
		Element output = results.createElement("output");
		root.appendChild(command);
		root.appendChild(parameters);
		root.appendChild(output);
	}
	
	//with output 
	public void success(String method, Element output, Element input) {
		Element root = createNode("success");
		Element command = results.createElement("command");
		if (!input.getAttribute("id").isEmpty()) {
			command.setAttribute("id", input.getAttribute("id"));
		}
		Element parameters = createParams(input);
		command.setAttribute("name", method);
		root.appendChild(command);
		root.appendChild(parameters);
		root.appendChild(output);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	//																						   //
	//										Error Calls										   //
	//																						   //
	/////////////////////////////////////////////////////////////////////////////////////////////
		
	//error for general cases
	public void error(String method, String type, Element input) {
		Element root = createNode("error");
		Element command = results.createElement("command");
		command.setAttribute("name", method);		
		if (!input.getAttribute("id").isEmpty()) {
			command.setAttribute("id", input.getAttribute("id"));
		}
		Element params = createParams(input);
		root.setAttribute("type", type);
		root.appendChild(command);
		root.appendChild(params);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	//																						   //
	//									Helper Calls										   //
	//																						   //
	/////////////////////////////////////////////////////////////////////////////////////////////

	//creates list cities XML
	public Element listOutput(String order) {
		Element output = results.createElement("output");
		Element cityList = results.createElement("cityList");
		Dictionary dict = new Dictionary();
		if (order.equals("coordinate")) {
			for (City temp : dict.getSet()) {
				Element t = results.createElement("city");
				t.setAttribute("color", temp.getColor());
				t.setAttribute("name", temp.getName());
				t.setAttribute("x", String.valueOf((int) temp.getX()));
				t.setAttribute("y", String.valueOf((int) temp.getY()));
				t.setAttribute("radius", String.valueOf(temp.getRadius()));
				cityList.appendChild(t);
			}
		} else {
			for (String name : dict.getMap().keySet()) {
				Element t = results.createElement("city");
				City temp = dict.get(name);
				t.setAttribute("color", temp.getColor());
				t.setAttribute("name", temp.getName());
				t.setAttribute("x", String.valueOf((int) temp.getX()));
				t.setAttribute("y", String.valueOf((int) temp.getY()));
				t.setAttribute("radius", String.valueOf(temp.getRadius()));
				cityList.appendChild(t);
			}
		}
		output.appendChild(cityList);
		return output;
	}
	
	@SuppressWarnings("rawtypes")
	public Element AvlOutput(AvlGTree.AvlGNode i, Element parent) {
		if (i == null) {
			Element node = MeeshQuest.results.createElement("emptyChild");
			return node;
		} else {
			Element node = MeeshQuest.results.createElement("node");
			node.setAttribute("key", String.valueOf(i.getKey()));
			node.setAttribute("value", String.valueOf(i.getValue().toString()));
			node.appendChild(AvlOutput(i.right, node));
			node.appendChild(AvlOutput(i.left, node));
			return node;
		}
	}
	
	public Element PMOutput(ArrayList<PMNode> PMQT) {
		Element quadTree = results.createElement("quadtree");
		quadTree.setAttribute("order", "3");
		if (PMQT.get(0) instanceof PMQT.grayNode) {
			Element gray = results.createElement("gray");
			gray.setAttribute("x", String.valueOf((int) ((PMQT.grayNode) PMQT.get(0)).getCenter().getX()));
			gray.setAttribute("y", String.valueOf((int) ((PMQT.grayNode) PMQT.get(0)).getCenter().getY()));
			PMQT.remove(0);
			quadTree.appendChild(gray);
			PMgrayNode(PMQT, gray);
		} else {
			printPMNode(PMQT, quadTree);
		} 
		return quadTree;
	}
	
	public Element cityRangeOutput(TreeSet<City> list) {
		Element cityList = results.createElement("cityList");
		for (City c : list) {
			Element city = results.createElement("city");
			city.setAttribute("name", c.getName());
			city.setAttribute("x", String.valueOf((int) c.getX()));
			city.setAttribute("y", String.valueOf((int) c.getY()));
			city.setAttribute("color", c.getColor());
			city.setAttribute("radius", String.valueOf(c.getRadius()));
			cityList.appendChild(city);
		}
		return cityList;
	}
	
	public Element roadRangeOutput(TreeSet<Road> list) {
		Element cityList = results.createElement("roadList");
		TreeSet<Road> temp = orderRoads(list);
		for (Road r : temp) {
			Element road = results.createElement("road");
			road.setAttribute("start", r.getStartCity().getName());
			road.setAttribute("end", r.getEndCity().getName());
			cityList.appendChild(road);
		}
		return cityList;
	}
	
	private Element printPMNode(ArrayList<PMNode> PMQT, Element qt) {
		if (PMQT.get(0) instanceof PMQT.whiteNode) {
			Element white = results.createElement("white");
			qt.appendChild(white);
			PMQT.remove(0);
			return qt;
		} else if (PMQT.get(0) instanceof PMQT.blackNode) {
			PMQT.blackNode b = (PMQT.blackNode) PMQT.get(0);
			Element black = results.createElement("black");
			black.setAttribute("cardinality", String.valueOf(b.numCities()+b.numRoads()));
			// cardinality
			if (b.numCities() > 0) {
				City c = b.getCity();
				Element city;
				if (c.isIsolated()) {
					 city = results.createElement("isolatedCity");
				} else {
					city = results.createElement("city");
				}	
				city.setAttribute("color", c.getColor());
				city.setAttribute("name", c.getName());
				city.setAttribute("radius", String.valueOf(c.getRadius()));
				city.setAttribute("x", String.valueOf((int) c.getX()));
				city.setAttribute("y", String.valueOf((int) c.getY()));
				black.appendChild(city);
			}
			TreeSet<Road> roads = orderRoads(b.getRoads());
			for (Road r : roads) {
				Element road = results.createElement("road");
				road.setAttribute("start", r.getStartCity().getName());
				road.setAttribute("end", r.getEndCity().getName());
				black.appendChild(road);
			}
			PMQT.remove(0);
			qt.appendChild(black);
			return qt;
		} else if (PMQT.get(0) instanceof PMQT.grayNode){
			Element gray = results.createElement("gray");
			gray.setAttribute("x", String.valueOf((int) ((PMQT.grayNode) PMQT.get(0)).getCenter().getX()));
			gray.setAttribute("y", String.valueOf((int) ((PMQT.grayNode) PMQT.get(0)).getCenter().getY()));
			PMQT.remove(0);
			qt.appendChild(gray);
			//System.out.println("CALLING PMGray from PMNode");
			PMgrayNode(PMQT, gray);
			return gray;
		}
		return null;
	}
	
	private void PMgrayNode(ArrayList<PMNode> PMQT, Element parent) {
		for (int x = 0; x < 4; x++) {

			//System.out.println();
			if (PMQT.get(0) instanceof PMQT.grayNode) {
				printPMNode(PMQT, parent);
			} else if (PMQT.get(0) instanceof PMQT.blackNode) {
				//System.out.println("inside gray node "+c+" : black node"+PMQT.get(0)+" X = "+x);
				printPMNode(PMQT, parent);
			} else if (PMQT.get(0) instanceof PMQT.whiteNode) {
				//System.out.println("inside gray node "+c+" : white node"+PMQT.get(0)+" X = "+x);
				printPMNode(PMQT, parent);
			}
		}

		
	}
	
	public Element shortestPath(ArrayList<Road> path, double dist) {
		Element p = MeeshQuest.results.createElement("path");
		p.setAttribute("hops", String.valueOf(path.size()));
		//System.out.println(dist);
		DecimalFormat df = new DecimalFormat("0.000");		
		p.setAttribute("length", df.format(dist));
		for (int x = path.size()-1; x > 0 ; x--) {
			Road first = path.get(x);
			Road second = path.get(x-1);
			Element road = MeeshQuest.results.createElement("road");
			road.setAttribute("start", first.getStartCity().getName());
			road.setAttribute("end", first.getEndCity().getName());
			p.appendChild(road);
			City p1 = null, p2 = null, p3 = null;
			Arc2D.Double arc = new Arc2D.Double();
			if (first.getStartCity() == second.getStartCity() || first.getStartCity() == second.getEndCity()) {
				p1 = first.getEndCity();
				p2 = first.getStartCity();
				if (p2 == second.getEndCity())
					p3 = second.getStartCity();
				else 
					p3 = second.getEndCity();
				arc.setArcByTangent(p1.toPoint2D(), p2.toPoint2D(), p3.toPoint2D(), 1);
			} else if (first.getEndCity() == second.getStartCity() || first.getEndCity() == second.getEndCity()) {
				p1 = first.getStartCity();
				p2 = first.getEndCity();
				if (p2 == second.getEndCity())
					p3 = second.getStartCity();
				else 
					p3 = second.getEndCity();
				arc.setArcByTangent(p1.toPoint2D(), p2.toPoint2D(), p3.toPoint2D(), 1);
			}
			arc.setArcByTangent(p1.toPoint2D(), p2.toPoint2D(), p3.toPoint2D(), 1);
			Element dir;
			if (Math.abs(arc.getAngleExtent()) < 45)
				dir = MeeshQuest.results.createElement("straight");
			else {
				if (arc.getAngleExtent() < 0)
					dir = MeeshQuest.results.createElement("left");
				else 
					dir = MeeshQuest.results.createElement("right");
			}
			p.appendChild(dir);
		}
		if (path.size() > 0) {
			Element road = MeeshQuest.results.createElement("road");
			road.setAttribute("start", path.get(0).getStartCity().getName());
			road.setAttribute("end", path.get(0).getEndCity().getName());
			p.appendChild(road);
		}
		return p;
	}
	
	/*
	 * 
	 * This code is used for PRQT and since Node name is the same I am going to get rid of it so that it has the same abstract
	 * 
	 */
	
	public Element PRQTOutput(ArrayList<Node> list) {
		Element PRQT = results.createElement("quadtree");
		if (list.get(0) instanceof grayNode) {
			Element gray = results.createElement("gray");
			gray.setAttribute("x", String.valueOf((int) list.get(0).getCenterX()));
			gray.setAttribute("y", String.valueOf((int) list.get(0).getCenterY()));
			list.remove(0);
			PRQT.appendChild(gray);
			grayNode(list, gray);
		} else {
			printNode(list, PRQT);
		} 
		return PRQT;
	}
	
	private Element printNode(ArrayList<Node> list, Element parent) {
		if (list.get(0) instanceof whiteNode) {
			Element white = results.createElement("white");
			parent.appendChild(white);
			list.remove(0);
			return parent;
		} else if (list.get(0) instanceof blackNode) {
			Element black = results.createElement("black");
			black.setAttribute("name", ((blackNode) list.get(0)).getCity().getName());
			black.setAttribute("x", String.valueOf((int) ((blackNode) list.get(0)).getCity().getX()));
			black.setAttribute("y", String.valueOf((int) ((blackNode) list.get(0)).getCity().getY()));
			list.remove(0);
			parent.appendChild(black);
			return parent;
		} else {
			Element gray = results.createElement("gray");
			gray.setAttribute("x", String.valueOf((int) ((grayNode) list.get(0)).getX()));
			gray.setAttribute("y", String.valueOf((int) ((grayNode) list.get(0)).getY()));
			list.remove(0);
			parent.appendChild(gray);
			return gray;
		}
	}
	
	private void grayNode(ArrayList<Node> list, Element parent) {
		for (int x = 0; x < 4; x++) {
			//System.out.println(list.get(0).toString());
			if (list.get(0) instanceof grayNode) {
				//System.out.println("gray node"+x);
				grayNode(list, printNode(list, parent));
			} else if (list.get(0) instanceof blackNode) {
				//System.out.println("black node in gray"+x);
				printNode(list, parent);
			} else if (list.get(0) instanceof whiteNode) {
				//System.out.println("white node in gray"+x);
				printNode(list, parent);
			}
		}
		//System.out.println("END OF FOR LOOP"+level);
	}
	
	public TreeSet<Road> orderRoads(TreeSet<Road> list){
		ArrayList<Road> temp = new ArrayList<Road>();
		for (Road r : list) {
			if (r.getStartCity().compareName(r.getEndCity()) < 0) {
				temp.add(new Road(r.getEndCity(), r.getStartCity()));
			} else {
				temp.add(r);
			}
		}
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
		for (Road r : temp) {
			results.add(r);
		}
		return results;
	}
	
	public TreeSet<Road> orderRoads(ArrayList<Road> list){
		ArrayList<Road> temp = new ArrayList<Road>();
		for (Road r : list) {
			if (r.getStartCity().compareName(r.getEndCity()) < 0) {
				temp.add(new Road(r.getEndCity(), r.getStartCity()));
			} else {
				temp.add(r);
			}
		}
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
		for (Road r : temp) {
			results.add(r);
		}
		return results;
	}

}
