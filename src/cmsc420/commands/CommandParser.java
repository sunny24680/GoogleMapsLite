package cmsc420.commands;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import org.w3c.dom.Element;

import cmsc420.structure.PRQT.*;
import cmsc420.stucture.PMQT.*;
import cmsc420.stucture.PMQT.PMNode;
import cmsc420.structure.*;
import cmsc420.structure.Dictionary;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.meeshquest.part2.*;

public class CommandParser {
	protected final static String CREATE_CITY = "createCity";
	protected final static String DELETE_CITY = "deleteCity";
	protected final static String LIST_CITIES = "listCities";
	protected final static String CLEAR_ALL = "clearAll";
	protected final static String MAP_CITY = "mapCity";
	protected final static String UNMAP_CITY = "unmapCity";
	protected final static String PRINT_PRQT = "printPRQuadtree";
	protected final static String SAVE_MAP = "saveMap";
	protected final static String RANGE_CITIES = "rangeCities";
	protected final static String NEAREST_CITY = "nearestCity";
	protected final static String PRINT_AVL = "printAvlTree";
	protected final static String PRINT_PM = "printPMQuadtree";
	protected final static String MAP_ROAD = "mapRoad";
	protected final static String RANGE_ROADS = "rangeRoads";
	protected final static String NEAREST_ROAD = "nearestRoad";
	protected final static String NEAREST_ISO_CITY = "nearestIsolatedCity";
	protected final static String NEAREST_CITY_ROAD = "nearestCityToRoad";
	protected final static String SHORTEST_PATH = "shortestPath";
	private final static Dictionary dict = new Dictionary();
	private final static XMLHandler xml = new XMLHandler();
	public static PRQT tree;
	public static PM3QT PMTree;
	public static AdjacencyList roadMap = new AdjacencyList(); 
	
	public static Element parseCommand(Element command){
		Element res = (Element) MeeshQuest.results.getFirstChild();
		switch (command.getNodeName()){
			case CREATE_CITY : createCity(command);
			break;
			case DELETE_CITY : deleteCity(command);
			break;
			case CLEAR_ALL : clearAll(command);
			break;
			case LIST_CITIES : listCities(command);
			break;
			case MAP_CITY : mapCity(command);
			break;
			case UNMAP_CITY : unmapCity(command);
			break;
			case PRINT_PRQT : printPRQT(command);
			break;
			case SAVE_MAP : saveMap(command);
			break;
			case RANGE_CITIES : rangeCities(command);
			break;
			case NEAREST_CITY : nearestCity(command);
			break;
			case PRINT_AVL : printAVL(command);
			break;
			case PRINT_PM : printPM(command);
			break;
			case MAP_ROAD : mapRoad(command);
			break;
			case RANGE_ROADS : rangeRoads(command);
			break;
			case NEAREST_ROAD : nearestRoad(command);
			break;
			case NEAREST_ISO_CITY : nearestIsoCity(command);
			break;
			case NEAREST_CITY_ROAD : nearestCityToRoad(command);
			break;
			case SHORTEST_PATH : shortestPath(command);
			default : ;
		}
		return res;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	//																						   //
	//									Function Calls										   //
	//																						   //
	/////////////////////////////////////////////////////////////////////////////////////////////

	 // adds a city to the 2 dictionaries
	 // checks for duplicates (same name or same coordinates)
	
	private static void createCity(Element command) {
		City temp = new City(command.getAttribute("name"), Float.parseFloat(command.getAttribute("x")), Float.parseFloat(command.getAttribute("y")), 
				Integer.parseInt(command.getAttribute("radius")), command.getAttribute("color"), false);
		if (dict.containsCoordinate(temp)) {
			xml.error("createCity", "duplicateCityCoordinates", command);
		} else if (dict.contains(temp)) {
			xml.error("createCity", "duplicateCityName", command);
		} else {
			xml.success("createCity", command);
			dict.add(temp);
		}
	}
	
	private static void deleteCity(Element command) {
		String name = command.getAttribute("name");
		if (dict.containsName(name)) {
			Element output = MeeshQuest.results.createElement("output");
			if (PMTree.containsCity(dict.get(name))) {
				//unmap the city from PRQT first
				Element unMap = MeeshQuest.results.createElement("cityUnmapped");
				City temp = dict.get(name);
				unMap = xml.cityXML(temp, unMap);
				output.appendChild(unMap);
				// NEED TO MAKE PMTREE
				tree.removeCity(dict.get(name));
			}
			//delete city from data dictionary
			dict.delete(name);
			xml.success("deleteCity", output, command);
		} else {
			//error output for cityDoesNotExist
			xml.error("deleteCity", "cityDoesNotExist", command);
		}
	}
	
	private static void listCities(Element command) {
		String sortBy = command.getAttribute("sortBy");

		if (dict.isEmpty()) {
			xml.error("listCities", "noCitiesToList", command);
		} else {
			Element output = xml.listOutput(sortBy);
			xml.success("listCities", output, command);
		}
	}
	
	private static void clearAll(Element command) {
		dict.clearAll();
		PMTree.clearAll();
		roadMap.clearAll();
		xml.success("clearAll", command);
	}
	
	private static void mapCity(Element command) {
		String name = command.getAttribute("name");
		//System.out.println("adding a city "+name);

		if (dict.containsName(name)) {
			//checks if the city is already mapped
			if (PMTree.containsCity(dict.get(name))) {
				//error output CityAlreadyMapped
				xml.error("mapCity", "cityAlreadyMapped", command);
			} else if (dict.get(name).x > tree.dimensions.getWidth() || dict.get(name).y > tree.dimensions.getHeight()) {
				//error output CityOutOfBounds
				xml.error("mapCity", "cityOutOfBounds", command);
			} 
			else {
				//map the city in the PRQT
				City c = dict.get(name);
				c.changeToIsolated();
				PMTree.add(dict.get(name));
				Element output = MeeshQuest.results.createElement("output");
				xml.success("mapCity", output, command);
			}
		} else {
			//error output nameNotInDictionary 
			xml.error("mapCity", "nameNotInDictionary", command);
		}
	}
	
	private static void unmapCity(Element command) {
		String name = command.getAttribute("name");
		if (dict.containsName(name)) {
			if (PMTree.containsCity(dict.get(name))) {
				//NEED TO MAKE PMQT
				tree.removeCity(dict.get(name));
				xml.success("unmapCity", command);
			} else {
				// error output cityNotMapped
				xml.error("unmapCity", "cityNotMapped", command);
			}
		} else {
			//error output nameNotInDictionary 
			xml.error("unmapCity", "nameNotInDictionary", command);
		}
	}
	
	//IGNORE PRQT NOT NEEDED FOR PART 2
	private static void printPRQT(Element command) {
		//System.out.println("PRINTING");
		if (tree.isEmpty()) {
			//error output mapIsEmpty
			xml.error("printPRQuadtree", "mapIsEmpty", command);
		} else {
			ArrayList<Node> PRQT = tree.printPRQT();
			Element output = MeeshQuest.results.createElement("output");
			output.appendChild(xml.PRQTOutput(PRQT));
			xml.success("printPRQuadtree", output, command);
		}
	}
	
	private static void saveMap(Element command) {
		String filename = command.getAttribute("name");
		try {
			MeeshQuest.canvas.save(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//technically should only print a success output
		Element output = MeeshQuest.results.createElement("output");
		xml.success("saveMap", output, command);
	}
	
	private static void rangeCities(Element command) {
		float xpos = Float.parseFloat(command.getAttribute("x"));
		float ypos = Float.parseFloat(command.getAttribute("y"));
		int r = Integer.parseInt(command.getAttribute("radius"));
		Circle2D.Float circle = new Circle2D.Float(xpos, ypos, r);
		TreeSet<City> inRange = PMTree.citiesIn(circle);
		if (inRange.size() == 0) {
			xml.error("rangeCities", "noCitiesExistInRange", command);
		} else {
			Element output = MeeshQuest.results.createElement("output");
			output.appendChild(xml.cityRangeOutput(inRange));
			xml.success("rangeCities", output, command);
		}
	}
	
	private static void rangeRoads(Element command) {
		float xpos = Float.parseFloat(command.getAttribute("x"));
		float ypos = Float.parseFloat(command.getAttribute("y"));
		int r = Integer.parseInt(command.getAttribute("radius"));
		Circle2D.Float circle = new Circle2D.Float(xpos, ypos, r);
		TreeSet<Road> inRange = PMTree.roadsIn(circle);
		if (inRange.size() == 0) {
			xml.error("rangeRoads", "noRoadsExistInRange", command);
		} else {
			Element output = MeeshQuest.results.createElement("output");
			output.appendChild(xml.roadRangeOutput(inRange));
			xml.success("rangeRoads", output, command);
		}
	}
	
	private static void nearestCity (Element command) {		
		float xpos = Float.parseFloat(command.getAttribute("x"));
		float ypos = Float.parseFloat(command.getAttribute("y"));		
		Point2D.Float loc = new Point2D.Float(xpos, ypos);
		City near = PMTree.nearestCity(loc);
		if (near != null) {
			Element output = MeeshQuest.results.createElement("output");
			Element city = MeeshQuest.results.createElement("city");
			city.setAttribute("x", String.valueOf((int) near.getX()));
			city.setAttribute("y", String.valueOf((int) near.getY()));
			city.setAttribute("name", near.getName());
			city.setAttribute("radius", String.valueOf((int) near.getRadius()));
			city.setAttribute("color", near.getColor());
			output.appendChild(city);
			xml.success("nearestCity", output, command);
		} else {
			xml.error("nearestCity", "cityNotFound", command);
		}
	}
	
	private static void printAVL (Element command) {
		if (dict.isEmpty()) {
			xml.error("printAvlTree", "emptyTree", command);
		} else {
			Element output = MeeshQuest.results.createElement("output");
			Element AVL = MeeshQuest.results.createElement("AvlGTree");
			AVL.setAttribute("cardinality", String.valueOf(dict.getTree().size()));
			AVL.setAttribute("height", String.valueOf(dict.getTree().height()));
			AVL.setAttribute("maxImbalance", String.valueOf(dict.getTree().imbalance()));

			AVL.appendChild(xml.AvlOutput(dict.getTree().root, AVL));
			output.appendChild(AVL);
			xml.success("printAvlTree", output, command);
		}
	}
	
	private static void printPM (Element command) {
		if (PMTree.isEmpty()) {
			xml.error("printPMQuadtree", "mapIsEmpty", command);
		} else {
			ArrayList<PMNode> PMQT = PMTree.getMap();
			Element output = MeeshQuest.results.createElement("output");
			output.appendChild(xml.PMOutput(PMQT));
			xml.success("printPMQuadtree", output, command);
		}
	}
	
	private static void mapRoad (Element command) {
		City start = dict.get(command.getAttribute("start"));
		City end = dict.get(command.getAttribute("end"));
		if (start == null) {
			xml.error("mapRoad", "startPointDoesNotExist", command);
		} else if (end == null) {
			xml.error("mapRoad", "endPointDoesNotExist", command);
		} else if (start.equals(end)) {
			xml.error("mapRoad", "startEqualsEnd", command);
		} else {
			if (start.isIsolated() || end.isIsolated()) {
				xml.error("mapRoad", "startOrEndIsIsolated", command);
			} else {
				Road road = new Road(start, end);
				//System.out.println("CHECKING IF ROAD "+road+" is already MAPPED "+dict.containsRoad(road));
				//dict.printRoads(road);
				//checks if road is already mapped
				if (dict.containsRoad(road)) {
					xml.error("mapRoad", "roadAlreadyMapped", command);
				} else if (!Inclusive2DIntersectionVerifier.intersects(road.toLine2D(), PMTree.dimensions)) {
					xml.error("mapRoad", "roadOutOfBounds", command);
				} else {
					if ((start.x <= tree.dimensions.getWidth() && start.x >= 0.0) && (start.y <= tree.dimensions.getHeight() && start.y >= 0.0)) {
						PMTree.add(start);
					}
					if ((end.x <= tree.dimensions.getWidth() && end.x >= 0.0) && (end.y <= tree.dimensions.getHeight() && end.y >= 0.0))
						PMTree.add(end);
					Element output = MeeshQuest.results.createElement("output");
					Element roadsCreated = MeeshQuest.results.createElement("roadCreated");
					roadsCreated.setAttribute("start", command.getAttribute("start"));
					roadsCreated.setAttribute("end", command.getAttribute("end"));
					output.appendChild(roadsCreated);
					xml.success("mapRoad", output, command);
					dict.addRoad(road);
					PMTree.add(road);
					//System.out.println("Start = "+start.getName());
					//System.out.println("END = "+end.getName());
					roadMap.add(start, end);
				}
			}
		}
	}
	
	private static void nearestRoad(Element command) {
		float xpos = Float.parseFloat(command.getAttribute("x"));
		float ypos = Float.parseFloat(command.getAttribute("y"));
		Point2D.Float loc = new Point2D.Float(xpos, ypos);
		Road near = PMTree.nearestRoad(loc);
		if (near != null) {
			Element output = MeeshQuest.results.createElement("output");
			TreeSet<Road> roads = xml.orderRoads(new ArrayList<Road>(Arrays.asList(near)));
			for (Road r : roads) {
				Element road = MeeshQuest.results.createElement("road");
				road.setAttribute("start", r.getStartCity().getName());
				road.setAttribute("end", r.getEndCity().getName());
				output.appendChild(road);
			}
			xml.success("nearestRoad", output, command);
		} else {
			xml.error("nearestRoad", "roadNotFound", command);
		}
	}
	
	private static void nearestIsoCity(Element command) {
		float xpos = Float.parseFloat(command.getAttribute("x"));
		float ypos = Float.parseFloat(command.getAttribute("y"));
		Point2D.Float loc = new Point2D.Float(xpos, ypos);
		City near = PMTree.nearestIsolatedCity(loc);
		if (near != null) {
			Element output = MeeshQuest.results.createElement("output");
			Element city = MeeshQuest.results.createElement("isolatedCity");
			city.setAttribute("color", near.getColor());
			city.setAttribute("name", near.getName());
			city.setAttribute("radius", String.valueOf(near.getRadius()));
			city.setAttribute("x", String.valueOf((int) near.getX()));
			city.setAttribute("y", String.valueOf((int) near.getY()));
			output.appendChild(city);
			xml.success("nearestIsolatedCity", output, command);
		} else {
			xml.error("nearestIsolatedCity", "cityNotFound", command);
		}
	}
	
	private static void nearestCityToRoad(Element command) {
		City start = dict.get(command.getAttribute("start"));
		City end = dict.get(command.getAttribute("end"));
		if (start == null || end == null) {
			xml.error("nearestCityToRoad", "roadIsNotMapped", command);
		} else {
			Element params = MeeshQuest.results.createElement("parameters");
			Element s = MeeshQuest.results.createElement("start");
			Element e = MeeshQuest.results.createElement("end");
			s.setAttribute("value", start.getName());;
			e.setAttribute("value", end.getName());
			params.appendChild(s);
			params.appendChild(e);
			Road r = new Road(start, end);
			if (!dict.containsRoad(r)){
				xml.error("nearestCityToRoad", "roadIsNotMapped", command);
			} else {
				//System.out.println("ROAD IN NEAREST = "+r);
				City c = PMTree.nearestCityToRoad(r);
				if (c == null) {
					xml.error("nearestCityToRoad", "noOtherCitiesMapped", command);
				} else {
					Element output = MeeshQuest.results.createElement("output");
					Element city = MeeshQuest.results.createElement("city");
					city.setAttribute("name", c.getName());
					city.setAttribute("x", String.valueOf((int) c.getX()));
					city.setAttribute("y", String.valueOf((int) c.getY()));
					city.setAttribute("color", c.getColor());
					city.setAttribute("radius", String.valueOf(c.getRadius()));
					output.appendChild(city);
					xml.success("nearestCityToRoad", output, command);
				}
			}
		}
	}
	
	private static void shortestPath(Element command) {
		
		City start = dict.get(command.getAttribute("start"));
		City end = dict.get(command.getAttribute("end"));
		if ((start == null) || (!PMTree.containsCity(start))) {
			xml.error("shortestPath", "nonExistentStart", command);
		} else if ((end == null) || (!PMTree.containsCity(end))) {
			xml.error("shortestPath", "nonExistentEnd", command);
		} else {
			//System.out.println("shortest path from "+start.getName()+" to "+end.getName());
			ArrayList<Road> roads = roadMap.shortestPath(start, end);		
			if (roads == null) {
				xml.error("shortestPath", "noPathExists", command);
			} else {
				double dist = roadMap.shortestPathDist(start, end);
				Element output = MeeshQuest.results.createElement("output");
				Element path = xml.shortestPath(roads, dist);
				output.appendChild(path);
				xml.success("shortestPath", output, command);
			}
		}
		
	}
	
}
