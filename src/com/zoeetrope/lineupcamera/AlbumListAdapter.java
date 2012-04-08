package com.zoeetrope.lineupcamera;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumListAdapter extends ArrayAdapter<Album> {

	private static int THUMBNAIL_HEIGHT = 200;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(mLayout, parent, false);
		TextView albumName = (TextView) rowView.findViewById(R.id.albumName);
		TextView albumDate = (TextView) rowView.findViewById(R.id.albumDate);
		ImageView thumbnailView = (ImageView) rowView
				.findViewById(R.id.thumbnail);

		Album album = mAlbums.get(position);
		Image image = album.getLatestImage();
		BitmapDrawable thumbnail = new BitmapDrawable(image.getBitmap(200));

		thumbnail.setBounds(new Rect(0, 0, Math.round(THUMBNAIL_HEIGHT
				* image.getAspectRatio()), THUMBNAIL_HEIGHT));

		thumbnailView.setImageDrawable(thumbnail);
		albumName.setText(album.getName());
		albumDate.setText(image.getModifiedDate().toString());

		return rowView;
	}

}
