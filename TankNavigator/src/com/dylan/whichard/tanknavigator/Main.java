package com.dylan.whichard.tanknavigator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class Main extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ToggleButton b = (ToggleButton) findViewById(R.id.sensorServiceToggle);
		b.setChecked(SensorListenerService.RUNNING);
		b.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (SensorListenerService.RUNNING != isChecked) {
					Intent startServiceIntent = new Intent(getApplicationContext(), SensorListenerService.class);
					getApplicationContext().startService(new Intent(getApplicationContext(), SocketListenerService.class));
					if (isChecked) {
						getApplicationContext().startService(startServiceIntent);
					} else {
						getApplicationContext().stopService(startServiceIntent);
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
