package com.zoeetrope.lineupcamera;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(mLayout, parent, false);
		TextView albumName = (TextView) rowView.findViewById(R.id.albumName);
		TextView albumDate = (TextView) rowView.findViewById(R.id.albumDate);
		ImageView thumbnailView = (ImageView) rowView
				.findViewById(R.id.thumbnail);

		Album album = mAlbums.get(position);
		Date lastModification = album.getLastModifiedDate();
		Bitmap image = album.getLatestImage(200);
		BitmapDrawable thumbnail = new BitmapDrawable(image);
		float aspectRatio = (float) image.getWidth()
				/ (float) image.getHeight();

		thumbnail.setBounds(new Rect(0, 0, Math.round(THUMBNAIL_HEIGHT
				* aspectRatio), THUMBNAIL_HEIGHT));

		thumbnailView.setImageDrawable(thumbnail);
		albumName.setText(album.getName());
		albumDate.setText(lastModification.toString());

		return rowView;
	}

}
