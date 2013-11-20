package com.dylan.whichard.tanknavigator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils.StringSplitter;
import android.util.Log;

public class SocketListenerService extends Service {
	public static boolean RUNNING = false;
	
	public class SocketListenerBinder extends Binder {
		SocketListenerService getService() {
			return SocketListenerService.this;
		}
	}
	
	private final IBinder binder = new SocketListenerBinder();
	
	private ServerSocket server;
	
	@Override
	public void onCreate() {
		RUNNING = true;
		
		Log.d("SocketListenerService", "Starting socket!");
		
		android.os.Debug.waitForDebugger();
		
		server = null;
		try {
			server = new ServerSocket(1337);
			while (true) {
				new SocketHandlerThread(getApplicationContext(), server.accept()).start();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onDestroy() {
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server = null;
		RUNNING = false;
	}
}
