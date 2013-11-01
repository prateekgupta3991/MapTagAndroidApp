package bitspilani.goa.maptag;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.widget.Toast;

public class MainAct extends MapActivity implements LocationListener{

	MapView mp;
	long starttouch,stoptouch;
	
	List<Overlay> OLlist;
	MyLocationOverlay compass;
	MapController cntlr;
	//MapController is used for control of map during person movement
	
	int xol,yol;
	GeoPoint touchedpt;
	
	LocationManager lm;
	String tow;
	int lat=0;
	int lon=0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.main);
		mp=(MapView) findViewById(R.id.map);
		mp.setBuiltInZoomControls(true);
		
		Touch t=new Touch();
		OLlist=mp.getOverlays();
		OLlist.add(t);
		compass=new MyLocationOverlay(MainAct.this, mp);
		OLlist.add(compass);
		cntlr=mp.getController();
		GeoPoint point = new GeoPoint(153908, 738775);
		cntlr.animateTo(point);
		cntlr.setZoom(6);
		
		//placing pinpoint on user current location through GPS value
		lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria cr=new Criteria();
		
		tow=lm.getBestProvider(cr, false);
		Location lock=lm.getLastKnownLocation(tow);
		if(lock != null)
		{
			lat=(int)(lock.getLatitude()*1E6);
			lon=(int)(lock.getLongitude()*1E6);
			GeoPoint gp=new GeoPoint(lat,lon);
			PinLoc pin=new PinLoc(getResources().getDrawable(R.drawable.ic_launcher), getBaseContext());
			OverlayItem oil=new OverlayItem(gp, "1st string", "2nd string");
			pin.insertPinPoint(oil);
			OLlist.add(pin);
		}
		else
		{
			Toast.makeText(getBaseContext(), "f**k up!!not able to get  position", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	@Override
	protected void onPause() { 
		// TODO Auto-generated method stub
		compass.disableCompass();
		super.onPause();
		lm.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		compass.enableCompass();
		super.onResume();
		lm.requestLocationUpdates(tow, 1000, 1, this);
	}


	/*
	 * Overlay is used for google map API for drawing the user touch etc
	 * happenings on the map. In a nutshell, to do stuff on a layer above
	 * the map layer.
	 * Just to lay off multiple computations on the map part only
	 */

	public class Touch extends Overlay{

		@SuppressWarnings("deprecation")
		@Override
		public boolean onTouchEvent(MotionEvent me, MapView m) {
			// TODO Auto-generated method stub
			if(me.getAction() == MotionEvent.ACTION_DOWN)
			{
				starttouch=me.getEventTime();
				xol=(int)me.getX();
				yol=(int)me.getY();
				touchedpt=mp.getProjection().fromPixels(xol, yol);
				System.out.println("Succesfull x= " + xol+ " y= "+yol);
			}
			else if(me.getAction() == MotionEvent.ACTION_UP)
			{
				stoptouch=me.getEventTime();
			}
			if((stoptouch - starttouch) > 1000)
			{
				AlertDialog dial=new AlertDialog.Builder(MainAct.this).create();
				dial.setTitle("Option");
				dial.setMessage("Pick an option");
				dial.setButton("Pin location", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						PinLoc pin=new PinLoc(getResources().getDrawable(R.drawable.ic_launcher), getBaseContext());
						OverlayItem oil=new OverlayItem(touchedpt, "1st string", "2nd string");
						pin.insertPinPoint(oil);
						OLlist.add(pin);
					}
				});
				
				dial.setButton2("Get Add", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog,int which) {
						// TODO Auto-generated method stub
						//Geocoder gives the information about a point on map
						//like locating the address on the touched point
						Geocoder code1=new Geocoder(MainAct.this, Locale.getDefault());
						try{
							List<Address> add=code1.getFromLocation(touchedpt.getLatitudeE6()/1E6, touchedpt.getLongitudeE6()/1E6, 1);
							if(add.size() > 0)
							{ 
								String display="";
								for(int i=0;i < add.get(0).getMaxAddressLineIndex();i++)
								{
									display+=add.get(0).getAddressLine(i)+"\n";
								}
								Toast.makeText(MainAct.this, display, Toast.LENGTH_SHORT).show();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(getBaseContext(), "Not able to do", Toast.LENGTH_SHORT).show();
						}
						finally
						{
							System.out.println("Succesfull");
						}
					}
				});
				dial.setButton3("Toggle View", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(mp.isSatellite())
						{
							mp.setSatellite(false);
							mp.setStreetView(true);
						}
						else
						{
							mp.setStreetView(false);
							mp.setSatellite(true);
						}
					}
				});

				dial.show();
				return true;
			}
			return super.onTouchEvent(me, m);
		}
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void onLocationChanged(Location l) {
		// TODO Auto-generated method stub
		lat=(int)(l.getLatitude()*1E6);
		lon=(int)(l.getLongitude()*1E6);
		GeoPoint gp=new GeoPoint(lat, lat);
		PinLoc pin=new PinLoc(getResources().getDrawable(R.drawable.ic_launcher), getBaseContext());
		OverlayItem oil=new OverlayItem(gp, "1st string", "2nd string");
		pin.insertPinPoint(oil);
		OLlist.add(pin);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "Provider is disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "Provider is Enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
