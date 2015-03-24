package com.csh.android.trafficmonitor;

import com.csh.android.trafficmonitor.NetworkMonitorManager.INetworkChangeLisener;

import android.content.Context;

public class TrafficMonitor implements INetworkChangeLisener {

	private Context mContext;
	
	public TrafficMonitor(Context context) {
		mContext = context;
		NetworkMonitorManager.getInstance(mContext).registeNetworkChangeLisener(this);
	}

	@Override
	public void onNetworkConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkDisConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWifiConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWifiDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGPRSConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGPRSDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	
}
