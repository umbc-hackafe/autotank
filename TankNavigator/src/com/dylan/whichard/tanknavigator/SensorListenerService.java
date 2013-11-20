package com.dylan.whichard.tanknavigator;

import java.util.HashSet;
import java.util.Set;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SensorListenerService extends Service implements SensorEventListener {
	public static boolean RUNNING = false;
	
	public interface TankDataListener {
		public void onAccelerationChanged(float x, float y, float z);
		public void onLocationChanged();	// FIXME
		public void onRotationChanged(float x, float y, float z);
	}
	
	public class SensorListenerBinder extends Binder {
		SensorListenerService getService() {
			return SensorListenerService.this;
		}
	}
	private SensorManager sensorManager;
	
	// the ADR3600 has these sensors:
	// * Light (not really useful)
	// * Proximity (not really useful)
	// * Accelerometer
	// * Compass
	
	private Sensor accelerometer;
	private Sensor compass;
	private final IBinder binder = new SensorListenerBinder();
	
	private Set<TankDataListener> listeners;
	
	@Override
	public void onCreate() {
		RUNNING = true;
		
		android.os.Debug.waitForDebugger();
		
		listeners = new HashSet<TankDataListener>();
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		compass = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.d("SensorListener", "Sensor " + sensor.getName() + " accuracy changed to" + accuracy);
	}
	
	@Override
	public void onDestroy() {
		RUNNING = false;
		sensorManager.unregisterListener(this);
	}
	
	public void addListener(TankDataListener l) {
		listeners.add(l);
	}
	
	public void removeListener(TankDataListener l) {
		listeners.remove(l);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch(event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			for (TankDataListener l : listeners) {
				l.onAccelerationChanged(event.values[0], event.values[1], event.values[2]);
			}
			Log.d("SensorListenerService", String.format("Accelerometer: [%.2f, %.2f, %.2f]", event.values[0], event.values[1], event.values[2]));
			break;
		case Sensor.TYPE_ROTATION_VECTOR:
			for (TankDataListener l : listeners) {
				l.onRotationChanged(event.values[0], event.values[1], event.values[2]);
			}
			Log.d("SensorListenerService", String.format("Rotation: <%.3f, %.3f, %.3f>", event.values[0], event.values[1], event.values[2]));
			break;
		}
	}
}
