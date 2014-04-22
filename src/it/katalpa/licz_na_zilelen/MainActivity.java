package it.katalpa.licz_na_zilelen;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.inject.Inject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.NoTitle;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import it.katalpa.licz_na_zilelen.R;
import it.katalpa.licz_na_zilelen.helper.FavPleaceObjectListAdapter;
import it.katalpa.licz_na_zilelen.helper.NearPleaceObjectListAdapter;
import it.katalpa.licz_na_zilelen.helper.SearchPleaceObjectListAdapter;
import it.katalpa.licz_na_zilelen.model.PleaceObject;
import it.katalpa.licz_na_zilelen.model.PleaceObject.ComentsMap;
import it.katalpa.licz_na_zilelen.service.WebApiService;

/**
*
* @coded by katalpa.it
*/


@NoTitle
@EActivity(R.layout.activity_main)
@RoboGuice
@OptionsMenu(R.menu.main)
public class MainActivity extends FragmentActivity implements OnMarkerClickListener {

	private GoogleMap map; 
	boolean isSatView = false;
	boolean isFavView = false;
	boolean hasError = false;
	LatLng myPosition = null;
	String apiPrefix = "beta";
	
	
	List<PleaceObject> nearObjects =  null;
	List<PleaceObject> favObjects =  null;
	List<PleaceObject> searchObjects =  null;
	
	private static final int DELETE_ID = Menu.FIRST + 4;
	FavPleaceObjectListAdapter favAdapter;
	SearchPleaceObjectListAdapter searchAdapter;
	 
	@ViewById
	TextView headerText;
	
	@ViewById
	ImageButton buttonSearch;
	
	@ViewById
	Button nearButton;
	
	@ViewById
	Button addButton;
	
	@Inject
	WebApiService webApi;	
	
	int markersColor[]={
			R.drawable.marker_lokalizacji_ulubionej_powiekszony,
			R.drawable.marker_lokalizacji,
			R.drawable.marker_lokalizacji_ulubionej,
			R.drawable.m4,
			R.drawable.m5,
			R.drawable.m6,
			R.drawable.m7,
			R.drawable.m8,
			R.drawable.m9,
			R.drawable.m10
	};
	
	@AfterViews
    void initApp() {	
		
		Intent it = this.getIntent();
			
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	    map.setMyLocationEnabled(true);
		
	    map.setOnMarkerClickListener(this);
	    
	    map.getUiSettings().setCompassEnabled(true);
	    map.getUiSettings().setZoomControlsEnabled(false);
	    
	    hasError = it.hasExtra("hasError");
	    apiPrefix = it.getStringExtra("prefix");
	    
	    myPosition = new LatLng(
	    			it.getDoubleExtra("Latitude",Double.parseDouble(webApi.getSettings(getApplicationContext(), "latitude","16.5603"))), 
	    			it.getDoubleExtra("Longitude",Double.parseDouble(webApi.getSettings(getApplicationContext(), "longitude","52.2430")))
	    		);
	    
	    CameraUpdate center = CameraUpdateFactory.newLatLng(myPosition);
	    CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        map.moveCamera(center);
        map.animateCamera(zoom);
        
        
        isSatView = Boolean.parseBoolean(webApi.getSettings(getApplicationContext(), "satelite", "false"));
        isFavView = Boolean.parseBoolean(webApi.getSettings(getApplicationContext(), "fav", "false"));
        
        if(isSatView)
        	map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        
        
        //if(isFavView)
        	ShowFavoriteOnMap(true);
        
        
        if(apiPrefix.isEmpty())
        {
        	buttonSearch.setEnabled(false);
    		nearButton.setEnabled(false);
    		addButton.setEnabled(false);
    		
    		for (int i=0; i < 2; i++)
    			ShowFlashMessage(0,"Twoj obszar nie jest obs³ugiwany przez aplikacje");
        }
        
          /*
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //performSearch();
                    return true;
                }
                return false;
            }
        });
        
        editSearch.setEnabled(false);
        
        */
	}
	
	private void ShowFavoriteOnMap(boolean isShow)
	{
		
		if(isShow)
		{			
			favObjects = webApi.getFavoriteObject(getApplicationContext());
			for (PleaceObject obs : favObjects) {
			      addMarkerToMap(obs,2);			      
			}			
		}else{		
			for (PleaceObject obs : favObjects) {
		       obs.getMarker().remove();
		    }
			favObjects = null;
		}
		
	}
	
	private double calcDistanceFromMyPosition(LatLng marker)
	{
		 float[] result = new float[1];
		 Location.distanceBetween (myPosition.latitude,myPosition.longitude,marker.latitude, marker.longitude,  result);
		 return (double)Math.abs(Math.round(result[0])/10)/100;
		
	}

	
	PleaceObject findPleaceObjectByMarker(Marker marker){    
		
		if(nearObjects!= null)
		{
			for (PleaceObject obs : nearObjects) {
				if (marker.equals(obs.getMarker())) {
		            return obs;
		        }
		    }
		}
		
		if(favObjects!= null)
		    for (PleaceObject obs : favObjects) {
		        if (marker.equals(obs.getMarker())) {
		            return obs;
		        }
		    }
		
		
	    return null; 
	}
	
	
	public boolean IsInFavorite(int iObjId)
	{
		if(favObjects!= null)
		    for (PleaceObject obs : favObjects) {
		        if (iObjId==obs.getId()) {
		            return true;
		        }
		    }
		return false;
	}
	
	@UiThread
	 public void ShowFlashMessage(int type,String sMessage)
	{
		Toast.makeText(getApplicationContext(), sMessage, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onMarkerClick(final Marker marker) {
	    	
		PleaceObject obj = findPleaceObjectByMarker(marker);
		
		if(obj!=null)
		{
			ShowObjectDialog(obj);
			return true;
		}
		
	    return false;
	}

	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if (keyCode == KeyEvent.KEYCODE_BACK) {
	    		    	 	    	 
	    	 new AlertDialog.Builder(this)
	         .setIcon(android.R.drawable.ic_dialog_alert)
	         .setTitle(R.string.signin_quit)
	         .setMessage(R.string.signin_really_quit)
	         .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

	             @Override
	             public void onClick(DialogInterface dialog, int which) {

	            	 MainActivity.this.finish();
	            	 
	             }

	         })
	         .setNegativeButton(R.string.no, null)
	         .show();

	         return true;
	     }else  if (keyCode == KeyEvent.KEYCODE_MENU){
	    	 showMenu();
	    	 return true;
	     }
	     
	     return super.onKeyDown(keyCode, event);
	 }
	
	
	@UiThread
	 public void ClearMarkers()
	{
		for (PleaceObject obs : nearObjects) {
	       obs.getMarker().remove();
	    }
		nearObjects = null;
	}	
	
	
	@UiThread
	 public void addMarkerToMap(PleaceObject po,int i)
	 {
		 
		 
		 LatLng position = new LatLng(po.getLatitude(), po.getLongitude());
		 
		 Marker oMarker = map.addMarker(new MarkerOptions()
	            .position(position)
	            .title(po.getName())
	            .alpha(1.0f)
	            .flat(true)
	            .icon(BitmapDescriptorFactory.fromResource(markersColor[i]))	            
            );
		 
		 
		 po.setMarker(oMarker);
		 
		 if(!hasError)
			 po.setDistance(calcDistanceFromMyPosition(position));
		 
	 }
	
	@UiThread
	 public void refreshMap(int iCount)
	 {
		
		List<PleaceObject> copy = new ArrayList<PleaceObject>(nearObjects); //shallow copy
		
		Collections.sort(copy,new Comparator<PleaceObject>()
				{

					@Override
					public int compare(PleaceObject lhs, PleaceObject rhs) {
						
						return lhs.compareTo(rhs);
					}
			
				});
		
		
		LatLngBounds.Builder bounds = new LatLngBounds.Builder();
		
		for(int i=0;i<iCount;i++)
		{
			bounds.include(new LatLng(copy.get(i).getLatitude(), copy.get(i).getLongitude()));
		}
		
		map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
				
	 }
	
	void ShowObjectDialog(final PleaceObject obj)
	{
		
		VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();

		double delta = visibleRegion.latLngBounds.northeast.latitude-visibleRegion.latLngBounds.southwest.latitude;
		delta = Math.abs(delta)/4;
		
		
		LatLng position = new LatLng(
    			obj.getLatitude()-delta, 
    			obj.getLongitude()
    		);
    
		CameraUpdate center = CameraUpdateFactory.newLatLng(position);

		map.moveCamera(center);
		
		headerText.setText(obj.getName());
		
		final Dialog mObjectDialog = new Dialog(this,R.style.AboutTheme);
        final RelativeLayout mObjectDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.objectdialog, null);

        final ImageButton favButton = ((ImageButton) mObjectDialogView.findViewById(R.id.buttonAddFav));
        
        if(webApi.getFavoriteObjectById(getApplicationContext(), obj.getId())!=null)
        {
        	favButton.setImageResource(R.drawable.heart_ico_active);
        }else{
        	favButton.setImageResource(R.drawable.heart_ico);
        }
        
        favButton.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 
            	 if(webApi.addFavoriteObject(getApplicationContext(), obj)!=null)
            	 {
            		 favButton.setImageResource(R.drawable.heart_ico_active);
            		 ShowFlashMessage(0, "Obiekt zosta³ dodany do Twojej listy ulubionych miejsc");
            	 }else{
            		 favButton.setImageResource(R.drawable.heart_ico);
            		 webApi.deleteFavorite(getApplicationContext(), obj);
            		 ShowFlashMessage(0, "Obiekt zosta³ usuniêty z listy Twoich ulubionych miejsc");
            	 }  
             }
         });
         
         

         ((ImageButton) mObjectDialogView.findViewById(R.id.buttonShere)).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
			         Intent intent = new Intent(Intent.ACTION_SEND);
			 		intent.setType("text/plain");
			 		intent.putExtra(Intent.EXTRA_TEXT, "http://licznazielen.pl");
			 		startActivity(Intent.createChooser(intent, "Udostêpnij"));
             }
         });
         
         mObjectDialog.setOnKeyListener(new OnKeyListener(){

 			@Override
 			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
 				if(keyCode == KeyEvent.KEYCODE_BACK)
 			    {
 					headerText.setText(""); 			        
 			    }
 			    return false;
 			}			
 			
 		});
         

        TextView lvNearObjects = (TextView) mObjectDialogView.findViewById(R.id.objtextView);
        lvNearObjects.setText(obj.getName());
            
        lvNearObjects = (TextView) mObjectDialogView.findViewById(R.id.textViewDistance);
        lvNearObjects.setText(obj.getDistance()+" km");
        
        
        lvNearObjects = (TextView) mObjectDialogView.findViewById(R.id.aboutText);
        
        String sText = "";
        
        if(obj.getComments()!=null)
	        for(ComentsMap nm : obj.getComments())
	        {
	        	sText+= "<b>"+nm.getKey() + "</b><br>- " + nm.getValue()+"<br><br>";
	        }
        
               
        lvNearObjects.setText(Html.fromHtml(sText));
        
        mObjectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mObjectDialog.setContentView(mObjectDialogView);
        
        mObjectDialog.show();
	}
	
	@UiThread
	void ShowNearDialog(final List<PleaceObject> aObjectList)
	{

		final Dialog mNearDialog = new Dialog(this,R.style.AboutTheme);
        final RelativeLayout mNearDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.neardialog, null);
               

        final ListView lvNearObjects = (ListView) mNearDialogView.findViewById(R.id.nearlistView);
         
        
        
        lvNearObjects.setClickable(true);
        lvNearObjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

            ShowObjectDialog((PleaceObject) lvNearObjects.getItemAtPosition(position));
            //mNearDialog.cancel();
            
          }
        });        
        
        NearPleaceObjectListAdapter adapter = new NearPleaceObjectListAdapter(this,R.layout.list_item_near,aObjectList);
 	    
 	    adapter.sort(new Comparator<PleaceObject>() {
 	    	public int compare(PleaceObject object1, PleaceObject object2) {
 	    		return (-1)*(object1.getName().compareTo(object2.getName()));
 	    	};
 	    });
 	    
 	   lvNearObjects.setAdapter(adapter);	
         
       mNearDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       mNearDialog.setContentView(mNearDialogView);
        
       mNearDialog.show();
	}
	
	
	@UiThread
	void ShowNotFoundDialog()
	{
		final Dialog mAboutDialog = new Dialog(MainActivity.this,R.style.AboutTheme);

        final RelativeLayout mmAboutDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.dialog_confrim, null);

        ((Button) mmAboutDialogView.findViewById(R.id.dlgOkButton)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	mAboutDialog.cancel();
                }
         });

         
         mAboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         mAboutDialog.setContentView(mmAboutDialogView);
        
         mAboutDialog.show();
	}
	
	@Background
	void getNearObjects(LatLng ll,LatLngBounds nowBounds)
	{
		//webApi.getSearchObjects("non");
		ClearMarkers();
		
		nearObjects =  webApi.getNearObjects(apiPrefix,ll.latitude,ll.longitude);
		
		if(nearObjects.size()==0)
		{
			ShowNotFoundDialog();
			//ShowFlashMessage(0,"W Twoim otoczeniu nie ma ¿adnych miejsc. Miasta aktualnie obs³ugiwane przez aplikacjê to Poznañ, £ódŸ, Kraków i Warszawa");
		}else{
		
			int iCounter = 0;
			
			for(int i=0;i<nearObjects.size();i++)
			{			
				PleaceObject po = nearObjects.get(i);
				addMarkerToMap(po,1);			
				
				iCounter += (nowBounds.contains(new LatLng(po.getLatitude(), po.getLongitude()))?1:0);
			}
			
			if(iCounter<5)
				refreshMap(5);
			ShowNearDialog(nearObjects);
		}
	}
	
	@Background
	void addObject(PleaceObject obj)
	{
		if(webApi.addPleace(apiPrefix,getApplicationContext(), obj))
		{			
			obj.setMyObject(true);
			webApi.addFavoriteObject(getApplicationContext(), obj);
       	 	favObjects.add(obj);
       	 	addMarkerToMap(obj,2);			
			ShowFlashMessage(0, "Lokalizacja zosta³a dodana do mapy. Dziêkujemy");
		}else{
			ShowFlashMessage(0, "Nie mo¿na dodaæ miejsca, spróbuj ponownie za chwilê.");
		}
			//ShowFlashMessage(0, "Miejsce juz dodane");
	}
	/*
	@Click(R.id.buttonShare)
	 void share()
	 {
		
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "http://licznazielen.pl");
		startActivity(Intent.createChooser(intent, "Udostêpnij"));
	}*/
	
	@Click(R.id.addButton)
	 void addObject()
	 {			
		/*PleaceObject obj = new PleaceObject();
		obj.setLatitude(map.getCameraPosition().target.latitude);
		obj.setLongitude(map.getCameraPosition().target.longitude);	
		addObject(obj);	*/
		
		
		CameraUpdate center = CameraUpdateFactory.newLatLng(myPosition);
	    CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

       // map.moveCamera(center);
       // map.animateCamera(zoom);
		
		 final Dialog mAboutDialog = new Dialog(MainActivity.this,R.style.AboutTheme);

         final RelativeLayout mmAboutDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.addobjectdialog, null);

         
         ((Button) mmAboutDialogView.findViewById(R.id.buttonSave)).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 mAboutDialog.cancel();
            	 
            	 
            	 
            	 PleaceObject obj= new PleaceObject();
            	 obj.setLatitude(map.getCameraPosition().target.latitude);
         		 obj.setLongitude(map.getCameraPosition().target.longitude);
         		 
         		 obj.setName(((EditText) mmAboutDialogView.findViewById(R.id.editText1)).getEditableText().toString());
         		 
         		 
         		 obj.aIcons = new ArrayList<String>(); 
            	
            	 if(((CheckBox) mmAboutDialogView.findViewById(R.id.checkBox1)).isChecked())
            		 obj.aIcons.add("1");         		
            	 if(((CheckBox) mmAboutDialogView.findViewById(R.id.checkBox2)).isChecked())
            		 obj.aIcons.add("2");
            	 if(((CheckBox) mmAboutDialogView.findViewById(R.id.checkBox3)).isChecked())
            		 obj.aIcons.add("3");
            	 if(((CheckBox) mmAboutDialogView.findViewById(R.id.checkBox4)).isChecked())
            		 obj.aIcons.add("4");
            	 if(((CheckBox) mmAboutDialogView.findViewById(R.id.checkBox5)).isChecked())
            		 obj.aIcons.add("5");
         		 
         		 obj.aComments = new ArrayList<ComentsMap>();           		
           		 obj.aComments.add( obj.new ComentsMap(	            				            				
       					"Jakie cechy tego miejsca sprawiaj¹, ¿e chêtnie spêdzasz w nim czas?",
       					((EditText) mmAboutDialogView.findViewById(R.id.editText2)).getEditableText().toString()
       			 ));         		 
         		
            	 addObject(obj);            	 
             }
         });
         
         
          
          mAboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
          mAboutDialog.setContentView(mmAboutDialogView);
         
          mAboutDialog.show();
		
	 }
	
	@Click(R.id.nearButton)
	 void searchNear()
	 {				
		getNearObjects( map.getCameraPosition().target, map.getProjection().getVisibleRegion().latLngBounds);
		webApi.setSettings(getApplicationContext(), "latitude",""+map.getCameraPosition().target.latitude);
		webApi.setSettings(getApplicationContext(), "longitude",""+map.getCameraPosition().target.longitude);	
				
	 }
	
	
	@UiThread
	void updateAdapter(String sCut)
	{
		searchAdapter.clear();
		
		String sText = sCut.toLowerCase();
		
		if(sCut.length()>2)
		{
			for (PleaceObject obs : searchObjects) {			    
				if(obs.getName().toLowerCase().startsWith(sText,0))
					searchAdapter.add(obs);
			}
		}
		
		searchAdapter.notifyDataSetChanged();
		
	}
	
	@Background
	void searchForObjects(String sSearch)
	{		
		searchObjects = webApi.getSearchObjects(apiPrefix,sSearch);
		updateAdapter(sSearch);		
	}

	@Click(R.id.buttonSearch)
	 void showSearchDialog()
	 {	
		final Dialog mNearDialog = new Dialog(this,R.style.AboutTheme);
        final RelativeLayout mNearDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.searchdialog, null);

        
         ((ImageButton) mNearDialogView.findViewById(R.id.CancelSearchDialog)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	mNearDialog.cancel();
                }
         });
         
         ((EditText) mNearDialogView.findViewById(R.id.textEditDialog)).addTextChangedListener(new TextWatcher(){
             public void afterTextChanged(Editable s) {
                 
            	 if(s.length()!=3)
                 {
                	 updateAdapter(s.toString());
                 }else{
                	 
                	 if(searchAdapter.isEmpty())
                		 searchForObjects(s.toString());
                	 else
                		 updateAdapter("");
                 }
                 
                 
             }
             public void beforeTextChanged(CharSequence s, int start, int count, int after){}
             public void onTextChanged(CharSequence s, int start, int before, int count){}
         }); 
         
         
         mNearDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        
         
         searchObjects = new ArrayList<PleaceObject>();
         
         searchAdapter = new SearchPleaceObjectListAdapter(this,R.layout.list_item_search,searchObjects);
  	    
         
        final ListView lvNearObjects = (ListView) mNearDialogView.findViewById(R.id.listViewSearch);
         
        
        lvNearObjects.setClickable(true);
        lvNearObjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        	mNearDialog.cancel();  
            ShowObjectDialog((PleaceObject) lvNearObjects.getItemAtPosition(position));
           
          }
        });        
        
        
        searchAdapter.sort(new Comparator<PleaceObject>() {
 	    	public int compare(PleaceObject object1, PleaceObject object2) {
 	    		return object1.getName().compareTo(object2.getName());
 	    	};
 	    });
 	    
 	   lvNearObjects.setAdapter(searchAdapter);	
        
       mNearDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       mNearDialog.setContentView(mNearDialogView);
        
       mNearDialog.show();
	 }
	
	@Click(R.id.buttonMenu)
	 void showMenu()
	 {	
		final Dialog mMenuDialog = new Dialog(this,R.style.DialogMenuTheme);		
		

		mMenuDialog.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_MENU  && event.getAction() == 0)
			    {
			        dialog.cancel();
			        return true;
			    }
			    return false;
			}			
			
		});
				
        final RelativeLayout mMenuDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.menudialog, null);

        
         ((ImageButton) mMenuDialogView.findViewById(R.id.CancelMenuDialog)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	mMenuDialog.cancel();
                }
         });
/*
         ((Button) mMenuDialogView.findViewById(R.id.buttonClose)).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 MainActivity.this.finish();
             }
         });
*/
         
        
         
         
         ((Button) mMenuDialogView.findViewById(R.id.buttonFavorite)).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 /*isFavView = !isFavView;
            	 
                 webApi.setSettings(getApplicationContext(), "fav", ""+isFavView);
                 
                 if(isSatView)
                 	map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                 
                 ShowFavoriteOnMap(isFavView);
            	 
                 if(isFavView)
                 {*/
            	 	favObjects = webApi.getFavoriteObject(getApplicationContext());
                	 mMenuDialog.cancel();
                	 if(!favObjects.isEmpty())
                		 ShowNearDialog(favObjects);
                	 else                	 
                		 ShowFlashMessage(0, "Nie masz jeszcze ulubionych miejsc");                	 
                // }
                 
             }
         });

         ((Button) mMenuDialogView.findViewById(R.id.buttonAbout)).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 
            	 final Dialog mAboutDialog = new Dialog(MainActivity.this,R.style.AboutTheme);

                 final RelativeLayout mmAboutDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.aboutdialog, null);

                 
                 
                 String  sText = getApplicationContext().getResources().getString(R.string.about_text_string);
              

			
              	((TextView) mmAboutDialogView.findViewById(R.id.aboutText)).setText(Html.fromHtml(sText));
                 
                 
                 
                  ((ImageButton) mmAboutDialogView.findViewById(R.id.CancelDialog)).setOnClickListener(new OnClickListener() {
                         public void onClick(View v) {
                         	mAboutDialog.cancel();
                         }
                  });

                  
                  mAboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                  mAboutDialog.setContentView(mmAboutDialogView);
                 
                  mAboutDialog.show();
                  
             }
         });

         ((Button) mMenuDialogView.findViewById(R.id.buttonLegend)).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 
            	 final Dialog mAboutDialog = new Dialog(MainActivity.this,R.style.AboutTheme);

                 final RelativeLayout mmAboutDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.legenddialog, null);

                 
                  ((ImageButton) mmAboutDialogView.findViewById(R.id.CancelDialog)).setOnClickListener(new OnClickListener() {
                         public void onClick(View v) {
                         	mAboutDialog.cancel();
                         }
                  });

                  
                  mAboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                  mAboutDialog.setContentView(mmAboutDialogView);
                 
                  mAboutDialog.show();
                  
             }
         });

         
         ((Button) mMenuDialogView.findViewById(R.id.buttonSatelite))
       	.setCompoundDrawablesWithIntrinsicBounds(
       			null,
       			null,
       			getApplicationContext().getResources().getDrawable(isSatView?R.drawable.slider_on:R.drawable.slider_off),
       			null
       	);
         
         ((Button) mMenuDialogView.findViewById(R.id.buttonSatelite)).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 isSatView = !isSatView;
         		
                 webApi.setSettings(getApplicationContext(), "satelite", ""+isSatView);
                 
        		 if(isSatView)
        	        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        		 else
        			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        		 
        		 
        		 ((Button) mMenuDialogView.findViewById(R.id.buttonSatelite))
              	.setCompoundDrawablesWithIntrinsicBounds(
              			null,
              			null,
              			getApplicationContext().getResources().getDrawable(isSatView?R.drawable.slider_on:R.drawable.slider_off),
              			null
              	);
        		 
             }
         });
         
         
        // final ListView lvFavObjects = (ListView) mMenuDialogView.findViewById(R.id.listViewFavorite);
         
         /*
         lvNearObjects.setClickable(true);
         lvNearObjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

             ShowObjectDialog((PleaceObject) lvNearObjects.getItemAtPosition(position));
             //mNearDialog.cancel();
             
           }
         });
        favAdapter = new FavPleaceObjectListAdapter(this,R.layout.list_item_near,webApi.getFavoriteObject(getApplicationContext()));
  	    */ 
         
         favAdapter = new FavPleaceObjectListAdapter(this,R.layout.list_item_near,new ArrayList<PleaceObject>());
   	    
         
         /*
  	    favAdapter.sort(new Comparator<PleaceObject>() {
  	    	public int compare(PleaceObject object1, PleaceObject object2) {
  	    		return object1.getName().compareTo(object2.getName());
  	    	};
  	    });*/
  	    
  	  //  lvFavObjects.setAdapter(favAdapter);	
         
  	    ///registerForContextMenu(lvFavObjects);
  	    /*lvFavObjects.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {


            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            	//menu.setHeaderTitle("Menu");
            	menu.add(Menu.NONE, DELETE_ID, Menu.NONE, "Usuñ")
                 .setOnMenuItemClickListener(new OnMenuItemClickListener() {
                     public boolean onMenuItemClick(MenuItem item) {
                    	 	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();         	       		
         	        		PleaceObject ct = favAdapter.getItem(info.position);         	       		
         	        		favAdapter.remove(ct);         	       	    
         	        		webApi.deleteFavorite(getApplicationContext(), ct);
                         return true;
                     }
             });
            }
        });*/
         
         mMenuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         mMenuDialog.setContentView(mMenuDialogView);
        
         mMenuDialog.show();
	 }
		

	
	
	
}
