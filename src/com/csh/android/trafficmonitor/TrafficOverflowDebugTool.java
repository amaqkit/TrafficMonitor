package com.csh.android.trafficmonitor;

import com.csh.android.trafficmonitor.TrafficMonitor.OnTrafficOverflowListener;

import android.content.Context;
import android.util.Log;

public class TrafficOverflowDebugTool {

	public static void start(final Context context) {
		TrafficMonitor monitor = new AllTrafficMonitor(context.getApplicationContext(), "com.gau.go.launcherex");
		monitor.setThreshold(3 * 60 * 1000, 200 * 1000);
		monitor.setOnTrafficOverflowListener(new OnTrafficOverflowListener() {
			
			@Override
			public void onTrafficDataOverflow(long amount) {
				Log.w(TrafficMonitor.TAG, "onTrafficDataOverflow " + amount);
				if (HttpRequestLogger.getInstance().isRunning()) {
					return;
				}
				
				HttpRequestLogTask task = new HttpRequestLogTask(context);
				task.setInterval(1 * 30 * 1000).setTrafficAmount(amount).start();	
			}
		});
		monitor.start();
	}
	
	public void stop() {
		
	}

}
