package pl.org.sendzimir.licznazielen.helper;

/**
 *
 * @author LeRafiK
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.org.sendzimir.licznazielen.R;
import pl.org.sendzimir.licznazielen.model.PleaceObject;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NearPleaceObjectListAdapter extends ArrayAdapter<PleaceObject> {

	private List<PleaceObject> alData;
	Context oContext;

	public NearPleaceObjectListAdapter(Context context, int textViewResourceId,
			List<PleaceObject> nearObjects) {
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
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) oContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item_near, null);
		}

		TextView timeView = (TextView) v.findViewById(R.id.licTextView);

		PleaceObject msg = alData.get(position);
		timeView.setText(msg.getName());

		timeView = (TextView) v.findViewById(R.id.licTextView2);
		timeView.setText(msg.getDistance() + " km");

		List<String> icons = msg.getIcons();
		Map<String, Integer> mp = new HashMap<String, Integer>();
		mp.put("1", R.id.imageView5);
		mp.put("2", R.id.imageView4);
		mp.put("3", R.id.imageView3);
		mp.put("4", R.id.imageView2);
		mp.put("5", R.id.imageRadar2);

		ImageView imgView;

		if (icons != null) {

			for (int i = 1; i < 6; i++) {
				imgView = (ImageView) v.findViewById(mp.get("" + i));

				if (icons.indexOf("" + i) == -1) {
					imgView.setVisibility(View.GONE);
				} else {
					imgView.setVisibility(View.VISIBLE);
				}

			}

		}

		return v;
	}

}
