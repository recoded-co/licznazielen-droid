package it.katalpa.licz_na_zilelen.service;
/**
*
* @coded by katalpa.it
*/
import it.katalpa.licz_na_zilelen.helper.FavoriteDataSource;
import it.katalpa.licz_na_zilelen.model.PleaceObject;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import roboguice.util.Ln;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.inject.Singleton;

@Singleton
public class RealWebApiService implements WebApiService {

	@Override
	public List<PleaceObject> getNearObjects(String prefix,double latitude, double longitude) {

			
		final String url = "http://"+prefix+".licznazielen.pl/geocache/search/geo/?polygon={lo}";
		List<PleaceObject> list = new ArrayList<PleaceObject>();
		RestTemplate restTemplate = new RestTemplate();
    	restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    	
    	try {
    		    	
	    	String myObject = restTemplate.getForObject(url, String.class,"POINT ("+longitude+" "+latitude+")");
    			    	
	    	JSONObject jsonObject = new JSONObject(myObject);
	    	
    		 if(jsonObject.getBoolean("success"))
            {
            	JSONArray array = jsonObject.getJSONArray("objects");
            	
            	for(int i = 0 ; i < array.length() ; i++){
            	    list.add(PleaceObject.createFromJSON(array.getJSONObject(i)));
            	}
            	
            	
            }
    		
        } catch (Exception e) {
        	
        } 
		
		
		return list;
	}

	@Override
	public String getSettings(Context context,String key,String def) {
		
		SharedPreferences  shared_preferences = context.getSharedPreferences("shared_solar",Context.MODE_PRIVATE);		
		return shared_preferences.getString(key, def);
	}

	@Override
	public void setSettings(Context context,String key, String val) {
		SharedPreferences  shared_preferences = context.getSharedPreferences("shared_solar",Context.MODE_PRIVATE);		
		SharedPreferences.Editor shared_preferences_editor = shared_preferences.edit();
		shared_preferences_editor.putString(key,val);
	    shared_preferences_editor.commit();
	}

	@Override
	public List<PleaceObject> getFavoriteObject(Context context) {
		FavoriteDataSource oWblds = new FavoriteDataSource(context);
		oWblds.open();
		List<PleaceObject> oList = oWblds.getAllObjects();
		oWblds.close();
		return oList;
	}

	@Override
	public void deleteFavorite(Context context,PleaceObject fav) {
		FavoriteDataSource oWblds = new FavoriteDataSource(context);
		oWblds.open();
		oWblds.deleteFavorite(fav.getId());
		oWblds.close();
	}

	@Override
	public PleaceObject addFavoriteObject(Context context,PleaceObject fav) {
		FavoriteDataSource oWblds = new FavoriteDataSource(context);
		oWblds.open();
		PleaceObject oContact = oWblds.createFavorite(fav);
		oWblds.close();
		return oContact;
	}

	@Override
	public List<PleaceObject> getSearchObjects(String prefix,String sSearchText) {
		
		final String url = "http://"+prefix+".licznazielen.pl/geocache/search/namehint/"+sSearchText;
		
		List<PleaceObject> list = new ArrayList<PleaceObject>();
		RestTemplate restTemplate = new RestTemplate();
    	restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		//restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
    	
    	try {
    		    	
	    	String myObject = restTemplate.getForObject(url, String.class);
    		
    		JSONObject jsonObject = new JSONObject(myObject);
            
         
            if(jsonObject.getBoolean("success"))
            {
            	JSONArray array = jsonObject.getJSONArray("objects");
            	
            	for(int i = 0 ; i < array.length() ; i++){
            	    list.add(PleaceObject.createFromJSON(array.getJSONObject(i)));
            	}
            	
            	
            }
            
            
        } catch (Exception e) {
        	e.printStackTrace();        	
        } 
		
		
    	return list;
	}

	@Override
	public void sendComment(PleaceObject obj,String sAuthor, String sMessage) {
		final String url = "http://beta.licznazielen.pl/geocache/addComment/";


        final JSONObject jsonObject = new JSONObject();
       
        try {
        	 
            jsonObject.put("feature", obj.getId())
                    .put("name", sAuthor)
                    .put("comment",sMessage);

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);

            final HttpEntity<String> httpEntity = new HttpEntity<String>(jsonObject.toString(), requestHeaders);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            final JSONObject responseJSONObject = new JSONObject(response.getBody());
            Ln.d("Otrzymano odpowiedü: %s", responseJSONObject.toString());



        } catch (JSONException e) { 
        	 //throw new InternalException("Bad json.", e);
        } catch (RestClientException e) {
        	// throw new InternalException("Error server", e);
        } catch (IllegalArgumentException e) {
        	// throw new InternalException("Invalid argument exception.", e);
        }

	}

	@Override
	public boolean addPleace(String prefix,Context context, PleaceObject fav) {
				
		
		final String url = "http://"+prefix+".licznazielen.pl/geocache/addPoint/";

		final JSONObject jsonObject = new JSONObject();
       
        try {
        	
        	JSONArray array = new JSONArray();
        	array.put(new JSONObject().put("name", "1").put("value", fav.getName()));
        	
        	for(String s: fav.getIcons())
		    {
		    	array.put(new JSONObject().put("name", "2").put("value", s));
		    }        	
        	
        	array.put(new JSONObject().put("name", "3").put("value", "dwa"));
        	
            jsonObject.put("group", "mobilki")
                    .put("name", "wersja-1")
                    .put("user","13242423")
                    .put("lat", fav.getLatitude())
                    .put("lon", fav.getLongitude())
                    .put("crs", "WGS84")
                    .put("form_values", array);

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);

            final HttpEntity<String> httpEntity = new HttpEntity<String>(jsonObject.toString(), requestHeaders);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            final JSONObject responseJSONObject = new JSONObject(response.getBody());
           
            return true;
            
        } catch (JSONException e) { 
        	//throw new InternalException("Bad json.", e);
        } catch (RestClientException e) {
        	 // throw new InternalException("Error server", e);
        } catch (IllegalArgumentException e) {
        	// throw new InternalException("Invalid argument exception.", e);
        }

		
		return false;
	}

	@Override
	public PleaceObject getFavoriteObjectById(Context context, int id) {
		FavoriteDataSource oWblds = new FavoriteDataSource(context);
		oWblds.open();
		PleaceObject oContact = oWblds.getFavoriteByObjId(id);
		oWblds.close();
		return oContact;
	}

	@Override
	public String getPrefix(double latitude,double longitude) {		
		
		final String url = "http://www.licznazielen.pl/getprefix/?point={lo}";

		String list = "";
		
		RestTemplate restTemplate = new RestTemplate();
    	restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    	
    	try {
    		    	
	    	String myObject = restTemplate.getForObject(url, String.class,"POINT ("+longitude+" "+latitude+")");
    		
	    	JSONObject jsonObject = new JSONObject(myObject);
	    	
    		 if(jsonObject.getBoolean("success"))
	            {
	    			 list = jsonObject.getString("prefix");
	            }
    		
        } catch (Exception e) {
        	
        }
		
		
		return list;
	}

	
}
