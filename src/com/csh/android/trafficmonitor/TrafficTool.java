package com.csh.android.trafficmonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.net.TrafficStats;
import android.util.Log;

/**
 * 获取流量工具
 * 
 * @author chenshihang
 */
public class TrafficTool {
	
	public static long computeIncrease(HashMap<Integer, Long[]> old, HashMap<Integer, Long[]> now) {
		long total = 0;
		if (old != null && now != null) {
			Set<Integer> nowSet = now.keySet();
			for (Integer integer : nowSet) {
				if (old.containsKey(integer)) {
					long nowData = now.get(integer)[0] + now.get(integer)[1];
					long oldData = old.get(integer)[0] + old.get(integer)[1];
					if (nowData < oldData) {
						Log.e(TrafficMonitor.TAG, "traffic exception, decreased " + oldData + "/" + nowData);
					} else {
						total += nowData - oldData;
					}
				}
			}
		}
		return total;
	}

	public static HashMap<Integer, Long[]> getTrafficByPackage(Context context, String pkgName) {
		Long[] traffic = {0l, 0l};
		HashMap<Integer, Long[]> result = new HashMap<Integer, Long[]>();
		ArrayList<Integer> processUids = getProcessUidsByPackage(context, pkgName);
		if (processUids != null) {
			final int size = processUids.size();
			for (int i = 0; i < size; i++) {
				long rx = TrafficStats.getUidRxBytes(processUids.get(i));
				long tx = TrafficStats.getUidTxBytes(processUids.get(i));
				traffic[0] = rx;
				traffic[1] = tx;
				result.put(processUids.get(i), traffic);
			}
		}
		return result;
	}
	
	public static ArrayList<Integer> getProcessUidsByPackage(Context context, String pkgName) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcessInfos = activityManager.getRunningAppProcesses();
		if (runningAppProcessInfos != null) {
			ArrayList<Integer> uidList = new ArrayList<Integer>();
			final int size = runningAppProcessInfos.size();
			for (int i = 0; i < size; i++) {
				RunningAppProcessInfo info = runningAppProcessInfos.get(i);
				final String pkg = info.pkgList[0];
				if (pkg != null && pkg.equals(pkgName)) {
					uidList.add(info.uid);
				}
			}
			return uidList;
		}
		return null;
	}
}
