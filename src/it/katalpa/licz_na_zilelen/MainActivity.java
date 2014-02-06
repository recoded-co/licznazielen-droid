package it.katalpa.licz_na_zilelen;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.inject.Inject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Fullscreen;
import com.googlecode.androidannotations.annotations.NoTitle;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.UiThread;

import it.katalpa.licz_na_zilelen.R;
import it.katalpa.licz_na_zilelen.model.PleaceObject;
import it.katalpa.licz_na_zilelen.service.WebApiService;

@NoTitle
@EActivity(R.layout.activity_main)
@RoboGuice
@Fullscreen
public class MainActivity extends Activity implements OnMarkerClickListener {

	private GoogleMap map; 
	
	boolean hasError = false;
	LatLng myPosition = null;
	
	List<PleaceObject> nearObjects;
	
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
		
	    map.getUiSettings().setCompassEnabled(true);
	    
	    myPosition = new LatLng(it.getDoubleExtra("Latitude", 16.5603), it.getDoubleExtra("Longitude",52.2430));
	    
	    CameraUpdate center = CameraUpdateFactory.newLatLng(myPosition);
	    CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        map.moveCamera(center);
        map.animateCamera(zoom);
        
	}
	
	
	private double calcDistanceFromMyPosition(LatLng marker)
	{
		Location locationA = new Location("location A"); 
		
		locationA.setLatitude(myPosition.latitude);  
		locationA.setLongitude(myPosition.longitude);  

		Location locationB = new Location("location B");  

		locationB.setLatitude(marker.latitude);  
		locationB.setLongitude(marker.longitude);;  

		return  locationA.distanceTo(locationB);
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
	    
	    
	    return true;
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
	    	
	    	 return true;
	     }
	     return super.onKeyDown(keyCode, event);
	 }
	
	
	@UiThread
	 public void addMarkerToMap(PleaceObject po,int i)
	 {
		 Log.v("duda",po.getName());
		 
		 LatLng position = new LatLng(po.getLatitude(), po.getLongitude());
		 
		 map.addMarker(new MarkerOptions()
	            .position(position)
	            .title(po.getName())
	            .alpha(0.7f)
	            .flat(true)
	            .icon(BitmapDescriptorFactory.fromResource(markersColor[i]))
            );
		 
		 po.setDistance(calcDistanceFromMyPosition(position));		 
	 }
	
	@UiThread
	 public void refreshMap()
	 {
	
			
	 }
	
	@Background
	void getNearObjects(LatLng ll)
	{
		//webApi.getSearchObjects("non");
				
		nearObjects =  webApi.getNearObjects(ll.latitude,ll.longitude);
		
		for(int i=0;i<nearObjects.size();i++)
		{			
			PleaceObject po = nearObjects.get(i);
			addMarkerToMap(po,i);
		}
		
	}
	
	@Click(R.id.nearButton)
	 void searchNear()
	 {				
		getNearObjects( map.getCameraPosition().target);
		
		
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
	
}
