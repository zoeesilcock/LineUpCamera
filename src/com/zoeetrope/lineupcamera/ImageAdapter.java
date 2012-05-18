package com.zoeetrope.lineupcamera;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zoeetrope.lineupcamera.model.Album;
import com.zoeetrope.lineupcamera.model.Image;

public class ImageAdapter extends BaseAdapter {

	private static int COLUMN_WIDTH_DP = 150;

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
		// Load the layout and find our gui elements.
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(mLayout, parent, false);
		TextView albumDate = (TextView) rowView.findViewById(R.id.imageDate);
		LinearLayout gridItem = (LinearLayout) rowView
				.findViewById(R.id.gridItem);

		// Load the thumbnail.
		Image image = mAlbum.getImages().get(position);
		Bitmap bitmap = image.getThumbnail();
		Drawable thumbnail = new BitmapDrawable(bitmap);

		// Calculate the height of the cell and apply it.
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		int cellWidth = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, COLUMN_WIDTH_DP, metrics));
		int cellHeight = Math.round(cellWidth / image.getAspectRatio());
		LayoutParams params = (LayoutParams) gridItem.getLayoutParams();

		params.height = cellHeight;

		// Format the date.
		String dateFormat = mContext.getResources().getString(
				R.string.dateformat);
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		String date = df.format(image.getModifiedDate());

		albumDate.setText(date);
		gridItem.setBackgroundDrawable(thumbnail);

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
