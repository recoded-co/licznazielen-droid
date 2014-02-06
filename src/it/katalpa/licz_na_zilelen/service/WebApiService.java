package it.katalpa.licz_na_zilelen.service;

import it.katalpa.licz_na_zilelen.model.PleaceObject;
import java.util.List;

public interface WebApiService {
	public List<PleaceObject> getNearObjects(double latitude,double longitude);
	public String getSettings(String val);
	public void setSettings(String key,String val);
	public List<PleaceObject> getFavoriteObject();
	public void setFavoriteObject(PleaceObject fav);
	public List<PleaceObject> getSearchObjects(String name);
	public void sendComment(String sAuthor,String sMessage);	
}
