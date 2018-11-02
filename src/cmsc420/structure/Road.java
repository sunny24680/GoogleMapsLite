package cmsc420.structure;

import java.awt.geom.Line2D;

import cmsc420.geom.Geometry2D;

@SuppressWarnings("serial")
public class Road extends Line2D.Float implements Geometry2D{
	private City startCity, endCity;
	
	public Road(City s, City e) {
		super(s.x, s.y, e.x, e.y);
		setStartCity(s);
		setEndCity(e);
	}

	@Override
	public int getType() {
		return SEGMENT;
	}

	public City getStartCity() {
		return startCity;
	}

	public void setStartCity(City startCity) {
		this.startCity = startCity;
	}

	public City getEndCity() {
		return endCity;
	}

	public void setEndCity(City endCity) {
		this.endCity = endCity;
	}
	
	public Line2D toLine2D() {
		return new Line2D.Float(startCity.x, startCity.y, endCity.x, endCity.y);
	}
}
