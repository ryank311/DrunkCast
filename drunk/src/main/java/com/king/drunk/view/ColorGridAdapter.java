package com.king.drunk.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.king.drunk.R;

/**
 * Created by king on 2/17/14.
 */
public class ColorGridAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

	private static final int[] colors = {
			0xFFE31207,
			0xFF351BE8,
			0xFFFFC300,
			0xFF1AB242,
			0xFFFF8225,
			0xFFAE2CE3,
			0xFFE322B1,
			0xFF06CE94,
			0xFF33B5E5};

	private static final String[] colorNames = {
			"Red",
			"Blue",
			"Yellow",
			"Green",
			"Orange",
			"Purple",
			"Pink",
			"Teal"};

	private Activity parentActivity;
	private int previouslySelectedIndex = -1;
	private View previouslySelected = null;

	public ColorGridAdapter(Activity parentActivity) {
		this.parentActivity = parentActivity;
	}

	@Override
	public int getCount() {
		return 8;
	}

	@Override
	public Object getItem(int i) {
		return null;
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		View colorChooserItem = null;
		if(view == null) {
			colorChooserItem = LayoutInflater.from(parentActivity).inflate(
					R.layout.fragment_color_select, null);
		} else {
			colorChooserItem = view;
		}
		colorChooserItem.setBackgroundColor(colors[i]);
		TextView titleLabel = (TextView) colorChooserItem.findViewById(R.id.color_name);
		titleLabel.setText(colorNames[i]);
		return colorChooserItem;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		//Check for previously selected elements and deselect them.
		if(previouslySelected != null) {
			previouslySelected.setBackgroundColor(colors[previouslySelectedIndex]);
		}
		previouslySelected = view;
		previouslySelectedIndex = i;
		previouslySelected.setBackgroundColor(colors[8]);
	}

	public boolean validateSelection() {
		return previouslySelectedIndex >= 0;
	}

	public int getSelectedIndex() {
		return previouslySelectedIndex;
	}

	public int getColor() {
		if(previouslySelectedIndex < 0) return previouslySelectedIndex;
		return colors[previouslySelectedIndex];
	}
}
