package it.katalpa.licz_na_zilelen.service;
/**
*
* @coded by katalpa.it
*/
import it.katalpa.licz_na_zilelen.helper.FavoriteDataSource;
import it.katalpa.licz_na_zilelen.model.PleaceObject;
import it.katalpa.licz_na_zilelen.model.PleaceObject.ComentsMap;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.inject.Singleton;

@Singleton
public class RealWebApiService implements WebApiService {

	@Override
	public String getPrefixByPosition(double lat,double lon)
	{
		
		double x1 = 15.6011163;
		double x2 = 18.2948956;
		double x3 = 20.0784373;
		double x4 = 22.2100081;
		double x5 = 19.2992967;
		double x6 = 20.4326486;
		double y1 = 53.4758863;
		double y2 = 50.8093207;
		double y3 = 49.6864208;
		
		if (lat < y3 || lat > y1)
            return "";
        if (lat > y2){
            if (lon < x1 || lon > x4)
                return "";
            if (lon < x2)
                return "beta";
            if (lon < x3)
                return "lodz";
            if (lon < x4)
                return "warszawa";
        }else{
            if (lon < x6 || lon > x5)
                return "krakow";
            else
                return null;
        }
        return null;
	}
	
	@Override
	public List<PleaceObject> getNearObjects(String prefix,double latitude, double longitude) {

		prefix = getPrefixByPosition(latitude,longitude);
				
		if(prefix==null)
			return null;
		
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
		if(fav.getId()!=0)
			oWblds.deleteFavoriteByObjId(fav.getId());
		else
			oWblds.deleteFavorite(fav.getDataBaseId());
		fav.setDataBaseId(0);
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
            Ln.d("Otrzymano odpowied�: %s", responseJSONObject.toString());



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
        	/*
        	JSONArray array = new JSONArray();
        	array.put(new JSONObject().put("name", "1").put("value", fav.getName()));
        	
        	for(String s: fav.getIcons())
		    {
		    	array.put(new JSONObject().put("name", "2").put("value", s));
		    }        	
        	
        	//array.put(new JSONObject().put("name", "3").put("value", "dwa"));
        	
        	if(fav.getComments()!=null)
     	        for(ComentsMap nm : fav.getComments())
     	        {
     	        	array.put(new JSONObject().put("name", "3").put("value", nm.getValue()));
     	        }
			*/
        	
        	TelephonyManager  tm=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        	
        	
        	JSONArray array = new JSONArray();
        	if (fav.getName() != null && !fav.getName().equals("")){
        		array.put(new JSONObject().put("name", "<b>Nazwa-miejsca-(jeżli-dotyczy)-</b></br>").put("value", fav.getName()));
        	}
        	
        	HashMap<String, String> iconsToValues = new HashMap<String, String>();
        	iconsToValues.put("1", "relaks-i-odpoczynek");
        	iconsToValues.put("2", "aktywny-wypoczynek");
        	iconsToValues.put("3", "spotkania-ze-znajomymi-i-rodzina");
        	iconsToValues.put("4", "obserwowanie-przyrody");
        	iconsToValues.put("5", "w-jaki-sposob-spedza-pani-czas-w-tym-miejscu");
        	
        	for(String s: fav.getIcons())
		    {	
        		String value = iconsToValues.get(s);
        		array.put(new JSONObject().put("name", "w-jaki-sposob-spedza-pani-czas-w-tym-miejscu").put("value", value));
		    }        	
        	
        	if(fav.getComments()!=null)
     	        for(ComentsMap nm : fav.getComments())
     	        {
     	        	if (!nm.getValue().equals("")){
     	        		array.put(new JSONObject().put("name", "<b>Jakie-cechy-tego-miejsca-sprawiają,-że-chętnie-spędza-Pan(i)-w-nim-czas?-</b>").put("value", nm.getValue()));
     	        	}
     	        }
            jsonObject.put("group", "Q-mapa-1")
                    .put("name", "miejsca-spedzania-czasu-w-otoczeniu-zieleni")
                    .put("popup_id", "miejsca-spedzania-czasu-w-otoczeniu-zieleni-60")
                    .put("mobile", "True")
                    .put("user",tm.getDeviceId())
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
            
            //Log.v("duda","server: "+responseJSONObject.toString());
            
            return true;
            
        } catch (JSONException e) { 
        	//Log.v("duda","JSONException: "+e.getMessage());
        	//throw new InternalException("Bad json.", e);
        } catch (RestClientException e) {
        //	e.printStackTrace();
        	//Log.v("duda","RestClientException: "+e.getMessage());
        	 // throw new InternalException("Error server", e);
        } catch (IllegalArgumentException e) {
        //	Log.v("duda","IllegalArgumentException: "+e.getMessage());
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

	@Override
	public double[] getRegion(String prefix) {
		double[] reg= new double[4];
		
		if(prefix.equals("beta"))
		{
			reg[0]=52.20529295087437;
			reg[1]=52.621027546435755;
			reg[2]=17.252853848040104;
			reg[3]=16.78489051759243;
		}
		
		if(prefix.equals("lodz"))
    	{
			reg[0]=51.67198689093647;
			reg[1]=51.838442515129685;
			reg[2]=19.5498376339674;
			reg[3]=19.36521414667368;	
    	}
		
		if(prefix.equals("warszawa"))
    	{
			reg[0]=52.00495333617714;
			reg[1]=52.4020592632483;
			reg[2]=21.23726561665535;
			reg[3]=20.792383700609207;
    	}
		
		if(prefix.equals("krakow"))
    	{
			reg[0]=49.97333361884823;
			reg[1]=50.14771277578873;
			reg[2]=20.045531652867794;
			reg[3]=19.859035313129425;
    	}
		
		
		return reg;
	}

	
}
