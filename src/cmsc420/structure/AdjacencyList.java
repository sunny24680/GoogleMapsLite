package cmsc420.structure;

import java.util.TreeMap;
import java.util.TreeSet;

public class AdjacencyList {
	private TreeMap<String, TreeSet<City>> list = new TreeMap<String, TreeSet<City>>(new City.nameComparator());
	
	public void add(City city, City neighbor) {
		TreeSet<City> s = new TreeSet<City>(new City.Point2DComparator());
		if (list.containsKey(city.getName())) {
			s = list.get(city.getName());
			s.add(neighbor);
		}	
		else {
			s.add(neighbor);
			list.put(city.getName(), s);
		}
	}
	
	public void remove(String name) {
		list.remove(name);
	}
	
	public void remove(City city) {
		list.remove(city.getName());
	}
}
