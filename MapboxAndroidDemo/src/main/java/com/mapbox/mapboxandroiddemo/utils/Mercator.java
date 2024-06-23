package com.mapbox.mapboxandroiddemo.utils;

public class Mercator {
	
	
	/**
	 * @param lon
	 * @param lat
	 * @return  ��γ��תī����
	 */
	public static double[] Lonlat2Mercator(double lon,double lat, double RefLat) {
		double[] m_return = new double[2];
		double CentralMeridian=0;
		//double RefLat = 39.089751991900954;
			
		double N0 = 6378137.0 / Math.sqrt( 1-Math.pow(0.081819190843,2)*Math.pow(Math.sin(RefLat*Math.PI/180),2) ); 
	    double q1 = Math.log( Math.tan( (180.0/4.0+lat/2.0)*Math.PI/180.0 ) ); 
	    double q2 = 0.081819190843/2 * Math.log( (1+0.081819190843*Math.sin(lat*Math.PI/180.0) ) / (1-0.081819190843*Math.sin(lat*Math.PI/180.0) ) ); 
	    double q = q1 - q2 ; 
	    double x = N0 * Math.cos(RefLat*Math.PI/180.0) * ((lon-CentralMeridian)*Math.PI/180.0) ; 
	    double y = N0 * Math.cos(RefLat*Math.PI/180.0) * q ;
	          
	    m_return[0] = x;
	    m_return[1] = y;
	    return m_return;
	}
	

	/**
	 * @param x
	 * @param y
	 * @return  ī����תΪWGS84����ϵ
	 */
	public static double[] mercator2LonLat(double x, double y, double Reflat){
		double[] wpt_return = new double[2];
		//double Reflat = 39.089751991900954;
		double lon =0;
		double lat =38;
		double F = 6378137.0;
		double D = 0.081819190843;
		double change = Math.PI/180.0;
		double N0 = 0;
		double temp =0 ;
		double A = 0;
		double e = Math.E;
		double sinreflat = Math.sin(Reflat * change);
		double cosreflat = Math.cos(Reflat * change);
		lon = (x * Math.sqrt(1 - Math.pow(D, 2)*Math.pow(sinreflat, 2)))/(change * F *cosreflat);
		N0 = F/Math.sqrt(1- Math.pow(D, 2) * Math.pow(sinreflat, 2));
		temp = y/(N0 * cosreflat);
		try {
			//��������
			double delta = 2;
			final double maxDelta = 0.000001;
			int count = 0;
			while (delta > maxDelta) {
				A = temp +(D/2)*Math.log((1 + D*Math.sin(lat * change))/(1 - D*Math.sin(lat * change)));
				double dlat1 = (360/Math.PI )* Math.atan(Math.pow(e, A)) - 90 ;
				delta = Math.abs(dlat1 - lat);
				lat = dlat1;
				count ++;
				if (count > 10) {
					delta = 0.0000001;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		wpt_return[0] = lon;
		wpt_return[1] = lat;
		return wpt_return;
	}
	


}
