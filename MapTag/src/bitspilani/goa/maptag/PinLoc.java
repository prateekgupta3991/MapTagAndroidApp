package bitspilani.goa.maptag;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class PinLoc extends ItemizedOverlay<OverlayItem>{

	/*
	 * This class basicaly handles the pin location
	 * feature as a separate function.Choice dependent
	 */
	//constructor #1
	ArrayList<OverlayItem> pinpoints=new ArrayList<OverlayItem>();
	Context c;
	
	public PinLoc(Drawable draw) {
		/*
		 * adding drawable in boundCenter to add it in the
		 * center of the bitmap.
		 */		
		super(boundCenter(draw));
		// TODO Auto-generated constructor stub
	}
	public PinLoc(Drawable draw,Context context) {
		//constructor #2		
		this(draw);
		c=context;
	}

	@Override
	protected OverlayItem createItem(int itemindex) {
		// TODO Auto-generated method stub
		return pinpoints.get(itemindex);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return pinpoints.size();
	}
	
	public void insertPinPoint(OverlayItem oli)
	{
		pinpoints.add(oli);
		this.populate();
	}

}
