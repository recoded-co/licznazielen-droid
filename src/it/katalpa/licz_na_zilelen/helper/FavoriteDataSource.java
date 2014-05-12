package it.katalpa.licz_na_zilelen.helper;
/**
*
* @coded by katalpa.it
*/
import it.katalpa.licz_na_zilelen.model.PleaceObject;
import it.katalpa.licz_na_zilelen.model.PleaceObject.ComentsMap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FavoriteDataSource {
	 // Database fields
	  private SQLiteDatabase database;
	  private DatabaseHelper dbHelper;
	  private String[] allColumns = { 
			  DatabaseHelper.KEY_ID,
			  DatabaseHelper.COLUMN_FAV_OBJID,
			  DatabaseHelper.COLUMN_FAV_NAME,
			  DatabaseHelper.COLUMN_FAV_LATITUDE,
			  DatabaseHelper.COLUMN_FAV_LONGITUDE,
			  DatabaseHelper.COLUMN_FAV_POPULARITY,
			  DatabaseHelper.COLUMN_FAV_MYOBJECT,
			  DatabaseHelper.COLUMN_FAV_ICONS,
			  DatabaseHelper.COLUMN_FAV_ANSWRWS
	  };

	  public FavoriteDataSource(Context context) {
	    dbHelper = new DatabaseHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public PleaceObject createFavorite(PleaceObject obj) {
		  
		PleaceObject ctn = getFavoriteByObjId(obj.getId());
		
		if(ctn!=null || obj.getDataBaseId()!=0)
			return null;
		  
		ContentValues values = new ContentValues();
	    values.put(DatabaseHelper.COLUMN_FAV_NAME, obj.getName());
	    values.put(DatabaseHelper.COLUMN_FAV_OBJID, obj.getId());
	    values.put(DatabaseHelper.COLUMN_FAV_LATITUDE,obj.getLatitude());
	    values.put(DatabaseHelper.COLUMN_FAV_LONGITUDE,obj.getLongitude());
	    values.put(DatabaseHelper.COLUMN_FAV_POPULARITY,obj.getPopularity());  	
	    values.put(DatabaseHelper.COLUMN_FAV_MYOBJECT,obj.getMyObject());
	    	    
	    String sText = "0";
	    
	    for(String s: obj.getIcons())
	    {
	    	sText +='|'+s;
	    }
	    
	    values.put(DatabaseHelper.COLUMN_FAV_ICONS,sText);
	    
	    
	    JSONObject jsonObject = new JSONObject();	      
    	JSONArray array = new JSONArray();
    	
    	try
    	{    	
	        if(obj.getComments()!=null)
		        for(ComentsMap nm : obj.getComments())
		        {
		        	array.put(new JSONObject().put("name",nm.getKey()).put("value",nm.getValue()));
		        }    	
	    	
	    	jsonObject.put("form_values", array);
    	
    	}catch(Exception e){
    		
    	}
        
    	values.put(DatabaseHelper.COLUMN_FAV_ANSWRWS,jsonObject.toString());
	        	   	
	    long insertId = database.insert(DatabaseHelper.TABLE_NAME_FAV, null,
	        values);
	    
	    Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_FAV,
	        allColumns, DatabaseHelper.KEY_ID + " = " + insertId, null,
	        null, null, null);
	    
	    cursor.moveToFirst();
	    PleaceObject newComment = cursorToPleaceObject(cursor);
	    cursor.close();
	    
	    return newComment;
	  }

	  public void deleteFavoriteByObjId(int id) {
	   
	    database.delete(DatabaseHelper.TABLE_NAME_FAV,DatabaseHelper.COLUMN_FAV_OBJID
	        + " = " + id, null);
	  }

	  public void deleteFavorite(int id) {
		   
		    database.delete(DatabaseHelper.TABLE_NAME_FAV,DatabaseHelper.KEY_ID
		        + " = " + id, null);
		  }
	  
	  public PleaceObject getFavoriteByObjId(int iId) {
		 
		  if(iId==0)
			  return null;
		  
		  Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_FAV, allColumns
		    		,DatabaseHelper.COLUMN_FAV_OBJID + "=?", new String[] { ""+iId }, null, null, null, null); 
		  
		  PleaceObject contact = null;
		    if (cursor.moveToFirst()) {
		        contact = cursorToPleaceObject(cursor);
		    }
		    cursor.close();
		   
		    return contact;
	  } 
	  
	  public List<PleaceObject> getAllObjects() {
	    List<PleaceObject> objs = new ArrayList<PleaceObject>();

	    Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_FAV,
	        allColumns,null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	
	      PleaceObject pleace = cursorToPleaceObject(cursor);
	      pleace.setFavorite(true);
	      objs.add(pleace);
	      cursor.moveToNext();
	    }
	   
	    cursor.close();
	    return objs;
	  }

	  private PleaceObject cursorToPleaceObject(Cursor cursor) {
		  PleaceObject obj = new PleaceObject();
		  obj.setDataBaseId(cursor.getInt(0));
		  obj.setId(cursor.getInt(1));
		  obj.setName(cursor.getString(2));
		  obj.setLatitude(cursor.getDouble(3));
		  obj.setLongitude(cursor.getDouble(4));
		  obj.setPopularity(cursor.getInt(5));
		  obj.setMyObject(cursor.getInt(6)==1);
		  obj.setFavorite(true);
		  
		  String sText = cursor.getString(7);
		  
		  String[] aText = sText.split("\\|");
		  
		  for(int i = 0 ; i < aText.length ; i++){
      		obj.aIcons.add(aText[i]);
      	  }
		  
		  obj.aComments = new ArrayList<ComentsMap>();
		  
		  try
		  {
		  
			  	JSONObject jsonObject = new JSONObject(cursor.getString(8));
			  	JSONArray array = jsonObject.getJSONArray("form_values");
			  	
			  	for(int i = 0 ; i < array.length() ; i++){
            		
            		ComentsMap okl = obj.new ComentsMap(	            				            				
        					array.getJSONObject(i).getString("name"),
        					array.getJSONObject(i).getString("value")
        					);
            		
            		obj.aComments.add(okl);
            	}
			  	
		  }catch(Exception e){}
		  
		  return obj;
	  }
}
