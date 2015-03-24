package com.csh.android.trafficmonitor;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 网络监听管理器，提供注册或注销监听网络状态（wifi、gprs），当状态发生变化时通过{@link INetworkChangeLisener}回调。
 * 网络变化时系统会发出多个广播，包含不同的细节状态，这里只关心关键状态。
 * 
 * @author chenshihang
 *
 */
public class NetworkMonitorManager {

	private static NetworkMonitorManager sInstance;
	
	private Context mContext;
	
	private ArrayList<INetworkChangeLisener> mLiseners;
	
	private NetworkMonitorManager(Context context) {
		mContext = context;
		mLiseners = new ArrayList<NetworkMonitorManager.INetworkChangeLisener>();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(new NetworkReceiver(), filter);
	}
	
	public synchronized static NetworkMonitorManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new NetworkMonitorManager(context);
		}
		return sInstance;
	}
	
	/**
	 * 注册网络变化监听
	 * 
	 * @param lisener
	 * @return true 成功注册， false 已经注册过
	 */
	public synchronized boolean registeNetworkChangeLisener(INetworkChangeLisener lisener) {
		final int size = mLiseners.size();
		for (int i = 0; i < size; i++) {
			if (mLiseners.get(i) == mLiseners) {
				return false;
			}
		}
		return mLiseners.add(lisener);
	}
	
	public synchronized boolean removeNetworkChangeLisener(INetworkChangeLisener lisener) {
		return mLiseners.remove(lisener);
	}
	
	/**
	 * 网络监听
	 * 
	 * @author chenshihang
	 *
	 */
	class NetworkReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {

			Log.i("CSH", "----------------------------");
			Log.i("CSH", "----------------------------");
			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] infos = connManager.getAllNetworkInfo();
			if (infos != null && infos.length > 0) {
				int length = infos.length;
				for (int i = 0; i < length; i++) {
					NetworkInfo info = infos[i];
					Log.i("CSH", "info : " + info.getTypeName() + "/" + info.isConnected() + "/" + info.getDetailedState() + "/" );
				}
			}
			Log.i("CSH", "----------------------------");
			NetworkInfo info = connManager.getActiveNetworkInfo();
			Log.i("CSH", "info : " + info.getTypeName() + "/" + info.isConnected() + "/" + info.getDetailedState());
		}
	}
	
	/**
	 * 
	 * @author chenshihang
	 *
	 */
	public interface INetworkChangeLisener {
		void onNetworkConnected();
		void onNetworkDisConnected();
		void onWifiConnected();
		void onWifiDisconnected();
		void onGPRSConnected();
		void onGPRSDisconnected();
	}
}
