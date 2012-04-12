package com.zoeetrope.lineupcamera;

import java.text.DateFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zoeetrope.lineupcamera.model.Album;
import com.zoeetrope.lineupcamera.model.Image;

public class ImageAdapter extends BaseAdapter {

	private static int THUMBNAIL_HEIGHT = 200;

	private Album mAlbum;
	private Context mContext;
	private int mLayout;

	public ImageAdapter(Context context, int itemViewResourceId, Album album) {
		this.mContext = context;
		this.mAlbum = album;
		this.mLayout = itemViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(mLayout, parent, false);
		TextView albumDate = (TextView) rowView.findViewById(R.id.imageDate);
		Image image = mAlbum.getImages().get(position);
		Bitmap bitmap = image.getBitmap(200);
		Drawable thumbnail = new BitmapDrawable(bitmap);

		thumbnail.setBounds(new Rect(0, 0, Math.round(THUMBNAIL_HEIGHT
				* image.getAspectRatio()), THUMBNAIL_HEIGHT));

		String date = DateFormat.getDateInstance().format(
				image.getModifiedDate());
		albumDate.setText(date);
		albumDate.setCompoundDrawables(null, thumbnail, null, null);

		return rowView;
	}

	@Override
	public int getCount() {
		return mAlbum.getImages().size();
	}

	@Override
	public Object getItem(int position) {
		return mAlbum.getImages().get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
