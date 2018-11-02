package cmsc420.structure;

import java.util.TreeMap;
import java.util.TreeSet;

public class Dictionary {
	private final static TreeMap<String, City> nameMap = new TreeMap<String, City>(new City.nameComparator());	
	private final static TreeSet<City> coordinateSet = new TreeSet<City>(new City.Point2DComparator());	
	
	//adds city to the data dictionaries
	public void add(City newCity) {
		nameMap.put(newCity.getName(), newCity);
		coordinateSet.add(newCity);
	}
	
	public void delete(String name) {
		coordinateSet.remove(nameMap.remove(name));
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
