package it.katalpa.licz_na_zilelen.service;

import it.katalpa.licz_na_zilelen.model.PleaceObject;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

import com.google.inject.Singleton;

@Singleton
public class RealWebApiService implements WebApiService {

	@Override
	public List<PleaceObject> getNearObjects(double latitude, double longitude) {

		Log.v("duda","begin: ok");
		
		
		final String url = "http://beta.licznazielen.pl/geocache/search/geo/?polygon={lo}";
		List<PleaceObject> list = new ArrayList<PleaceObject>();
		RestTemplate restTemplate = new RestTemplate();
    	restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    	
    	try {
    		    	
	    	String myObject = restTemplate.getForObject(url, String.class,"POINT ("+longitude+" "+latitude+")");
    			    	
	    	JSONObject jsonObject = new JSONObject(myObject);
	    	
    		Log.v("duda","res: "+jsonObject);
    		
            if(jsonObject.getBoolean("success"))
            {
            	JSONArray array = jsonObject.getJSONArray("objects");
            	
            	for(int i = 0 ; i < array.length() ; i++){
            	    list.add(PleaceObject.createFromJSON(array.getJSONObject(i)));
            	}
            	
            	
            }
    		
           // Log.v("duda","Otrzymano odpowiedü: "+);

        } catch (Exception e) {
        	Log.v("duda","getNearObjectsError: "+e.getMessage());
        } 
		
		
		return list;
	}

	@Override
	public String getSettings(String val) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSettings(String key, String val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<PleaceObject> getFavoriteObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFavoriteObject(PleaceObject fav) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<PleaceObject> getSearchObjects(String sSearchText) {
		Log.v("duda","begin: ok");		
		
		final String url = "http://beta.licznazielen.pl/geocache/search/namehint/"+sSearchText;
		
		
		RestTemplate restTemplate = new RestTemplate();
    	restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		//restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
    	
    	try {
    		    	
	    	String myObject = restTemplate.getForObject(url, String.class);
    		
    		JSONObject jsonObject = new JSONObject(myObject);
            
            Log.v("duda","Otrzymano odpowiedü: "+jsonObject.toString());

        } catch (Exception e) {
        	e.printStackTrace();
        	//Log.v("duda","error:"+e.getMessage());
        } 
		
		
/*

        try {           
           
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);

            final HttpEntity<String> httpEntity = new HttpEntity<String>(null, requestHeaders);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            final JSONObject responseJSONObject = new JSONObject(response.getBody());
            Log.v("duda","Otrzymano odpowiedü: "+responseJSONObject.toString());

        } catch (Exception e) {
        	//Log.v("duda","error: ok"+e.getMessage());
        } 
		*/
		
		/*
    	try {
    		    
    		String url = "'http://beta.licznazielen.pl/geocache/search/namehint/"+password+"/"+username;   	
        	RestTemplate restTemplate = new RestTemplate();
        	restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    		//restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        	
    		
    		
	    	ResponseEntity<String> myObject = restTemplate.getForEntity(url, String.class);
    		
    		JSONObject jsonObject = new JSONObject(myObject.getBody().toString());
			
			if(jsonObject.has("error_code"))
            {
            	//throw new AuthenticationException("Authentication");
            }	    	    	
	    	
	    	//return User.createFromJSON(jsonObject);
	    	
    	} catch (RestClientException e) {
		//	throw new InternalException("Error server");
		//} catch (UserCreationException e) {
			//throw new InternalException("Error server");
		} catch (JSONException e) {
			//throw new InternalException("Error server");
		}catch (Exception e) {
			//throw new InternalException("Error server");
		}*/
		
		return null;
	}

	@Override
	public void sendComment(String sAuthor, String sMessage) {
		// TODO Auto-generated method stub
		
	}

	

	
}
