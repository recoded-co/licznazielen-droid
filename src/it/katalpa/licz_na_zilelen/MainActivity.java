package it.katalpa.licz_na_zilelen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import it.katalpa.licz_na_zilelen.service.WebApiService;

@NoTitle
@EActivity(R.layout.activity_main)
@RoboGuice
@OptionsMenu(R.menu.main)
public class MainActivity extends Activity implements OnMarkerClickListener {

	private GoogleMap map; 
	boolean isSatView = false;
	boolean isFavView = false;
	boolean hasError = false;
	LatLng myPosition = null;
	
	List<PleaceObject> nearObjects =  null;
	List<PleaceObject> favObjects =  null;
	List<PleaceObject> searchObjects =  null;
	
	private static final int DELETE_ID = Menu.FIRST + 4;
	FavPleaceObjectListAdapter favAdapter;
	 SearchPleaceObjectListAdapter searchAdapter;
	 
	@ViewById
	EditText editSearch;
	
	@Inject
	WebApiService webApi;	
	
	int markersColor[]={
			R.drawable.m1,
			R.drawable.m2,
			R.drawable.m3,
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
			
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	    map.setMyLocationEnabled(true);
		
	    map.setOnMarkerClickListener(this);
	    
	    map.getUiSettings().setCompassEnabled(true);
	    
	    
	    hasError = it.hasExtra("hasError");
	    	    		
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
        
        
        if(isFavView)
        	ShowFavoriteOnMap(true);
          
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
        
        
	}
	
	private void ShowFavoriteOnMap(boolean isShow)
	{
		
		if(isShow)
		{			
			favObjects = webApi.getFavoriteObject(getApplicationContext());
			for (PleaceObject obs : favObjects) {
			      addMarkerToMap(obs,0);			      
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
		 return (double)result[0]/1000;
		
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
	
	
	@UiThread
	 public void ShowFlashMessage(int type,String sMessage)
	{
		Toast.makeText(getApplicationContext(), sMessage, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onMarkerClick(final Marker marker) {
	    	
		PleaceObject obj = findPleaceObjectByMarker(marker);
		
		Log.v("duda","oki");
		
		if(obj!=null)
		{
			Log.v("duda",obj.getName());
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
	            .alpha(0.7f)
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
		final Dialog mObjectDialog = new Dialog(this,R.style.DialogCustomTheme);
        final RelativeLayout mObjectDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.objectdialog, null);

        
         ((Button) mObjectDialogView.findViewById(R.id.objectDialogClose)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	mObjectDialog.cancel();
                }
         });
         
         ((Button) mObjectDialogView.findViewById(R.id.buttonAddFav)).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 
            	 if(webApi.addFavoriteObject(getApplicationContext(), obj)!=null)
            		 Toast.makeText(getApplicationContext(), "dodano do ulubionych", Toast.LENGTH_LONG).show();
            	 else
            		 Toast.makeText(getApplicationContext(), "juz jest w ulubionych", Toast.LENGTH_LONG).show();
             }
         });
         

        final TextView lvNearObjects = (TextView) mObjectDialogView.findViewById(R.id.objtextView);
        lvNearObjects.setText(obj.getName()+" - "+obj.getDistance()+" km");
            
        mObjectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mObjectDialog.setContentView(mObjectDialogView);
        
        mObjectDialog.show();
	}
	
	@UiThread
	void ShowNearDialog()
	{

		final Dialog mNearDialog = new Dialog(this,R.style.DialogCustomTheme);
        final RelativeLayout mNearDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.neardialog, null);

        
         ((Button) mNearDialogView.findViewById(R.id.nearDialogClose)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	mNearDialog.cancel();
                }
         });
         

        final ListView lvNearObjects = (ListView) mNearDialogView.findViewById(R.id.nearlistView);
         
        
        lvNearObjects.setClickable(true);
        lvNearObjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

            ShowObjectDialog((PleaceObject) lvNearObjects.getItemAtPosition(position));
            //mNearDialog.cancel();
            
          }
        });        
        
        NearPleaceObjectListAdapter adapter = new NearPleaceObjectListAdapter(this,R.layout.list_item_near,nearObjects);
 	    
 	    adapter.sort(new Comparator<PleaceObject>() {
 	    	public int compare(PleaceObject object1, PleaceObject object2) {
 	    		return object1.getName().compareTo(object2.getName());
 	    	};
 	    });
 	    
 	   lvNearObjects.setAdapter(adapter);	
         
       mNearDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       mNearDialog.setContentView(mNearDialogView);
        
       mNearDialog.show();
	}
	
	
	@Background
	void getNearObjects(LatLng ll,LatLngBounds nowBounds)
	{
		//webApi.getSearchObjects("non");
		ClearMarkers();
		
		nearObjects =  webApi.getNearObjects(ll.latitude,ll.longitude);
		
		if(nearObjects.size()==0)
			ShowFlashMessage(0,"W Twoim otoczeniu nie ma ¿adnych miejsc. Miasta aktualnie obs³ugiwane przez aplikacjê to Poznañ, £ódŸ, Kraków i Warszawa");
		
		int iCounter = 0;
		
		for(int i=0;i<nearObjects.size();i++)
		{			
			PleaceObject po = nearObjects.get(i);
			addMarkerToMap(po,i);			
			
			iCounter += (nowBounds.contains(new LatLng(po.getLatitude(), po.getLongitude()))?1:0);
		}
		
		if(iCounter<5)
			refreshMap(5);
		ShowNearDialog();
	}
	
	@Background
	void addObject(PleaceObject obj)
	{
		if(webApi.addPleace(getApplicationContext(), obj))
		{
			addMarkerToMap(obj,0);
			ShowFlashMessage(0, "Miejsce dodane");
		}else
			ShowFlashMessage(0, "Miejsce juz dodane");
	}
	
	@Click(R.id.buttonShare)
	 void share()
	 {
		
		/*Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "http://licznazielen.pl");
		startActivity(Intent.createChooser(intent, "Udostêpnij"));*/
		
		PleaceObject obj = new PleaceObject();		
		
		obj.setLatitude(map.getCameraPosition().target.latitude);
		obj.setLongitude(map.getCameraPosition().target.longitude);		
		
		addObject(obj);		
		
	}
	
	@Click(R.id.nearButton)
	 void searchNear()
	 {				
		getNearObjects( map.getCameraPosition().target, map.getProjection().getVisibleRegion().latLngBounds);
		webApi.setSettings(getApplicationContext(), "latitude",""+map.getCameraPosition().target.latitude);
		webApi.setSettings(getApplicationContext(), "longitude",""+map.getCameraPosition().target.longitude);	
		
		
		/*
		 * VisibleRegion visibleRegion = mMap.getProjection()
                    .getVisibleRegion();

Point x = mMap.getProjection().toScreenLocation(
                    visibleRegion.farRight);

Point y = mMap.getProjection().toScreenLocation(
                    visibleRegion.nearLeft);

Point centerPoint = new Point(x.x / 2, y.y / 2);

LatLng centerFromPoint = mMap.getProjection().fromScreenLocation(
                    centerPoint);
		 */
		
		//
	 }
	
	
	@UiThread
	void updateAdapter(String sCut)
	{
		searchAdapter.clear();
		
		if(sCut.length()>2)
		{
			for (PleaceObject obs : searchObjects) {			    
				//if(obs.getName().startsWith(sCut,0))
					searchAdapter.add(obs);
			}
		}
		
		searchAdapter.notifyDataSetChanged();
		
	}
	
	@Background
	void searchForObjects(String sSearch)
	{		
		searchObjects = webApi.getSearchObjects(sSearch);
		updateAdapter(sSearch);		
	}

	@Click(R.id.buttonSearch)
	 void showSearchDialog()
	 {	
		final Dialog mNearDialog = new Dialog(this,R.style.DialogCustomTheme);
        final RelativeLayout mNearDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.searchdialog, null);

        
         ((Button) mNearDialogView.findViewById(R.id.CancelSearchDialog)).setOnClickListener(new OnClickListener() {
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
         
         
         searchObjects = new ArrayList<PleaceObject>();
         
         searchAdapter = new SearchPleaceObjectListAdapter(this,R.layout.list_item_near,searchObjects);
  	    
         
         

        final ListView lvNearObjects = (ListView) mNearDialogView.findViewById(R.id.listViewSearch);
         
        
        lvNearObjects.setClickable(true);
        lvNearObjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

            ShowObjectDialog((PleaceObject) lvNearObjects.getItemAtPosition(position));
            //mNearDialog.cancel();
            
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
/*
		mMenuDialog.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_MENU)
			    {
			        dialog.cancel();
			        return false;
			    }
			    return true;
			}			
			
		});
		
		*/
		
		
        final RelativeLayout mMenuDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.menudialog, null);

        
         ((Button) mMenuDialogView.findViewById(R.id.CancelMenuDialog)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	mMenuDialog.cancel();
                }
         });

         ((Button) mMenuDialogView.findViewById(R.id.buttonClose)).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 MainActivity.this.finish();
             }
         });

         ((Button) mMenuDialogView.findViewById(R.id.buttonFavorite)).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 isFavView = !isFavView;
            	 
                 webApi.setSettings(getApplicationContext(), "fav", ""+isFavView);
                 
                 if(isSatView)
                 	map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                 
                 ShowFavoriteOnMap(isFavView);
            	 
             }
         });

         ((Button) mMenuDialogView.findViewById(R.id.buttonAbout)).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 
            	 final Dialog mAboutDialog = new Dialog(MainActivity.this,R.style.DialogCustomTheme);

                 final RelativeLayout mmAboutDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.aboutdialog, null);

                 
                  ((Button) mmAboutDialogView.findViewById(R.id.CancelDialog)).setOnClickListener(new OnClickListener() {
                         public void onClick(View v) {
                         	mAboutDialog.cancel();
                         }
                  });

                  
                  mAboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                  mAboutDialog.setContentView(mmAboutDialogView);
                 
                  mAboutDialog.show();
                  
             }
         });

         ((Button) mMenuDialogView.findViewById(R.id.buttonSatelite)).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 isSatView = !isSatView;
         		
                 webApi.setSettings(getApplicationContext(), "satelite", ""+isSatView);
                 
        		 if(isSatView)
        	        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        		 else
        			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
             }
         });
         
         
         final ListView lvFavObjects = (ListView) mMenuDialogView.findViewById(R.id.listViewFavorite);
         
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
  	    
  	    lvFavObjects.setAdapter(favAdapter);	
         
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
