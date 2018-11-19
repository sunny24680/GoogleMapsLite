package cmsc420.meeshquest.part2;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cmsc420.drawing.CanvasPlus;
import cmsc420.structure.PRQT.PRQT;
import cmsc420.stucture.PMQT.PM3QT;
import cmsc420.xml.XmlUtility;
import cmsc420.commands.*;

public class MeeshQuest {

	//visible to classes in the same package
	public static Document results = null;
	public static int spatialHeight;
	public static int spatialWidth;
	public static CanvasPlus canvas = new CanvasPlus("MeeshQuest");
	
    public static void main(String[] args) {
    	try {
			results = XmlUtility.getDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
        	//File test = new File ("new/part1.nonFatalError.input.xml");
        	//Document doc = XmlUtility.validateNoNamespace(test);    	
        	Document doc = XmlUtility.validateNoNamespace(System.in);
        	Element commandNode = doc.getDocumentElement();
        	
        	//Gets the spatial width and height from the command node
        	spatialHeight = Integer.parseInt(commandNode.getAttribute("spatialHeight"));
        	spatialWidth = Integer.parseInt(commandNode.getAttribute("spatialWidth"));
        	
        	//canvas tree
        	CommandParser.tree = new PRQT(spatialHeight, spatialWidth);
        	CommandParser.AVLTree = new PM3QT(spatialHeight, spatialWidth);
        	
        	canvas.setFrameSize(spatialWidth, spatialHeight);
        	
        	final NodeList nl = commandNode.getChildNodes();
        	//System.out.println("command = "+nl.getLength());
        	//creating start of the XML file 
        	Element res = results.createElement("results");
        	results.appendChild(res);
        	
        	//parsing through the input
        	for (int i = 0; i < nl.getLength(); i++) {
        		if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
        			commandNode = (Element) nl.item(i);
        			
        			/* TODO: Process your commandNode here */
        			
        			CommandParser.parseCommand(commandNode);
        			
                    //CommandParser.parseCommand(commandNode);
        			
        		}
        	}
        	canvas.save("meeshMap");
        } catch (SAXException | IOException | ParserConfigurationException e) {
        	//System.out.println("Fatal Error"+e.getMessage());
        	if (results != null) {
        		results.appendChild(results.createElement("fatalError"));
        	}
		} finally {
            try {
            	//System.out.println("printing");
				XmlUtility.print(results);
				//System.out.println("printed results");
			} catch (TransformerException e) {
				e.printStackTrace();
			}
        }
    }
}
