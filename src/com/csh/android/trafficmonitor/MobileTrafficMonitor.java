package com.csh.android.trafficmonitor;

import android.content.Context;


/**
 * 移动网络监控器，只记录移动网络下的流量消耗
 * 
 * @author chenshihang
 *
 */
public class MobileTrafficMonitor extends TrafficMonitor{
	
	public MobileTrafficMonitor(Context context, String monitorPackage) {
		super(context, monitorPackage);
	}

	@Override
	public void onWifiActive() {
		pause();
	}

	@Override
	public void onMobileNewworkActive() {
		resume();
	}

	@Override
	public void onNetworkDown() {
		pause();
	}

	@Override
	public void start() {
		super.start();
		if (!Machine.isNetworkOK(mContext) || Machine.isWifiEnable(mContext)) {
			pause();
		}
	}

	@Override
	public boolean isExceptionalCase() {
		// TODO check download manager
		return false;
	}
	
}
