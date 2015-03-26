package com.csh.android.trafficmonitor;

import java.util.HashMap;

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
	public void checkTraffic(boolean ignore) {
		HashMap<Integer, Long[]> trafficData = TrafficTool.getTrafficByPackage(
				mContext, mMonitorPackage);

		if (!ignore) {
			if (mLastCheckTrafficCount == null) {
				addTrafficRecord(0);
			} else {
				long increase = TrafficTool.computeIncrease(
						mLastCheckTrafficCount, trafficData);
				addTrafficRecord(increase);
			}
		}

		mLastCheckTrafficCount = trafficData;
	}

}
