package it.katalpa.licz_na_zilelen.model;
/**
*
* @coded by katalpa.it
*/
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.model.Marker;

public class PleaceObject implements Comparable<PleaceObject> {

	private String sName;
	private boolean iFavorite = false;
	private boolean iMyObject = false;
	private int iId = 0;
	private int iDataBaseId = 0;
	private double fDistance;
	private int iPopularity;
	private double dLatitude;
	private double dLongitude;
	Marker marker;
	public List<String> aIcons = null;
	public List<ComentsMap> aComments = null;
	
	 public class ComentsMap{	
		
		String key="";
		String value="";
		
		public ComentsMap(String _key,String _value)
		{
			key = _key;
			value = _value;	
		}
		
		public String getValue()
		{
			return value;
		}
		
		public String getKey()
		{
			return key;
		}
		
	}
	
	 public PleaceObject() {
		 aIcons = new ArrayList<String>();
	}

	public static PleaceObject createFromJSON(JSONObject jsonObject){
	        
	            
	        	PleaceObject obj = new PleaceObject() ;
	        	
	        	try
	        	{	        	
	        		obj.setName(jsonObject.getString("name"));
	        		//obj.setFavorite(jsonObject.getInt("favorite"));
	        		obj.setId(jsonObject.getInt("id"));
	        		obj.setPopularity(jsonObject.getInt("popularity"));
	        		
	        		
	        		String str = jsonObject.getString("position");
	        		
	        		str = str.replaceAll("POINT", "");
	        		str = str.replaceAll("\\(", "");
	        		str = str.replaceAll("\\)", "");
	        		
	        		String[] commatokens = str.trim().split(" ");
	        		
	        		obj.setLatitude(Double.parseDouble(commatokens[1]));
	        		obj.setLongitude(Double.parseDouble(commatokens[0]));	        		
	        			  
	        		obj.aIcons = new ArrayList<String>();
	        		
	        		JSONArray array = jsonObject.getJSONArray("icons");
	            	
	            	for(int i = 0 ; i < array.length() ; i++){
	            		obj.aIcons.add(array.getString(i));
	            	}
	        			        		
	            	obj.aComments = new ArrayList<ComentsMap>();
	            	
	            	 array = jsonObject.getJSONArray("comments");
		            	
	            	 
	            	for(int i = 0 ; i < array.length() ; i++){
	            			            		
	            		ComentsMap okl = obj.new ComentsMap(	            				            				
            					array.getJSONObject(i).getString("key"),
            					array.getJSONObject(i).getString("value")
            					);
	            		
	            		obj.aComments.add(okl);
	            	}
	            	
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
	
	public List<String> getIcons()
	{
		return aIcons;
	}
	
	public List<ComentsMap> getComments()
	{
		return aComments;
	}
	
	public String getName() {			
		return sName;
		//return "Park Cycadela";
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
	
	public boolean getFavorite(){			
		return iFavorite;			
    }
	
    public void setFavorite(boolean fav) {			
		iFavorite = fav;			
    }
    
	public boolean getMyObject(){			
		return false;			
    }
	
    public void setMyObject(boolean fav) {			
    	iMyObject = fav;			
    }
	
    public int getDataBaseId() {
    	return iDataBaseId;
    }
	
    public void setDataBaseId(int id) {			
    	iDataBaseId = id;			
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
