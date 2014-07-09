package it.katalpa.licz_na_zilelen;

import roboguice.util.Ln;
import it.katalpa.licz_na_zilelen.service.WebApiService;
import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.NoTitle;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.ViewById;

/**
*
* @coded by katalpa.it
*/

@NoTitle
@EActivity(R.layout.activity_splash)
@RoboGuice
public class SplashActivity extends Activity implements LocationListener{

	private static long SLEEP_TIME = 3;
	
	private static boolean isFounded = true;
	
	@ViewById
	ImageView imageRadar;
	
	String provider;
	LocationManager service;
	
	@Inject
	WebApiService webApi;	
	
	@AfterViews
	void initConf() {
		System.out.println("initConf");
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
		imageRadar.startAnimation(animation);
		System.out.println("imageRadar");
		service = (LocationManager) getSystemService(LOCATION_SERVICE);
		System.out.println("service");
		Criteria criteria = new Criteria();
		provider = service.getBestProvider(criteria, true);
	    Location location = service.getLastKnownLocation(provider);		
		System.out.println("location:"+location);
		//boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
			/*	
		if (!enabled) {
			  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			  startActivity(intent);
		}else{
			*/		
			
			if (location != null) {
				System.out.println("location is null");
			    onLocationChanged(location);
				//runInBackground(true,location);
			}else{
				System.out.println("else, there is location");
				runInBackground(false,location);
			}

		//}
		
		
	}
	
	
		@Override
	  protected void onResume() {
	    super.onResume();
	    isFounded = true;
	    service.requestLocationUpdates(provider, 400, 1, this);
	  }

	   @Override
	  protected void onPause() {
	    super.onPause();
	    service.removeUpdates(this);
	  }
	   
	
	@Background
	void runInBackground(boolean isGood,Location loc) {	
			
		try {
	        Thread.sleep(SLEEP_TIME*1000);
	     } catch (Exception e) {
	        Ln.e(e.getMessage());
	     }	    
		
		Intent intent = new Intent(SplashActivity.this, MainActivity_.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		
		
		
		if(isGood && loc!=null)
		{
			intent.putExtra("Latitude", loc.getLatitude());
			intent.putExtra("Longitude", loc.getLongitude());
			intent.putExtra("prefix", webApi.getPrefixByPosition(loc.getLatitude(),loc.getLongitude()));
		}else{
			intent.putExtra("hasError", true);
			intent.putExtra("prefix","");
		}
		
		SplashActivity.this.startActivity(intent);
	    SplashActivity.this.finish();
     
	}

	@Override
	public void onLocationChanged(Location arg0) {
		
		if(isFounded)
			runInBackground(true,arg0);
		
		isFounded = false;
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
