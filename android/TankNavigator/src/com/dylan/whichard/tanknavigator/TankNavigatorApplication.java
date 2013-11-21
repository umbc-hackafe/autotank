package com.dylan.whichard.tanknavigator;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class TankNavigatorApplication extends Application {
	
	private SensorListenerService sensorService;
	private SocketListenerService socketService;
	
	private boolean sensorBound;
	private boolean socketBound;
	
	private ServiceConnection sensorConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        Log.d("TankNavigatorApplication", "Connecting to SensorListenerService");
	        sensorService = ((SensorListenerService.SensorListenerBinder)service).getService();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        sensorService = null;
	        Log.d("TankNavigatorApplication", "Disconnecting from SensorListenerService");
	    }
	};
	
	private ServiceConnection socketConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
	        socketService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
	        socketService = ((SocketListenerService.SocketListenerBinder)service).getService();
		}
	};
	
	public void onCreate() {
		doBindService();
	}

	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    bindService(new Intent(this, 
	            SensorListenerService.class), sensorConnection, Context.BIND_AUTO_CREATE);
	    bindService(new Intent(this, SocketListenerService.class), socketConnection, Context.BIND_AUTO_CREATE);
	    sensorBound = true;
	}

	void doUnbindService() {
	    if (sensorBound) {
	        // Detach our existing connection.
	        unbindService(sensorConnection);
	        sensorBound = false;
	    }
	}
}
