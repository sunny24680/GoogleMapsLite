package cmsc420.structure;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import cmsc420.meeshquest.part2.MeeshQuest;
import cmsc420.sortedmap.AvlGTree;

public class Dictionary {
	private final static TreeMap<String, City> nameMap = new TreeMap<String, City>(new City.nameComparator());	
	private final static TreeSet<City> coordinateSet = new TreeSet<City>(new City.Point2DComparator());	
	private final static AvlGTree<String, City> avlTree = new AvlGTree<String, City>(MeeshQuest.g);
	private final static TreeSet<Road> roadSet = new TreeSet<Road>(new Road.nameComparator());
	
	//adds city to the data dictionaries
	public void add(City newCity) {
		nameMap.put(newCity.getName(), newCity);
		coordinateSet.add(newCity);
		avlTree.put(newCity.getName(), newCity);
	}
	
	public void addRoad(Road r) {
		//System.out.println("ADDING ROAD");
		roadSet.add(r);
		nameMap.put(r.getStartCity().getName(), r.getStartCity());
		nameMap.put(r.getEndCity().getName(), r.getEndCity());
		avlTree.put(r.getStartCity().getName(), r.getStartCity());
		avlTree.put(r.getEndCity().getName(), r.getEndCity());
	}
	
	public boolean containsRoad(Road r) {
		for (Road road : roadSet) {
			if (road.equals(r))
				return true;
		}
		return false;
	}
	
	public void delete(String name) {
		coordinateSet.remove(nameMap.remove(name));
	}
	
	public void printRoads(Road road) {
		System.out.println("ROAD SET = ");
		for (Road r : roadSet) {
			System.out.println(r+" "+r.equals(road));
		}
	}
	public boolean contains(City newCity) {
		return nameMap.containsKey(newCity.getName());
	}
	
	public boolean containsName(String name) {
		return nameMap.containsKey(name);
	}
	
	public boolean containsCoordinate(City newCity) {
		return coordinateSet.contains(newCity);
	}
	
	public boolean isEmpty() {
		return nameMap.isEmpty() && coordinateSet.isEmpty();
	}
	
	public void clearAll() {
		nameMap.clear();
		coordinateSet.clear();
	}
	
	public AvlGTree<String, City> getTree() {
		return avlTree;
	}
	
	public City get(String key) {
		return nameMap.get(key);
	}
	
	//returns keyMap for the name Map
	public TreeMap<String, City> getMap() {
		return nameMap;
	}
	
	//returns keySet for coordinate Set
	public TreeSet<City> getSet() {
		return coordinateSet;
	}
}
