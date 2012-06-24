package com.zoeetrope.lineupcamera.lists;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zoeetrope.lineupcamera.R;
import com.zoeetrope.lineupcamera.R.id;
import com.zoeetrope.lineupcamera.model.Album;
import com.zoeetrope.lineupcamera.model.Image;

public class ImageGalleryAdapter extends BaseAdapter {

	private Album mAlbum;
	private Context mContext;
	private int mLayout;

	public ImageGalleryAdapter(Context context, int itewViewResourceId,
			Album album) {
		this.mContext = context;
		this.mAlbum = album;
		this.mLayout = itewViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Load the layout and find our gui elements.
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(mLayout, parent, false);
		ImageView imageView = (ImageView) itemView.findViewById(R.id.thumbnail);

		// Load the thumbnail.
		Image image = mAlbum.getImages().get(position);
		Bitmap bitmap = image.getThumbnail();

		if (bitmap != null) {
			Drawable thumbnail = new BitmapDrawable(mContext.getResources(),
					bitmap);
			imageView.setImageDrawable(thumbnail);
		}

		return itemView;
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
