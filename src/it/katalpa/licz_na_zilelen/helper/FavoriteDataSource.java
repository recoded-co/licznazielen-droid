package it.katalpa.licz_na_zilelen.helper;

import it.katalpa.licz_na_zilelen.model.PleaceObject;

import java.util.ArrayList;
import java.util.List;

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
			  DatabaseHelper.COLUMN_FAV_POPULARITY
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
		
		if(ctn!=null)
			return null;
		  
		ContentValues values = new ContentValues();
	    values.put(DatabaseHelper.COLUMN_FAV_NAME, obj.getName());
	    values.put(DatabaseHelper.COLUMN_FAV_OBJID, obj.getId());
	    values.put(DatabaseHelper.COLUMN_FAV_LATITUDE,obj.getLatitude());
	    values.put(DatabaseHelper.COLUMN_FAV_LONGITUDE,obj.getLongitude());
	    values.put(DatabaseHelper.COLUMN_FAV_POPULARITY,obj.getPopularity());
	    		
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

	  public void deleteFavorite(int id) {
	   
	    database.delete(DatabaseHelper.TABLE_NAME_FAV,DatabaseHelper.COLUMN_FAV_OBJID
	        + " = " + id, null);
	  }

	  public PleaceObject getFavoriteByObjId(int iId) {
		 
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
	      objs.add(cursorToPleaceObject(cursor));
	      cursor.moveToNext();
	    }
	   
	    cursor.close();
	    return objs;
	  }

	  private PleaceObject cursorToPleaceObject(Cursor cursor) {
		  PleaceObject obj = new PleaceObject();
		  obj.setId(cursor.getInt(1));
		  obj.setName(cursor.getString(2));
		  obj.setLatitude(cursor.getDouble(3));
		  obj.setLongitude(cursor.getDouble(4));
		  obj.setPopularity(cursor.getInt(5));
		  return obj;
	  }
}
