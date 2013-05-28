package in.co.hopin.LocationHelpers;

import com.google.android.maps.GeoPoint;


public class SBGeoPoint extends GeoPoint{

	int lati;
	int longi;
  
	
	public SBGeoPoint(int d, int e) {
		super(d, e);
		this.lati=d;
		this.longi = e;

	}	
	
	public double getLatitude()
	{
		return lati/1e6;
	}
	
	public double getLongitude()
	{
		return longi/1e6;
	}  

  
}
