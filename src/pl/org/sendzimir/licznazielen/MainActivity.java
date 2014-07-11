package pl.org.sendzimir.licznazielen;

import it.katalpa.licz_na_zilelen.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import pl.org.sendzimir.licznazielen.helper.FavPleaceObjectListAdapter;
import pl.org.sendzimir.licznazielen.helper.NearPleaceObjectListAdapter;
import pl.org.sendzimir.licznazielen.helper.SearchPleaceObjectListAdapter;
import pl.org.sendzimir.licznazielen.model.PleaceObject;
import pl.org.sendzimir.licznazielen.model.PleaceObject.ComentsMap;
import pl.org.sendzimir.licznazielen.services.WebApiService;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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

/**
 *
 * @author LeRafiK
 */

@NoTitle
@EActivity(R.layout.activity_main)
@RoboGuice
@OptionsMenu(R.menu.main)
public class MainActivity extends FragmentActivity implements
		OnMarkerClickListener, OnMarkerDragListener {

	private GoogleMap map;
	boolean isSatView = false;
	boolean isFavView = false;
	boolean hasError = false;
	LatLng myPosition = null;
	String apiPrefix = "beta";
	Dialog progressDialog = null;

	final float fZoomLevel = 18.0f;

	List<PleaceObject> nearObjects = null;
	List<PleaceObject> favObjects = null;
	List<PleaceObject> searchObjects = null;

	FavPleaceObjectListAdapter favAdapter;
	SearchPleaceObjectListAdapter searchAdapter;

	Marker draggMarker = null;

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

	int markersColor[] = { R.drawable.marker_lokalizacji_ulubionej_powiekszony,
			R.drawable.marker_lokalizacji,
			R.drawable.marker_lokalizacji_ulubionej,
			R.drawable.marker_lokalizacji_powiekszony, R.drawable.m5,
			R.drawable.m6, R.drawable.m7, R.drawable.m8, R.drawable.m9,
			R.drawable.m10 };

	@AfterViews
	void initApp() {

		Intent it = this.getIntent();

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);

		map.setOnMarkerClickListener(this);

		map.getUiSettings().setCompassEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(false);

		hasError = it.hasExtra("hasError");
		apiPrefix = it.getStringExtra("prefix");

		myPosition = new LatLng(it.getDoubleExtra("Latitude", Double
				.parseDouble(webApi.getSettings(getApplicationContext(),
						"latitude", "52.2327277"))), it.getDoubleExtra(
				"Longitude", Double.parseDouble(webApi.getSettings(
						getApplicationContext(), "longitude", "21.0129143"))));


		CameraUpdate center = CameraUpdateFactory
				.newCameraPosition(new CameraPosition(myPosition, 15, 0, 0));
		map.moveCamera(center);

		System.out.println(map.getCameraPosition());
		System.out.println(map.getCameraPosition().target.latitude);
		System.out.println(map.getCameraPosition().target.longitude);
		getNearObjects(map.getCameraPosition().target, map.getProjection()
				.getVisibleRegion().latLngBounds, false);

		map.setOnMarkerDragListener(this);

		isSatView = Boolean.parseBoolean(webApi.getSettings(
				getApplicationContext(), "satelite", "false"));
		isFavView = Boolean.parseBoolean(webApi.getSettings(
				getApplicationContext(), "fav", "false"));

		if (isSatView)
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

		apiPrefix = webApi.getPrefixByPosition(myPosition.latitude,
				myPosition.longitude);

		if (apiPrefix.isEmpty()) {
			buttonSearch.setEnabled(false);
			nearButton.setEnabled(false);
			addButton.setEnabled(false);

			for (int i = 0; i < 2; i++)
				ShowFlashMessage(0, getApplicationContext().getResources()
						.getString(R.string.no_signal));
		}
	}

	@UiThread
	public void ShowFavoriteOnMap(boolean isShow) {

		if (isShow) {

			favObjects = webApi.getFavoriteObject(getApplicationContext());
			for (PleaceObject obs : favObjects) {
				PleaceObject near = null;
				if ((near = IsInNearObject(obs.getId())) == null) {
					addMarkerToMap(obs, 2);
				} else {
					near.getMarker().setIcon(
							BitmapDescriptorFactory
									.fromResource(markersColor[2]));
					near.setFavorite(true);
				}
			}

		} else {
			if (favObjects != null)
				for (PleaceObject obs : favObjects) {
					obs.getMarker().remove();
				}

			if (nearObjects != null)
				for (PleaceObject obs : nearObjects) {
					if (obs.getFavorite()) {
						obs.getMarker().setIcon(
								BitmapDescriptorFactory
										.fromResource(markersColor[1]));
						obs.setFavorite(false);
					}
				}

			favObjects = null;
		}

	}

	private double calcDistanceFromMyPosition(LatLng marker) {
		float[] result = new float[1];
		Location.distanceBetween(myPosition.latitude, myPosition.longitude,
				marker.latitude, marker.longitude, result);
		return (double) Math.abs(Math.round(result[0]) / 10) / 100;

	}

	PleaceObject findPleaceObjectByMarker(Marker marker) {

		if (nearObjects != null) {
			for (PleaceObject obs : nearObjects) {

				if (marker.getId().equals(obs.getMarker().getId())) {
					return obs;
				}
			}
		}

		if (favObjects != null) {

			for (PleaceObject obs : favObjects) {

				if (marker.getId().equals(obs.getMarker().getId())) {
					return obs;
				}
			}
		}

		return null;
	}

	public PleaceObject IsInNearObject(int iObjId) {
		if (nearObjects != null)
			for (PleaceObject obs : nearObjects) {

				if (iObjId == obs.getId()) {
					return obs;
				}
			}
		return null;
	}

	public boolean IsInFavorite(int iObjId) {
		if (favObjects != null)
			for (PleaceObject obs : favObjects) {
				if (iObjId == obs.getId()) {
					return true;
				}
			}
		return false;
	}

	@UiThread
	public void ShowFlashMessage(int type, String sMessage) {
		Toast.makeText(getApplicationContext(), sMessage, Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {

		if (marker.isDraggable()) {
			ShowAddObjectDialog(marker);
			return true;
		}

		PleaceObject obj = findPleaceObjectByMarker(marker);

		if (obj != null) {
			ShowObjectDialog(obj);
			return true;
		}

		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((event.getAction() == KeyEvent.ACTION_DOWN)
				&& keyCode == KeyEvent.KEYCODE_BACK) {

			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.signin_quit)
					.setMessage(R.string.signin_really_quit)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									MainActivity.this.finish();

								}

							}).setNegativeButton(R.string.no, null).show();

			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			showMenu();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@UiThread
	public void ClearMarkers() {
		if (nearObjects != null)
			for (PleaceObject obs : nearObjects) {
				obs.getMarker().remove();
			}
		nearObjects = null;
	}

	@UiThread
	public void addMarkerToMap(PleaceObject po, int i) {

		LatLng position = new LatLng(po.getLatitude(), po.getLongitude());

		Marker oMarker = map.addMarker(new MarkerOptions().position(position)
				.title(po.getName()).alpha(1.0f).flat(true)
				.icon(BitmapDescriptorFactory.fromResource(markersColor[i])));

		po.setMarker(oMarker);

		if (!hasError)
			po.setDistance(calcDistanceFromMyPosition(position));

	}

	@UiThread
	public void reAddMarkerToMap(PleaceObject po, int i) {
		po.getMarker().remove();
		addMarkerToMap(po, i);
	}

	@UiThread
	public void addDragMarkerToMap(double lat, double lon) {

		LatLng position = new LatLng(lat, lon);

		map.addMarker(new MarkerOptions().position(position)
				.draggable(true).alpha(1.0f).flat(true)
				.icon(BitmapDescriptorFactory.fromResource(markersColor[0])));
	}

	@UiThread
	public void refreshMap(int iCount) {

		List<PleaceObject> copy = new ArrayList<PleaceObject>(nearObjects); 
		Collections.sort(copy, new Comparator<PleaceObject>() {

			@Override
			public int compare(PleaceObject lhs, PleaceObject rhs) {

				return lhs.compareTo(rhs);
			}
		});

		LatLngBounds.Builder bounds = new LatLngBounds.Builder();

		for (int i = 0; i < iCount; i++) {
			bounds.include(new LatLng(copy.get(i).getLatitude(), copy.get(i)
					.getLongitude()));
		}
		map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
	}

	void ShowObjectDialog(final PleaceObject obj) {
		LatLng position = new LatLng(obj.getLatitude(), obj.getLongitude());
		CameraUpdate center = CameraUpdateFactory
				.newCameraPosition(new CameraPosition(position, fZoomLevel, 0, 0));
		map.moveCamera(center);

		VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();

		double delta = visibleRegion.latLngBounds.northeast.latitude
				- visibleRegion.latLngBounds.southwest.latitude;
		delta = Math.abs(delta) / 4;

		position = new LatLng(obj.getLatitude() - delta, obj.getLongitude());
		center = CameraUpdateFactory.newCameraPosition(new CameraPosition(
				position, fZoomLevel, 0, 0));

		map.moveCamera(center);
		headerText.setText(obj.getName());

		final Dialog mObjectDialog = new Dialog(this, R.style.SearchTheme);
		final RelativeLayout mObjectDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.objectdialog, null);

		final ImageButton favButton = ((ImageButton) mObjectDialogView
				.findViewById(R.id.buttonAddFav));

		if (obj.getFavorite()) {
			favButton.setImageResource(R.drawable.heart_ico_active);
			obj.getMarker().setIcon(
					BitmapDescriptorFactory.fromResource(markersColor[0]));
		} else {
			favButton.setImageResource(R.drawable.heart_ico);
			obj.getMarker().setIcon(
					BitmapDescriptorFactory.fromResource(markersColor[3]));
		}

		favButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (webApi.addFavoriteObject(getApplicationContext(), obj) != null) {
					favButton.setImageResource(R.drawable.heart_ico_active);
					obj.getMarker().setIcon(
							BitmapDescriptorFactory
									.fromResource(markersColor[0]));
					ShowFlashMessage(0, getApplicationContext().getResources()
							.getString(R.string.fav_add));
					obj.setFavorite(true);
				} else {
					favButton.setImageResource(R.drawable.heart_ico);
					obj.getMarker().setIcon(
							BitmapDescriptorFactory
									.fromResource(markersColor[3]));
					webApi.deleteFavorite(getApplicationContext(), obj);
					ShowFlashMessage(0, getApplicationContext().getResources()
							.getString(R.string.fav_del));
					obj.setFavorite(false);
				}

			}
		});

		((ImageButton) mObjectDialogView.findViewById(R.id.buttonShere))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("text/plain");
						intent.putExtra(Intent.EXTRA_TEXT,
								"http://licznazielen.pl");
						startActivity(Intent.createChooser(intent,
								getApplicationContext().getResources()
										.getString(R.string.share)));
					}
				});

		mObjectDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_BACK)) {
					headerText.setText("");

					if (obj.getFavorite()) {
						obj.getMarker().setIcon(
								BitmapDescriptorFactory
										.fromResource(markersColor[2]));
					} else {
						obj.getMarker().setIcon(
								BitmapDescriptorFactory
										.fromResource(markersColor[1]));
					}

				}
				return false;
			}

		});

		TextView lvNearObjects = (TextView) mObjectDialogView
				.findViewById(R.id.objtextView);
		lvNearObjects.setText(obj.getName());

		lvNearObjects = (TextView) mObjectDialogView
				.findViewById(R.id.textViewDistance);
		lvNearObjects.setText(obj.getDistance() + " km");

		lvNearObjects = (TextView) mObjectDialogView
				.findViewById(R.id.aboutText);

		String sText = "";

		if (obj.getComments() != null)
			for (ComentsMap nm : obj.getComments()) {
				sText += "<b>" + nm.getKey() + "</b><br>- " + nm.getValue()
						+ "<br><br>";
			}

		lvNearObjects.setText(Html.fromHtml(sText));

		mObjectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mObjectDialog.setContentView(mObjectDialogView);

		mObjectDialog.show();
	}

	@UiThread
	void ShowNearDialog(final List<PleaceObject> aObjectList) {

		final Dialog mNearDialog = new Dialog(this, R.style.SearchTheme);
		final RelativeLayout mNearDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.neardialog, null);

		final ListView lvNearObjects = (ListView) mNearDialogView
				.findViewById(R.id.nearlistView);

		NearPleaceObjectListAdapter adapter = new NearPleaceObjectListAdapter(
				this, R.layout.list_item_near, aObjectList);

		adapter.sort(new Comparator<PleaceObject>() {
			public int compare(PleaceObject object1, PleaceObject object2) {
				// SORT CHANGE
				return object1.compareTo(object2);
				// return (-1)*(object1.getName().compareTo(object2.getName()));
			};
		});

		lvNearObjects.setAdapter(adapter);

		lvNearObjects.setClickable(true);
		lvNearObjects
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {

						ShowObjectDialog((PleaceObject) lvNearObjects
								.getItemAtPosition(position));

					}
				});

		mNearDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mNearDialog.setContentView(mNearDialogView);

		mNearDialog.show();
	}

	@UiThread
	void ShowNotFoundDialog() {
		final Dialog mAboutDialog = new Dialog(MainActivity.this,
				R.style.AboutTheme);

		final RelativeLayout mmAboutDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.dialog_confrim, null);

		((Button) mmAboutDialogView.findViewById(R.id.dlgOkButton))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mAboutDialog.cancel();
					}
				});

		mAboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mAboutDialog.setContentView(mmAboutDialogView);

		mAboutDialog.show();
	}

	@UiThread
	void ShowFlashMessageEx(int i, boolean sn) {
		// if(sn)
		ShowFlashMessage(0, getApplicationContext().getResources().getString(i));
	}

	@UiThread
	void ShowProgressDialog(boolean sh) {
		if (sh) {

			if (progressDialog == null) {

				progressDialog = new Dialog(MainActivity.this,
						R.style.AboutTheme);

				RelativeLayout mmAboutDialogView = (RelativeLayout) getLayoutInflater()
						.inflate(R.layout.dialog_progress, null);

				Animation animation = AnimationUtils.loadAnimation(this,
						R.anim.rotate);
				((ImageView) mmAboutDialogView.findViewById(R.id.imageRadar2))
						.startAnimation(animation);

				progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				progressDialog.setContentView(mmAboutDialogView);

				progressDialog.show();
			}
		} else {

			if (progressDialog != null) {
				progressDialog.cancel();
			}
			progressDialog = null;

		}
	}

	@Background
	void getNearObjects(LatLng ll, LatLngBounds nowBounds, boolean showDialogs) {

		ShowProgressDialog(true);
		ClearMarkers();

		nearObjects = webApi.getNearObjects(apiPrefix, ll.latitude,
				ll.longitude);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

		}

		if (nearObjects == null) {
			ShowFlashMessageEx(R.string.no_signal, true);
		} else {

			if (nearObjects.size() == 0) {
				if (showDialogs)
					ShowNotFoundDialog();
			} else {

				int iCounter = 0;

				for (int i = 0; i < nearObjects.size(); i++) {
					PleaceObject po = nearObjects.get(i);
					addMarkerToMap(po, 1);

					iCounter += (nowBounds.contains(new LatLng(
							po.getLatitude(), po.getLongitude())) ? 1 : 0);
				}

				if (iCounter < 5)
					refreshMap(5);

				if (showDialogs)
					ShowNearDialog(nearObjects);
			}

		}

		ShowFavoriteOnMap(false);
		ShowFavoriteOnMap(true);

		ShowProgressDialog(false);
	}

	@Background
	void addObject(PleaceObject obj) {
		if (webApi.addPleace(apiPrefix, getApplicationContext(), obj)) {
			obj.setMyObject(true);
			obj.setFavorite(true);
			webApi.addFavoriteObject(getApplicationContext(), obj);
			favObjects.add(obj);
			addMarkerToMap(obj, 2);
			ShowFlashMessage(0, getApplicationContext().getResources()
					.getString(R.string.place_add));
		} else {
			ShowFlashMessage(0, getApplicationContext().getResources()
					.getString(R.string.place_add_error));
		}

	}

	private void ShowAddObjectDialog(final Marker dMarker) {

		LatLng position = new LatLng(dMarker.getPosition().latitude,
				dMarker.getPosition().longitude);

		CameraUpdate center = CameraUpdateFactory
				.newCameraPosition(new CameraPosition(position, fZoomLevel, 0,
						0));
		// CameraUpdate center =
		// CameraUpdateFactory.newLatLngZoom(position,fZoomLevel);
		map.moveCamera(center);

		VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();

		double delta = visibleRegion.latLngBounds.northeast.latitude
				- visibleRegion.latLngBounds.southwest.latitude;
		delta = Math.abs(delta) / 4 + Math.abs(delta) / 8;

		position = new LatLng(dMarker.getPosition().latitude - delta,
				dMarker.getPosition().longitude);

		// center = CameraUpdateFactory.newLatLng(position);
		center = CameraUpdateFactory.newCameraPosition(new CameraPosition(
				position, fZoomLevel, 0, 0));
		map.moveCamera(center);

		final Dialog mAboutDialog = new Dialog(MainActivity.this,
				R.style.SearchTheme);

		final RelativeLayout mmAboutDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.addobjectdialog, null);

		((Button) mmAboutDialogView.findViewById(R.id.buttonSave))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mAboutDialog.cancel();

						PleaceObject obj = new PleaceObject();
						obj.setLatitude(dMarker.getPosition().latitude);
						obj.setLongitude(dMarker.getPosition().longitude);

						obj.setName(((EditText) mmAboutDialogView
								.findViewById(R.id.editText1))
								.getEditableText().toString());

						obj.aIcons = new ArrayList<String>();

						if (((CheckBox) mmAboutDialogView
								.findViewById(R.id.checkBox1)).isChecked())
							obj.aIcons.add("1");
						if (((CheckBox) mmAboutDialogView
								.findViewById(R.id.checkBox2)).isChecked())
							obj.aIcons.add("2");
						if (((CheckBox) mmAboutDialogView
								.findViewById(R.id.checkBox3)).isChecked())
							obj.aIcons.add("3");
						if (((CheckBox) mmAboutDialogView
								.findViewById(R.id.checkBox4)).isChecked())
							obj.aIcons.add("4");
						if (((CheckBox) mmAboutDialogView
								.findViewById(R.id.checkBox5)).isChecked())
							obj.aIcons.add("5");

						obj.aComments = new ArrayList<ComentsMap>();
						obj.aComments.add(obj.new ComentsMap(
								getApplicationContext().getResources()
										.getString(R.string.place_add_comment),
								((EditText) mmAboutDialogView
										.findViewById(R.id.editText2))
										.getEditableText().toString()));

						addObject(obj);
						dMarker.remove();
					}
				});

		mAboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mAboutDialog.setContentView(mmAboutDialogView);

		mAboutDialog.show();
	}

	@Click(R.id.addButton)
	void addObject() {

		addDragMarkerToMap(map.getCameraPosition().target.latitude,
				map.getCameraPosition().target.longitude);

		ShowFlashMessageEx(R.string.hold_marker, false);
	}

	@Click(R.id.nearButton)
	void searchNear() {

		getNearObjects(map.getCameraPosition().target, map.getProjection()
				.getVisibleRegion().latLngBounds, true);
		webApi.setSettings(getApplicationContext(), "latitude",
				"" + map.getCameraPosition().target.latitude);
		webApi.setSettings(getApplicationContext(), "longitude",
				"" + map.getCameraPosition().target.longitude);

	}

	private void executeGeoSerach(final RelativeLayout mNearDialogView) {
		LatLng pos = map.getCameraPosition().target;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("map lat long:" + pos.toString()).setTitle("debug");
		AlertDialog dialog = builder.create();
		// dialog.show();

		String textLocation = webApi.getPrefixByPosition(pos.latitude,
				pos.longitude);

		Geocoder geoCoder = new Geocoder(MainActivity.this, Locale.getDefault());
		try {
			List<Address> addresses;
			
			addresses = geoCoder.getFromLocationName(
					((EditText) mNearDialogView
							.findViewById(R.id.textEditDialog)).getText()
							.toString()
							+ "," + textLocation + " Polska", 1);
			
			if (addresses != null && addresses.size() > 0) {
				LatLng latlon = new LatLng(addresses.get(0).getLatitude(),
						addresses.get(0).getLongitude());

				CameraUpdate center = CameraUpdateFactory
						.newCameraPosition(new CameraPosition(latlon, 15, 0, 0));

				map.moveCamera(center);
			}
		} catch (IOException e) {
			e.printStackTrace();
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder.setMessage(
					"Niestety nie odnaleziono lokalizacji dla tego miejsca")
					.setTitle("Geolokalizacja");
			AlertDialog dialog2 = builder2.create();
			dialog2.show();
		}

	}

	@Click(R.id.buttonSearch)
	void showSearchDialog() {

		final Dialog mNearDialog = new Dialog(this, R.style.AboutTheme);
		final RelativeLayout mNearDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.searchdialog, null);

		((ImageButton) mNearDialogView.findViewById(R.id.CancelSearchDialog))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mNearDialog.cancel();
					}
				});

		EditText etVid = (EditText) mNearDialogView
				.findViewById(R.id.textEditDialog);

		etVid.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {

					mNearDialog.cancel();
					executeGeoSerach(mNearDialogView);

					return true;
				}
				return false;
			}
		});

		((ImageButton) mNearDialogView.findViewById(R.id.searchSearchDialog))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mNearDialog.cancel();

						executeGeoSerach(mNearDialogView);
					}
				});

		mNearDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		searchObjects = new ArrayList<PleaceObject>();

		searchAdapter = new SearchPleaceObjectListAdapter(this,
				R.layout.list_item_search, searchObjects);

		final ListView lvNearObjects = (ListView) mNearDialogView
				.findViewById(R.id.listViewSearch);

		lvNearObjects.setClickable(true);
		lvNearObjects
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						mNearDialog.cancel();
						ShowObjectDialog((PleaceObject) lvNearObjects
								.getItemAtPosition(position));

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
	void showMenu() {
		final Dialog mMenuDialog = new Dialog(this, R.style.DialogMenuTheme);

		mMenuDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == 0) {
					dialog.cancel();
					return true;
				}
				return false;
			}

		});

		final RelativeLayout mMenuDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.menudialog, null);

		((ImageButton) mMenuDialogView.findViewById(R.id.CancelMenuDialog))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mMenuDialog.cancel();
					}
				});

		((Button) mMenuDialogView.findViewById(R.id.buttonFavorite))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {

						mMenuDialog.cancel();
						if (!favObjects.isEmpty())
							ShowNearDialog(favObjects);
						else
							ShowFlashMessage(0, getApplicationContext()
									.getResources().getString(R.string.fav_0));

					}
				});

		((Button) mMenuDialogView.findViewById(R.id.buttonAbout))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {

						final Dialog mAboutDialog = new Dialog(
								MainActivity.this, R.style.AboutTheme);

						final RelativeLayout mmAboutDialogView = (RelativeLayout) getLayoutInflater()
								.inflate(R.layout.aboutdialog, null);

						String sText = getApplicationContext().getResources()
								.getString(R.string.about_text_string);

						((TextView) mmAboutDialogView
								.findViewById(R.id.aboutText)).setText(Html
								.fromHtml(sText));

						((ImageButton) mmAboutDialogView
								.findViewById(R.id.CancelDialog))
								.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										mAboutDialog.cancel();
									}
								});

						mAboutDialog
								.requestWindowFeature(Window.FEATURE_NO_TITLE);
						mAboutDialog.setContentView(mmAboutDialogView);

						mAboutDialog.show();

					}
				});

		((Button) mMenuDialogView.findViewById(R.id.buttonLegend))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {

						final Dialog mAboutDialog = new Dialog(
								MainActivity.this, R.style.AboutTheme);

						final RelativeLayout mmAboutDialogView = (RelativeLayout) getLayoutInflater()
								.inflate(R.layout.legenddialog, null);

						((ImageButton) mmAboutDialogView
								.findViewById(R.id.CancelDialog))
								.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										mAboutDialog.cancel();
									}
								});

						mAboutDialog
								.requestWindowFeature(Window.FEATURE_NO_TITLE);
						mAboutDialog.setContentView(mmAboutDialogView);

						mAboutDialog.show();

					}
				});

		((Button) mMenuDialogView.findViewById(R.id.buttonSatelite))
				.setCompoundDrawablesWithIntrinsicBounds(
						null,
						null,
						getApplicationContext().getResources().getDrawable(
								isSatView ? R.drawable.slider_on
										: R.drawable.slider_off), null);

		((Button) mMenuDialogView.findViewById(R.id.buttonSatelite))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						isSatView = !isSatView;

						webApi.setSettings(getApplicationContext(), "satelite",
								"" + isSatView);

						if (isSatView)
							map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
						else
							map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

						((Button) mMenuDialogView
								.findViewById(R.id.buttonSatelite))
								.setCompoundDrawablesWithIntrinsicBounds(
										null,
										null,
										getApplicationContext()
												.getResources()
												.getDrawable(
														isSatView ? R.drawable.slider_on
																: R.drawable.slider_off),
										null);

					}
				});

		favAdapter = new FavPleaceObjectListAdapter(this,
				R.layout.list_item_near, new ArrayList<PleaceObject>());

		mMenuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mMenuDialog.setContentView(mMenuDialogView);

		mMenuDialog.show();
	}

	@Override
	public void onMarkerDrag(Marker arg0) {

	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		ShowAddObjectDialog(arg0);
		// draggMarker = null;
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		draggMarker = arg0;
	}

}
