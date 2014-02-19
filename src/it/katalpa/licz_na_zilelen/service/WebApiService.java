package it.katalpa.licz_na_zilelen.service;

import it.katalpa.licz_na_zilelen.model.PleaceObject;
import java.util.List;

import android.content.Context;

public interface WebApiService {
	public List<PleaceObject> getNearObjects(double latitude,double longitude);
	public String getSettings(Context context,String key,String def);
	public void setSettings(Context context,String key,String val);
	public List<PleaceObject> getFavoriteObject(Context context);
	public PleaceObject addFavoriteObject(Context context,PleaceObject fav);
	public List<PleaceObject> getSearchObjects(String name);
	public void sendComment(PleaceObject obj,String sAuthor,String sMessage);
	public void deleteFavorite(Context context, PleaceObject fav);	
	public boolean addPleace(Context context, PleaceObject fav);	
}
