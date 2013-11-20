package com.dylan.whichard.tanknavigator;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.dylan.whichard.tanknavigator.SensorListenerService.TankDataListener;
import com.dylan.whichard.tanknavigator.TankControlProtocol.Command;
import com.dylan.whichard.tanknavigator.TankControlProtocol.DataType;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class SocketHandlerThread extends Thread implements TankDataListener {
	
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
	
	private Context context;
	
	private SensorListenerService sensorService;
	private boolean sensorBound;
	
	private boolean rot;
	private boolean gps;
	private boolean acc;
	
	private boolean firehose;
	
	private volatile float lastAccX, lastAccY, lastAccZ;
	private volatile float lastRotX, lastRotY, lastRotZ;
	private volatile float lastGpsLat, lastGpsLon, lastGpsElev;

	private Socket socket;
	
	private Scanner scan;
	private volatile PrintWriter writer;
	
	public SocketHandlerThread(Context context, Socket s) {
		this.context = context;
		this.socket = s;
		doBindService();
		sensorService.addListener(this);
	}
	
	public void run() {
		try {
			scan = new Scanner(socket.getInputStream());
			writer = new PrintWriter(socket.getOutputStream(), true);
			
			while (scan.hasNext()) {
				String tok = scan.next();
				try {
					Command cmd = Command.valueOf(tok);
					switch (cmd) {
					case ADD_TYPES:
						handleTypeArg(scan.next(), true);
						break;
					case CLOSE_FIREHOSE:
						break;
					case EXIT:
						writer.println("Goodbye!");
						scan.close();
						break;
					case HELP:
						break;
					case OPEN_FIREHOSE:
						firehose = true;
						break;
					case REMOVE_TYPES:
						handleTypeArg(scan.next(), false);
						break;
					case SINGLE_DATAGRAM:
						sendLast();
						break;
					}
				} catch (IllegalArgumentException e) {
					Log.d("SocketHandlerThread", "Invalid command received", e);
					writer.println(e.getMessage());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			scan.close();
			writer.close();
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			sensorService.removeListener(this);
			doUnbindService();
		}
	}
	
	private void handleTypeArg(String typeStr, boolean value) {
		try {
			DataType type = DataType.fromString(typeStr);
			switch(type) {
			case ACCELEROMETER:
				acc = value;
				break;
			case GPS:
				gps = value;
				break;
			case ROTATION_VECTOR:
				rot = value;
				break;
			}
		} catch (IllegalArgumentException e) {
			Log.d("SocketHandlerThread", "Invalid type received", e);
			writer.println(e.getMessage());
		}
	}
	
	private void sendLast() {
		if (acc) {
			sendData("ACC", lastAccX, lastAccY, lastAccZ);
		}
		
		if (rot) {
			sendData("ROT", lastRotX, lastRotY, lastRotZ);
		}
		
		if (gps) {
			sendData("GPS", lastGpsLat, lastGpsLon, lastGpsElev);
		}
	}
	
	private void sendData(String tag, float x, float y, float z) {
		writer.println(String.format("%s %f,%f,%f", tag, x, y, z));
	}

	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    context.bindService(new Intent(context, 
	            SensorListenerService.class), sensorConnection, Context.BIND_AUTO_CREATE);
	    context.bindService(new Intent(context, SocketListenerService.class), sensorConnection, Context.BIND_AUTO_CREATE);
	    sensorBound = true;
	}

	void doUnbindService() {
	    if (sensorBound) {
	        // Detach our existing connection.
	        context.unbindService(sensorConnection);
	        sensorBound = false;
	    }
	}

	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		if (firehose && acc) {
			sendData("ACC", x, y, z);
		}
		
		lastAccX = x;
		lastAccY = y;
		lastAccZ = z;
	}

	@Override
	public void onLocationChanged() {
		if (firehose && gps) {
			//sendData("GPS", lat, lon, elev);
		}
	}

	@Override
	public void onRotationChanged(float x, float y, float z) {
		if (firehose && rot) {
			sendData("ROT", x, y, z);
		}
		
		lastRotX = x;
		lastRotY = y;
		lastRotZ = z;
	}
}