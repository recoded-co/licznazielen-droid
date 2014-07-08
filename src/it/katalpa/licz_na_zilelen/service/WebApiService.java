package it.katalpa.licz_na_zilelen.service;
/**
*
* @coded by katalpa.it
*/
import it.katalpa.licz_na_zilelen.model.PleaceObject;
import java.util.List;

import android.content.Context;

public interface WebApiService {
	public List<PleaceObject> getNearObjects(String prefix,double latitude,double longitude);
	public String getSettings(Context context,String key,String def);
	public void setSettings(Context context,String key,String val);
	public List<PleaceObject> getFavoriteObject(Context context);
	public PleaceObject addFavoriteObject(Context context,PleaceObject fav);
	public List<PleaceObject> getSearchObjects(String prefix,String name);
	public void sendComment(PleaceObject obj,String sAuthor,String sMessage);
	public void deleteFavorite(Context context, PleaceObject fav);	
	public boolean addPleace(String prefix,Context context, PleaceObject fav);
	public PleaceObject getFavoriteObjectById(Context context,int id );	
	/*public String getPrefix(double latitude, double longitude);*/	
	public double[] getRegion(String prefix);
	public String getPrefixByPosition(double lat,double lon);
}
