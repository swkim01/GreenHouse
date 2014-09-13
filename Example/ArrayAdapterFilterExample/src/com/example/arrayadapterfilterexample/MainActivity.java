package com.example.arrayadapterfilterexample;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class MainActivity extends ListActivity {

	private List<String> list = new ArrayList<String>();
	List<String> mOriginalValues;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final MyAdapter adapter = new MyAdapter(this, getModel());
		setListAdapter(adapter);

		EditText filterEditText = (EditText) findViewById(R.id.filterText);

		// Add Text Change Listener to EditText
		filterEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Call back the Adapter with current character to Filter
				adapter.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private List<String> getModel() {
		list.add("영주");
		list.add("서울");
		list.add("대구");
		list.add("부산");
		list.add("인천");
		list.add("청도");
		list.add("제주");
		list.add("남양주");
		list.add("부천");
		return list;
	}

	// Adapter Class
	public class MyAdapter extends BaseAdapter implements Filterable {

		List<String> arrayList;
		List<String> mOriginalValues; // Original Values
		LayoutInflater inflater;

		public MyAdapter(Context context, List<String> arrayList) {
			this.arrayList = arrayList;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			TextView textView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null) {

				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.row, null);
				holder.textView = (TextView) convertView
						.findViewById(R.id.textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(arrayList.get(position));
			return convertView;
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {

					arrayList = (List<String>) results.values; // has the
																// filtered
																// values
					notifyDataSetChanged(); // notifies the data with new
											// filtered values
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults(); // Holds the
																	// results
																	// of a
																	// filtering
																	// operation
																	// in values
					List<String> FilteredArrList = new ArrayList<String>();

					if (mOriginalValues == null) {
						mOriginalValues = new ArrayList<String>(arrayList); // saves
																			// the
																			// original
																			// data
																			// in
																			// mOriginalValues
					}

					/********
					 * 
					 * If constraint(CharSequence that is received) is null
					 * returns the mOriginalValues(Original) values else does
					 * the Filtering and returns FilteredArrList(Filtered)
					 * 
					 ********/
					if (constraint == null || constraint.length() == 0) {

						// set the Original result to return
						results.count = mOriginalValues.size();
						results.values = mOriginalValues;
					} else {
						constraint = constraint.toString().toLowerCase();
						for (int i = 0; i < mOriginalValues.size(); i++) {
							String data = mOriginalValues.get(i);
							if (data.toLowerCase().startsWith(
									constraint.toString())) {
								FilteredArrList.add(data);
							}
						}
						// set the Filtered result to return
						results.count = FilteredArrList.size();
						results.values = FilteredArrList;
					}
					return results;
				}
			};
			return filter;
		}
	}
}