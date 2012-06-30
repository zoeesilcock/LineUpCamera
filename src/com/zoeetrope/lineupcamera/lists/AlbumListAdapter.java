package com.zoeetrope.lineupcamera.lists;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.zoeetrope.lineupcamera.R;
import com.zoeetrope.lineupcamera.R.id;
import com.zoeetrope.lineupcamera.R.string;
import com.zoeetrope.lineupcamera.activities.LineUpCameraActivity;
import com.zoeetrope.lineupcamera.models.Album;
import com.zoeetrope.lineupcamera.models.Image;

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
		ImageView cameraButton = (ImageView) rowView
				.findViewById(R.id.cameraButton);
		ImageView thumbnailView = (ImageView) rowView
				.findViewById(R.id.thumbnail);

		Album album = mAlbums.get(position);
		Image image = album.getLatestImage();

		if (image != null) {
			String dateFormat = mContext.getResources().getString(
					R.string.dateformat);
			Date modificationDate = image.getModifiedDate();
			SimpleDateFormat df = new SimpleDateFormat(dateFormat);
			Bitmap bitmap = image.getThumbnail();

			if (bitmap != null) {
				BitmapDrawable thumbnail = new BitmapDrawable(
						mContext.getResources(), image.getThumbnail());

				thumbnail.setBounds(new Rect(0, 0, Math.round(THUMBNAIL_HEIGHT
						* image.getAspectRatio()), THUMBNAIL_HEIGHT));

				thumbnailView.setImageDrawable(thumbnail);
			}

			albumDate.setText(df.format(modificationDate));
		}

		albumName.setText(album.getName());

		cameraButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent();
				cameraIntent.setComponent(new ComponentName(mContext,
						LineUpCameraActivity.class));

				Bundle bundle = new Bundle();
				bundle.putString("ALBUM", mAlbums.get(position).getName());
				cameraIntent.putExtras(bundle);

				mContext.startActivity(cameraIntent);
			}
		});

		return rowView;
	}

}
