package com.zoeetrope.lineupcamera.models;

import java.util.ArrayList;
import java.util.Date;

import com.zoeetrope.lineupcamera.daos.ImageDAO;

public class Album {

	public static final int MEDIA_TYPE_IMAGE = 1;

	private ArrayList<Image> mImages;

	public Date mLastModificationDate;
	private String mName;

	public Album(String name) {
		mName = name;

		loadImages();
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;

		loadImages();
	}

	public Date getLastModifiedDate() {
		return mLastModificationDate;
	}

	public ArrayList<Image> getImages() {
		return mImages;
	}

	public void add(Image image) {
		mImages.add(image);
	}

	public Image getLatestImage() {
		if (mImages.size() > 0) {
			return mImages.get(mImages.size() - 1);
		} else {
			return null;
		}
	}

	public void loadImages() {
		ImageDAO imageDAO = new ImageDAO();
		mImages = imageDAO.getAll(mName);
	}

	public void remove(int position) {
		ImageDAO imageDAO = new ImageDAO();
		imageDAO.remove(mImages.get(position));

		mImages.remove(position);
	}

}
