package deu.sw.greenhouse.beacon;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aprilbrother.aprilbrothersdk.Beacon;

import deu.sw.greenhouse.R;

public class BeaconAdapter extends BaseAdapter {


	private ArrayList<Beacon> beacons;
	private LayoutInflater inflater;

	public BeaconAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	    this.beacons = new ArrayList<Beacon>();
	}
	
	public void replaceWith(Collection<Beacon> newBeacons) {
	    this.beacons.clear();
	    this.beacons.addAll(newBeacons);
	    notifyDataSetChanged();
	  }

	@Override
	  public int getCount() {
	    return beacons.size();
	  }

	  @Override
	  public Beacon getItem(int position) {
	    return beacons.get(position);
	  }

	  @Override
	  public long getItemId(int position) {
	    return position;
	  }

	  @Override
	  public View getView(int position, View view, ViewGroup parent) {
	    view = inflateIfRequired(view, position, parent);
	    bind(getItem(position), view);
	    return view;
	  }

	  
	  private void bind(Beacon beacon, View view) {
	    ViewHolder holder = (ViewHolder) view.getTag();
	    holder.macTextView.setText(String.format("장치 %d     거리 : %.2fm", beacons.indexOf(beacon)+1, beacon.getDistance()));
	    holder.uuidTextView.setText("장치 ID: "+ beacon.getProximityUUID());
	    holder.rssiTextView.setText("신호세기: " + beacon.getRssi());
	  }

	  private View inflateIfRequired(View view, int position, ViewGroup parent) {
	    if (view == null) {
	      view = inflater.inflate(R.layout.beacondevice_item, null);
	      view.setTag(new ViewHolder(view));
	    }
	    return view;
	  }

	  static class ViewHolder {
	    final TextView macTextView;
	    final TextView uuidTextView;
	    final TextView rssiTextView;

	    ViewHolder(View view) {
	      macTextView = (TextView) view.findViewWithTag("mac");
	      uuidTextView = (TextView) view.findViewWithTag("uuid");
	      rssiTextView = (TextView) view.findViewWithTag("rssi");
	    }
	  }
}
