package cmsc420.commands;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;

import cmsc420.structure.PRQT.*;
import cmsc420.structure.PRQT.Node;
import cmsc420.stucture.PMQT.*;
import cmsc420.structure.*;
import cmsc420.structure.Dictionary;
import cmsc420.meeshquest.part2.*;

public class CommandParser {
	private final static String CREATE_CITY = "createCity";
	private final static String DELETE_CITY = "deleteCity";
	private final static String LIST_CITIES = "listCities";
	private final static String CLEAR_ALL = "clearAll";
	private final static String MAP_CITY = "mapCity";
	private final static String UNMAP_CITY = "unmapCity";
	private final static String PRINT_PRQT = "printPRQuadtree";
	private final static String SAVE_MAP = "saveMap";
	private final static String RANGE_CITIES = "rangeCities";
	private final static String NEAREST_CITY = "nearestCity";
	private final static String PRINT_AVL = "printAvlTree";
	private final static Dictionary dict = new Dictionary();
	private final static XMLHandler xml = new XMLHandler();
	public static PRQT tree;
	public static PM3QT AVLTree;
	
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
			default : ;
		}
		return res;
	}
	
	/* adds a city to the 2 dictionaries
	 * checks for duplicates (same name or same coordinates)
	*/
	private static void createCity(Element command) {
		City temp = new City(command.getAttribute("name"), Float.parseFloat(command.getAttribute("x")), Float.parseFloat(command.getAttribute("y")), 
				Integer.parseInt(command.getAttribute("radius")), command.getAttribute("color"), true);
		//check for error first
		//check for same coordinate
		if (dict.containsCoordinate(temp)) {
			xml.error("createCity", "duplicateCityCoordinates", temp);
		} else if (dict.contains(temp)) {
			xml.error("createCity", "duplicateCityName", temp);
		} else {
			//success xml
			xml.success(temp);
			dict.add(temp);
		}
	}
	
	private static void deleteCity(Element command) {
		String name = command.getAttribute("name");
		if (dict.containsName(name)) {
			Element output = MeeshQuest.results.createElement("output");
			if (tree.contains(dict.get(name))) {
				//unmap the city from PRQT first
				Element unMap = MeeshQuest.results.createElement("cityUnmapped");
				City temp = dict.get(name);
				unMap.setAttribute("name", temp.getName());
				unMap.setAttribute("x", String.valueOf((int) temp.getX()));
				unMap.setAttribute("y", String.valueOf((int) temp.getY()));
				unMap.setAttribute("color", temp.getColor());
				unMap.setAttribute("radius", String.valueOf(temp.getRadius()));
				output.appendChild(unMap);
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
	
	/* Lists all the cities in the dictionaries based on the sortBy version
	 * If no cities are present then returns error
	 */
	private static void listCities(Element command) {
		String sortBy = command.getAttribute("sortBy");

		if (dict.isEmpty()) {
			xml.error("listCities", "noCitiesToList", command);
		} else {
			Element output = xml.listOutput(sortBy);
			xml.success("listCities", output, command);
		}
	}
	
	/* clears all cities in the dictionaries
	 * cannot return an error output
	 */
	private static void clearAll(Element command) {
		dict.clearAll();
		tree.clearAll();
		xml.success("clearAll", command);
	}
	
	private static void mapCity(Element command) {
		String name = command.getAttribute("name");
		//System.out.println("adding a city "+name);

		if (dict.containsName(name)) {
			//checks if the city is already mapped
			if (tree.contains(dict.get(name))) {
				//error output CityAlreadyMapped
				xml.error("mapCity", "cityAlreadyMapped", command);
			} else if (dict.get(name).x > tree.dimensions.getWidth() || dict.get(name).y > tree.dimensions.getHeight()) {
				//error output CityOutOfBounds
				xml.error("mapCity", "cityOutOfBounds", command);
			} 
			else {
				//map the city in the PRQT
				tree.addCity(dict.get(name));
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
			if (tree.contains(dict.get(name))) {
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
		Point2D.Float center = new Point2D.Float(xpos, ypos);
		int r = Integer.parseInt(command.getAttribute("radius"));
		ArrayList<City> inRange = tree.distanceFrom(center, r);
		Element params = MeeshQuest.results.createElement("parameters");
		Element x = MeeshQuest.results.createElement("x");
		x.setAttribute("value", String.valueOf((int) xpos));
		Element y = MeeshQuest.results.createElement("y");
		y.setAttribute("value", String.valueOf((int) ypos));
		Element radius = MeeshQuest.results.createElement("radius");
		radius.setAttribute("value", String.valueOf(r));
		params.appendChild(x);
		params.appendChild(y);
		params.appendChild(radius);
		if (command.hasAttribute("saveMap")) {
			Element save = MeeshQuest.results.createElement("saveMap");
			save.setAttribute("value", command.getAttribute("saveMap"));
			params.appendChild(save);
		}
		if (inRange.size() == 0) {
			xml.error("rangeCities", "noCitiesExistInRange", params, command);
		} else {
			Element output = MeeshQuest.results.createElement("output");
			output.appendChild(xml.rangeOutput(inRange));
			xml.success("rangeCities", params, output, command);
		}
	}
	
	private static void nearestCity (Element command) {
		float xpos = Float.parseFloat(command.getAttribute("x"));
		float ypos = Float.parseFloat(command.getAttribute("y"));
		Point2D.Float loc = new Point2D.Float(xpos, ypos);
		City near = tree.nearest(loc);
		if (near != null) {
			Element output = MeeshQuest.results.createElement("output");
			Element city = MeeshQuest.results.createElement("city");
			city.setAttribute("color", near.getColor());
			city.setAttribute("name", near.getName());
			city.setAttribute("radius", String.valueOf(near.getRadius()));
			city.setAttribute("x", String.valueOf((int) near.getX()));
			city.setAttribute("y", String.valueOf((int) near.getY()));
			output.appendChild(city);
			xml.success("nearestCity", output, command);
		} else {
			xml.error("nearestCity", "mapIsEmpty", command);
		}
	}
	
	private static void printAVL (Element command) {
		if (AVLTree.isEmpty()) {
			xml.error("printAvlTree", "emptyTree", command);
		} else {
			xml.success("hello",command);
		}
	}
}
