package it.katalpa.licz_na_zilelen.model;

import org.json.JSONObject;

import com.google.android.gms.maps.model.Marker;

public class PleaceObject implements Comparable<PleaceObject> {

	private String sName;
	private int iFavorite;
	private int iId;	
	private double fDistance;
	private int iPopularity;
	private double dLatitude;
	private double dLongitude;
	Marker marker;
	
	public PleaceObject(String sPhone,String sName)
	{
		
	}
	
	 public PleaceObject() {
		
	}

	public static PleaceObject createFromJSON(JSONObject jsonObject){
	        
	            
	        	PleaceObject obj = new PleaceObject() ;
	        	
	        	try
	        	{	        	
	        		obj.setName(jsonObject.getString("name"));
	        		obj.setFavorite(jsonObject.getInt("favorite"));
	        		obj.setId(jsonObject.getInt("id"));
	        		obj.setPopularity(jsonObject.getInt("popularity"));
	        		
	        		
	        		String str = jsonObject.getString("position");
	        		
	        		str = str.replaceAll("POINT", "");
	        		str = str.replaceAll("\\(", "");
	        		str = str.replaceAll("\\)", "");
	        		
	        		String[] commatokens = str.trim().split(" ");
	        		
	        		obj.setLatitude(Double.parseDouble(commatokens[1]));
	        		obj.setLongitude(Double.parseDouble(commatokens[0]));	        		
	        			        		
	        	}catch(Exception e){
	        		
	        	}
	        	
	        	return obj;
	    }
	
	public void setMarker(Marker m)
	{
		marker = m;
	}
	
	public Marker getMarker()
	{
		return marker;
	}
	
	public String getName() {			
		return sName;			
    }
	
	public void setName(String name) {			
		sName = name;			
    }
	
	public double getLatitude() {			
		return dLatitude;			
    }
	
    public double getLongitude() {			
		return dLongitude;			
    }   
   
	
    public void setLatitude(double lat) {			
		dLatitude = lat;			
    }
	
    public void setLongitude (double lng) {			
		dLongitude = lng;			
    }
	
	public int getFavorite(){			
		return iFavorite;			
    }
	
    public void setFavorite(int fav) {			
		iFavorite = fav;			
    }
	
    public int getId() {			
		return iId;			
    }
	
    public void setId(int id) {			
		iId = id;			
    }
    
    public double getDistance() {			
		return fDistance;
    }
	
	public void setDistance(double d) {	
		fDistance = d;
    }	
	
	public int getPopularity(){			
		return iPopularity;
    }
	
	public void setPopularity(int pop) {	
		iPopularity = pop;
    }

	@Override
	public int compareTo(PleaceObject another) {
		if(this.getDistance()==another.getDistance())
			return 0;
		else if(this.getDistance()<another.getDistance())
			return -1;
		else
			return 1;
	}
    
	/*
		
	this.getData = function () {			
		return JSON.stringify(objData)
    };
	
	this.cloneObject = function () {	

		var newObject = new PleaceObject(true);
		newObject.setData(this.getData());		
		return newObject;
		
    };
	
	this.setData = function (sText) {	
		
		try
		{
			objData = JSON.parse(sText);
		}catch(e)
		{
			
		}
    };	
	
		
		
	
	this.addQuestionAnswer = function (q,a) {			
		objData.aQuestAns[objData.aQuestAns.length] = new Array(q,a);
    };
	
	this.clearQuestionsAnswers = function (callback) {	
		objData.aQuestAns = new Array();
    };	
	
	this.enumQuestionsAnswers = function (callback) {	
	
		if(objData.aQuestAns.length)
			jQuery.each(objData.aQuestAns, function( key, val ) {				
				callback(val[0],val[1]);
			});
    };				
	
	this.addIcon = function (ico) {			
		objData.aIcons[objData.aIcons.length] = ico;
    };
	
	this.enumIcons = function (callback) {	
	
		if(objData.aIcons.length)
			jQuery.each(objData.aIcons, function( key, val ) {				
				callback(val);
			});
		
    };	*/
	
}
