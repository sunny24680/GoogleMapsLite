package cmsc420.commands;
//change package to where meeshquest is located
import cmsc420.meeshquest.part2.MeeshQuest;
import cmsc420.structure.City;
import cmsc420.structure.Dictionary;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import cmsc420.structure.PRQT.*;

//Deals with all the XML code 
public class XMLHandler {
	
	Document results = MeeshQuest.results;
	
	//creates root node to the results tag
	public Element createNode(String name) {
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
	
	public Element createParams(Element command) {
		Element parameters = results.createElement("parameters");
		NamedNodeMap temp = command.getAttributes();
		for (int x = 0; x < temp.getLength(); x++) {
			Element attr = results.createElement(temp.item(x).getNodeName());
			attr.setAttribute("value", temp.item(x).getNodeValue());
			parameters.appendChild(attr);
		}
		return parameters;
	}
	
	//creates "city" node params, output and nodes
	public void cityXML(Element root, City newCity) {
		Element parameters = results.createElement("parameters");
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
		parameters.appendChild(name);
		parameters.appendChild(x);
		parameters.appendChild(y);
		parameters.appendChild(radius);
		parameters.appendChild(color);
		root.appendChild(parameters);
		if (root.getNodeName().equals("success")) {
			Element output = results.createElement("output");
			root.appendChild(output);
		}
	}
	
	//success for createCity
	public void success(City newCity) {
		Element root = createNode("success");
		Element command = results.createElement("command");
		command.setAttribute("name", "createCity");
		root.appendChild(command);
		cityXML(root, newCity);
	}
	
	//no output 
	public void success(String method, Element input) {
		Element root = createNode("success");
		Element command = results.createElement("command");
		command.setAttribute("name", method);
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
		Element parameters = createParams(input);
		command.setAttribute("name", method);
		root.appendChild(command);
		root.appendChild(parameters);
		root.appendChild(output);
	}
	
	//a success with a specific params
	public void success(String method, Element params, Element output, Element input) {
		Element root = createNode("success");
		Element command = results.createElement("command");
		command.setAttribute("name", method);
		root.appendChild(command);
		root.appendChild(params);
		root.appendChild(output);
	}

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
	
	public Element printNode(ArrayList<Node> list, Element parent) {
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
	
	public void grayNode(ArrayList<Node> list, Element parent) {
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
	
	public Element rangeOutput(ArrayList<City> list) {
		Element cityList = results.createElement("cityList");
		for (int x = 0; x < list.size(); x++) {
			Element city = results.createElement("city");
			city.setAttribute("name", list.get(x).getName());
			city.setAttribute("x", String.valueOf((int) list.get(x).getX()));
			city.setAttribute("y", String.valueOf((int) list.get(x).getY()));
			city.setAttribute("color", list.get(x).getColor());
			city.setAttribute("radius", String.valueOf(list.get(x).getRadius()));
			cityList.appendChild(city);
		}
		return cityList;
	}
	
	//error output for no parameters
	public void error(String method, String type) {
		Element root = createNode("error");
		root.setAttribute("type", type);
		Element command = results.createElement("command");
		command.setAttribute("name", method);
		root.appendChild(command);
	}
		
	//error for mapCity when city doesnt exists
	public void error(String method, String type, Element input) {
		Element root = createNode("error");
		root.setAttribute("type", type);
		Element command = results.createElement("command");
		command.setAttribute("name", method);
		root.appendChild(command);
		Element params = createParams(input);
		root.appendChild(params);
	}
	
	public void error(String method, String type, Element params, Element input) {
		Element root = createNode("error");
		root.setAttribute("type", type);
		Element command = results.createElement("command");
		command.setAttribute("name", method);
		root.appendChild(command);
		root.appendChild(params);
	}
	
	//error for create City
	public void error(String method, String type, City errCity) {
		Element root = createNode("error");
		root.setAttribute("type", type);
		Element command = results.createElement("command");
		command.setAttribute("name", method);
		root.appendChild(command);
		cityXML(root, errCity);	
	}
}
