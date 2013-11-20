package com.dylan.whichard.tanknavigator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	public BootReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startServiceIntent = new Intent(context, SensorListenerService.class);
		context.startService(startServiceIntent);
		
		Intent startSocketIntent = new Intent(context, SocketListenerService.class);
		context.startService(startSocketIntent);
		Log.d("BootReceiver", "Receiving the boot! Starting the service.");
	}
}
