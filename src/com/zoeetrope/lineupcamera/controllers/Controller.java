package com.zoeetrope.lineupcamera.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Handler;
import android.os.Message;

public abstract class Controller {

	private final List<Handler> mHandlers = new ArrayList<Handler>();

	public abstract boolean handle(int what, HashMap<String, Object> params);

	public boolean handle(int what) {
		return handle(what, null);
	}

	public final void addOutboxHandler(Handler handler) {
		mHandlers.add(handler);
	}

	public final void removeOutboxHandler(Handler handler) {
		mHandlers.remove(handler);
	}

	protected final void notifyOutboxHandlers(int what, int arg1, int arg2,
			Object obj) {
		if (!mHandlers.isEmpty()) {
			for (Handler handler : mHandlers) {
				Message msg = Message.obtain(handler, what, arg1, arg2, obj);
				msg.sendToTarget();
			}
		}
	}

}
