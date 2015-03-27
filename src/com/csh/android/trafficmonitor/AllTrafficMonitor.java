package com.csh.android.trafficmonitor;

import android.content.Context;

/**
 * 记录所有流量消耗
 * 
 * @author chenshihang
 * 
 */
public class AllTrafficMonitor extends TrafficMonitor {

	public AllTrafficMonitor(Context context, String monitorPackage) {
		super(context, monitorPackage);
	}

	@Override
	public void onNetworkActive() {
		resume();
	}

	@Override
	public void onNetworkDown() {
		pause();
	}

	@Override
	public boolean isExceptionalCase() {
		return false;
	}

	
}
