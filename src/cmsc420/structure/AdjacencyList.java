package cmsc420.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class AdjacencyList {
	private TreeMap<City, TreeMap<City, Double>> list = new TreeMap<City, TreeMap<City, Double>>(new City.cityNameComparator());
	
	public void clearAll() {
		list.clear();
	}
	
	public void add(City start, City end) {
		//System.out.println("ADDING "+start.getName());
		//System.out.println("ADDING "+end.getName());
		Road r = new Road(start, end);
		TreeMap<City, Double> s = new TreeMap<City, Double>(new City.cityNameComparator());
		//checks if name is the same
		if (containsEntry(start)) {
			//System.out.println("UPDATING "+list.get(start)+start);
			s = list.get(start);
			s.put(end, r.getDist());
			list.put(start, s);
		}	
		else {
			s.put(end, r.getDist());
			list.put(start, s);
		}
		s = new TreeMap<City, Double>(new City.Point2DComparator());
		if (containsEntry(end)) {
			s = list.get(end);
			s.put(start, r.getDist());
			list.put(end, s);
		}	
		else {
			s.put(start, r.getDist());
			list.put(end, s);
		}
		//printList();
		//System.out.println();
	}
	
	private TreeMap remove (City c, TreeMap<City, Double> s) {
		TreeMap<City, Double> temp = new TreeMap<City, Double>();
		for (City city : s.keySet()) {
			if (!city.equals(c))
				temp.put(city, s.get(city));
		}
		return temp;
	}
	
	public boolean containsEntry(City c) {
		for (City start : list.keySet()) {
			if (c.equals(start))
				return true;
		}
		return false;
	}
	
	public void remove(City city) {
		list.remove(city);
		for (City c : list.keySet()) {
			list.get(c).remove(city);
		}
	}
	
	public ArrayList<Road> shortestPath(City start, City end) {
		//printList();
		if (start == end) {
			return new ArrayList<Road>();
		}
		HashMap<City, Double> dist = new HashMap<City, Double>();
		TreeSet<City> sptSet = new TreeSet<City>();
		HashMap<City, City> path = new HashMap<City, City>();
		for (City city : list.keySet()) {
            dist.put(city, Double.MAX_VALUE); 
            sptSet.add(city);
            path.put(city, null);
        }
		dist.put(start, 0.0);
		path.put(start, null);
		City cur = getMin(dist, sptSet);
		while (cur != null) {
			sptSet.remove(cur);
			for (City endPoint : list.get(cur).keySet()) {
				//System.out.println("END "+endPoint+endPoint.getName());
				//printm(dist);
				Object o = dist.get(endPoint);
				//System.out.println("O = "+o);
				double oldDist = dist.get(endPoint);
				double newDist = dist.get(cur)+list.get(cur).get(endPoint);
				//System.out.println(endPoint.getName()+" OLD DIST = "+oldDist+" : NEW DIST = "+newDist);
				if ((oldDist > newDist) && (sptSet.contains(endPoint))) {
					dist.put(endPoint, list.get(cur).get(endPoint)+dist.get(cur));
					path.put(endPoint, cur);
				}
				if (oldDist == newDist) {
					City old = path.get(endPoint);
					if (cur.compareTo(old) < 0) {
						dist.put(endPoint, list.get(cur).get(endPoint)+dist.get(cur));
						path.put(endPoint, cur);
					}
				}
			}
			cur = getMin(dist, sptSet);
			//System.out.println();
		}
		
		//print(path);
		City pointer = end;
		ArrayList<Road> result = new ArrayList<Road>();
		if (path.get(end) == null)
			return null;
		
		while (pointer != null) {
			//System.out.println("pointer = "+pointer.getName());
			City starttemp = path.get(pointer);
			if (starttemp != null) {
				Road r = new Road(starttemp, pointer);
				result.add(r);
			}
			pointer = starttemp;
		}
		return result;
	}
	
	public Double shortestPathDist(City start, City end) {
		if (start == end) {
			return 0.0;
		}
		HashMap<City, Double> dist = new HashMap<City, Double>();
		TreeSet<City> sptSet = new TreeSet<City>();
		HashMap<City, City> path = new HashMap<City, City>();
		for (City city : list.keySet()) {
            dist.put(city, Double.MAX_VALUE); 
            sptSet.add(city);
            path.put(city, null);
        }
		dist.put(start, 0.0);
		path.put(start, null);
		City cur = getMin(dist, sptSet);
		while (cur != null) {
			
			//System.out.println("CURENT = "+cur.getName());
			sptSet.remove(cur);
			//System.out.println("Starting from "+cur.getName());
			for (City endPoint : list.get(cur).keySet()) {
				double oldDist = dist.get(endPoint);
				double newDist = dist.get(cur)+list.get(cur).get(endPoint);
				//System.out.println(endPoint.getName()+" OLD DIST = "+oldDist+" : NEW DIST = "+newDist);
				if ((oldDist > newDist) && (sptSet.contains(endPoint))) {
					dist.put(endPoint, list.get(cur).get(endPoint)+dist.get(cur));
					path.put(endPoint, cur);
				}
				if (oldDist == newDist) {
					City old = path.get(endPoint);
					if (cur.compareTo(old) < 0) {
						dist.put(endPoint, list.get(cur).get(endPoint)+dist.get(cur));
						path.put(endPoint, cur);
					}
				}
			}
			cur = getMin(dist, sptSet);
			//System.out.println();
		}
		return dist.get(end);
	}
	
	private void print(HashMap<City, City> map) {
		for (City start : map.keySet()) {
			String prev; 
			if (map.get(start) == null)
				prev = "null";
			else 
				prev = map.get(start).getName();
			System.out.print("Start = "+start.getName());
			System.out.println(" Prev = "+prev);
		}
	}
	
	private void printm(HashMap<City, Double> map) {
		for (City start : map.keySet()) {
			Double prev; 
			prev = map.get(start);
			System.out.print("Start = "+start);
			System.out.println(" Value = "+prev);
		}
	}
	
	private City getMin(HashMap<City, Double> dist, TreeSet<City> sptSet) {
		double min = Double.MAX_VALUE;
		City res = null;
		for (City temp : sptSet) {
			if (dist.get(temp) < min) {
				res = temp;
				min = dist.get(temp);
			}
		}
		return res;
	}
	
	public int getIndex(String[] l, String obj) {
		int c = 0;
		for (String str : l) {
			if (obj.equals(str))
				return c;
			c++;
		}
		return -1;
	}
	
	public void printList() {
		for (City s : list.keySet()) {
			System.out.print(s.getName()+" "+s);
			System.out.print(" | ");
			for (City e : list.get(s).keySet()) {
				System.out.print(e.getName()+" "+e+" : "+list.get(s).get(e)+" | ");
			}
			System.out.println();
			System.out.print("--------------------------------------------------------------");
			System.out.println();
			
		}
	}
	
	
	
}
