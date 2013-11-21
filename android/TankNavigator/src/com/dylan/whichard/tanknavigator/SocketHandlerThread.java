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
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

public class SocketHandlerThread extends Thread implements TankDataListener {
	private static final int MS_IN_NS = 1000000;
	
	private ServiceConnection sensorConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        Log.d("TankNavigatorApplication", "Connecting to SensorListenerService");
	        sensorService = ((SensorListenerService.SensorListenerBinder)service).getService();
	        sensorService.addListener(SocketHandlerThread.this);
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	    	sensorService.removeListener(SocketHandlerThread.this);
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
	
	private long delay;
	private long lastGps;
	private long lastAcc;
	private long lastRot;
	
	private volatile float lastAccX, lastAccY, lastAccZ;
	private volatile float lastRotX, lastRotY, lastRotZ;
	private volatile Location lastLoc;

	private Socket socket;
	
	private Scanner scan;
	private volatile PrintWriter writer;
	
	public SocketHandlerThread(Context context, Socket s) {
		this.context = context;
		this.socket = s;
		doBindService();
	}
	
	public void run() {
		try {
			scan = new Scanner(socket.getInputStream());
			writer = new PrintWriter(socket.getOutputStream(), true);
			
			while (!socket.isClosed() && scan.hasNext()) {
				String tok = scan.next();
				try {
					Command cmd = Command.fromString(tok);
					switch (cmd) {
					case ADD_TYPES:
						handleTypeArg(scan.next(), true);
						break;
					case CLOSE_FIREHOSE:
						firehose = false;
						break;
					case EXIT:
						writer.println("KTHXBAI");
						socket.shutdownInput();
						socket.shutdownOutput();
						socket.close();
						break;
					case HELP:
						for (Command c : Command.values()) {
							writer.println(String.format("%s %s", c.commandString, c.helpString));
						}
						
						writer.println("------");
						writer.println("Return Data Format:");
						writer.println("ACC x,y,z (as floats)");
						writer.println("ROT x,y,z (as floats)");
						writer.println("GPS lat,lon,alt,bear,speed,acc,time");
						writer.println("  (time is a long, in ms; others are floats)");
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
					case RATE_LIMIT:
						if (!scan.hasNextLong()) {
							writer.write("Invalid delay specified");
						} else {
							delay = scan.nextLong() * MS_IN_NS;
						}
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
		
		if (gps && lastLoc != null) {
			sendLocation(lastLoc);
		}
	}
	
	private void sendLocation(Location l) {
		// lat
		// lon
		// elevation/altitude
		// bearing
		// speed
		// accuracy
		// time (ms)
		
		writer.println(String.format("GPS %f,%f,%f,%f,%f,%f,%d",l.getLatitude(), l.getLongitude(), l.getAltitude(),
				l.getBearing(), l.getSpeed(), l.getAccuracy(), l.getTime()));
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
		if (firehose && acc && (delay == 0 || System.nanoTime() - lastAcc >= delay)) {
			sendData("ACC", x, y, z);
		}
		
		lastAccX = x;
		lastAccY = y;
		lastAccZ = z;
		
		if (delay > 0) lastAcc = System.nanoTime();
	}

	@Override
	public void onLocationChanged(Location l) {
		if (firehose && gps && (delay == 0 || System.nanoTime() - lastGps >= delay)) {
			sendLocation(l);
		}
		
		lastLoc = l;
		if (delay > 0) lastGps = System.nanoTime();
	}

	@Override
	public void onRotationChanged(float x, float y, float z) {
		if (firehose && rot && (delay == 0 || System.nanoTime() - lastRot >= delay)) {
			sendData("ROT", x, y, z);
		}
		
		lastRotX = x;
		lastRotY = y;
		lastRotZ = z;
		if (delay > 0) lastRot = System.nanoTime();
	}
	
	public long getRateLimit(TankControlProtocol.DataType t) {
		return delay;
	}
}