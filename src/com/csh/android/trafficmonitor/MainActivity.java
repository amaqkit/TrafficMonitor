package com.csh.android.trafficmonitor;

import com.csh.android.trafficmonitor.TrafficMonitor.ITrafficMonitorCallback;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity implements ITrafficMonitorCallback {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TrafficMonitor monitor = new AllTrafficMonitor(getApplicationContext(), "com.gau.go.launcherex");
		monitor.setThreshold(3 * 60 * 1000, 200 * 1000);
		monitor.setTrafficMonitorCallback(this);
		monitor.start();
	}

	@Override
	public void onTrafficDataOverflow(long amount) {
		Log.w(TrafficMonitor.TAG, "onTrafficDataOverflow " + amount);
	}
}
