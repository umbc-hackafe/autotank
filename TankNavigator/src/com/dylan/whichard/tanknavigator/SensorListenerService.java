package com.dylan.whichard.tanknavigator;

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
	
	@Override
	public void onCreate() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		compass = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		
		
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_GAME);
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
	public void onSensorChanged(SensorEvent event) {
		Log.d("SensorListener", "Sensor event from " + event.sensor.getName());
	}
}
