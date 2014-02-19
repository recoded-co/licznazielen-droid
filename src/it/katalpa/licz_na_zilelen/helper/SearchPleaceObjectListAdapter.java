package it.katalpa.licz_na_zilelen.helper;

import it.katalpa.licz_na_zilelen.R;
import it.katalpa.licz_na_zilelen.model.PleaceObject;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



public class SearchPleaceObjectListAdapter extends ArrayAdapter<PleaceObject> {

	private List<PleaceObject> alData;
	Context oContext;
	
	public SearchPleaceObjectListAdapter(Context context, int textViewResourceId, List<PleaceObject> nearObjects){
		super(context, textViewResourceId, nearObjects);
        alData = nearObjects;
        oContext = context;
    }
	
	@Override
	public int getCount() {
		return alData.size();
	}

	@Override
	public PleaceObject getItem(int position) {
		 return alData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
			View v = convertView;
		    if (v == null)
		    {
		       LayoutInflater vi = (LayoutInflater)oContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		       v = vi.inflate(R.layout.list_item_search, null);
		    }

         
          TextView timeView = (TextView)v.findViewById(R.id.licTextView);

          PleaceObject msg = alData.get(position);
          timeView.setText(msg.getName());
                      
       return v;
	}


}
