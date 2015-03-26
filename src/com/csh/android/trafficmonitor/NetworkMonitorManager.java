package com.csh.android.trafficmonitor;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 网络监听管理器，提供注册或注销监听网络状态（wifi、gprs），当状态发生变化时通过{@link INetworkChangeLisener}回调。
 * 移动设备网络变化有很多路径，这里只关心关键状态——wifi是否连接、是否使用移动网络、网络是否ok。
 * 约定有优先级，当从wifi切换到移动网络时，只会发出移动网络连接建立的通知。当wifi连接正常时，移动网络发生变化的事件也会直接忽略。
 * 
 * @author chenshihang
 *
 */
public class NetworkMonitorManager {
	
	public static boolean sDebug = true;
	
	public static final String TAG = "NetworkMonitorManager";
	
	/**
	 * 无网络时的状态，类似{@link ConnectivityManager #Type_NONE}，但该值是hide，所以在此处重新定义一个
	 */
	private static final int NETWORK_TYPE_NONE = -1;
	
	private static final int STATE_WIFI_ACTIVE = 1;
	
	private static final int STATE_MOBILE_NETWORK_ACTIVE = 2;
	
	private static final int STATE_NETWORK_ACTIVE = 3;
	
	private static final int STATE_NETWORK_DOWN = 4;
	
	private static NetworkMonitorManager sInstance;
	
	private Context mContext;
	
	private ArrayList<INetworkChangeLisener> mListeners;
	
	private int mLastType = -1;
	
	private State mLastState = NetworkInfo.State.UNKNOWN;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			if (mListeners != null) {
				final int size = mListeners.size();
				for (int i = 0; i < size; i++) {
					switch (msg.what) {
					case STATE_WIFI_ACTIVE:
						mListeners.get(i).onWifiActive();
						break;
					case STATE_MOBILE_NETWORK_ACTIVE:
						mListeners.get(i).onMobileNewworkActive();
						break;
					case STATE_NETWORK_ACTIVE:
						mListeners.get(i).onNetworkActive();
						break;
					case STATE_NETWORK_DOWN:
						mListeners.get(i).onNetworkDown();
						break;
					default:
						break;
					}
				}
			}
		}
		
	};
	
	private NetworkMonitorManager(Context context) {
		mContext = context;
		mListeners = new ArrayList<NetworkMonitorManager.INetworkChangeLisener>();
		
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
		final int size = mListeners.size();
		for (int i = 0; i < size; i++) {
			if (mListeners.get(i) == mListeners) {
				return false;
			}
		}
		return mListeners.add(lisener);
	}
	
	public synchronized boolean removeNetworkChangeLisener(INetworkChangeLisener lisener) {
		return mListeners.remove(lisener);
	}
	
	private void performWifiActive() {
		mHandler.sendEmptyMessage(STATE_WIFI_ACTIVE);
	}
	
	private void performMobileNetworkActive() {
		mHandler.sendEmptyMessage(STATE_MOBILE_NETWORK_ACTIVE);
	}
	
	private void performNetworkActive() {
		mHandler.sendEmptyMessage(STATE_NETWORK_ACTIVE);
	}
	
	private void performNetworkDown() {
		mHandler.sendEmptyMessage(STATE_NETWORK_DOWN);
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

			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connManager.getActiveNetworkInfo();
			if (info != null) {
				if (sDebug) {
					Log.i(TAG, "active info : " + info.getTypeName() + "/" + info.isConnected() + "/" + info.getDetailedState());
				}
				final int type = info.getType();
				final State state = info.getState();
				if (info.isAvailable() && info.isConnected()) {
					if (mLastType == NETWORK_TYPE_NONE) {
						performNetworkActive();
					}
					if (type != mLastType) {
						//type changed
						switch (type) {
						case ConnectivityManager.TYPE_MOBILE:
							performMobileNetworkActive();
							break;
						case ConnectivityManager.TYPE_WIFI:
							performWifiActive();
							break;
						default:
							break;
						}
					}
				} // ignore else， 似乎网络只要正常，总是会在isConnecte的状态下发出广播的
				mLastType = type;
				mLastState = state;
			} else {
				//no network
				if (mLastType != NETWORK_TYPE_NONE) {
					performNetworkDown();
					mLastType = NETWORK_TYPE_NONE;
				}
			}
		}
	}
	
	/**
	 * 网络变化接口
	 * 
	 * @author chenshihang
	 */
	public interface INetworkChangeLisener {
		
		/**
		 * wifi连接OK
		 */
		void onWifiActive();
		
		/**
		 * 移动网络连接OK，wifi断开时触发
		 */
		void onMobileNewworkActive();
		
		/**
		 * 网络OK，与{@link #onWifiActive()} and/or {@link #onMobileNewworkActive()}同时触发，并且此接口首先调用
		 */
		void onNetworkActive();
		
		/**
		 * 无网络
		 */
		void onNetworkDown();
	}

}
