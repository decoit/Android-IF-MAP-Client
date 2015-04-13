package de.simu.decoit.android.decomap.observer.camera;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
* observe use of camera
*
* @author Markus Schölzel, Decoit GmbH
*/
public class CameraReceiver extends BroadcastReceiver {
	public static Date sLastPictureTakenDate = null;

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		sLastPictureTakenDate = new Date();
	}
}
