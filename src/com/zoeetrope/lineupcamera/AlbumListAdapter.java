package com.zoeetrope.lineupcamera;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AlbumListAdapter extends ArrayAdapter<Album> {
	
	private Context mContext;
	private ArrayList<Album> mAlbums;
	private int mLayout;

	public AlbumListAdapter(Context context, int textViewResourceId,
			List<Album> albums) {
		super(context, textViewResourceId, albums);
		
		this.mContext = context;
		this.mAlbums = (ArrayList<Album>) albums;
		this.mLayout = textViewResourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(mLayout, parent, false);
		TextView albumName = (TextView) rowView.findViewById(R.id.albumName);
		
		albumName.setText(mAlbums.get(position).getName());
		
		return rowView;
	}

}
