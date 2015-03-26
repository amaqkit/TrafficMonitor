package com.csh.android.trafficmonitor;

import java.util.ArrayList;
import java.util.HashMap;

import com.csh.android.trafficmonitor.NetworkMonitorManager.INetworkChangeLisener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 监控流量工具模板类
 * 
 * @author chenshihang
 *
 */
public abstract class TrafficMonitor implements INetworkChangeLisener {
	
	public static boolean sDebug = true;
	
	public static final String TAG = "TrafficMonitor";

	private static final int MSG_CHECK_TRAFFIC = 1;
	
	/**
	 * 默认检查流量时间为5min
	 */
	private static final long DEFAULT_CHECK_INTERVAL = 5 * 60 * 1000;
	
	protected Context mContext;
	
	private ITrafficMonitorCallback mTrafficMonitorCallback;
	
	/**
	 * 检查间隔时间
	 */
	private long mCheckInterval = DEFAULT_CHECK_INTERVAL;

	/**
	 * 监控的应用包名
	 */
	protected String mMonitorPackage;
	
	/**
	 * 阈值作用时间
	 */
	private long mInterval = 0;
	
	/**
	 * 设定的阈值
	 */
	private int mThreshold = 0;
	
	/**
	 * 当前记录的数量
	 */
	protected ArrayList<TrafficRecord> mTrafficRecordList;
	
	/**
	 * 暂停时记录已经消耗的时间
	 */
	private long mUsedTime;
	
	private long mRecordStartTime;
	
	private long mRecordEndTime;
	
//	protected long mLastCheckTrafficCount;
	
	protected HashMap<Integer, Long[]> mLastCheckTrafficCount;
	
	private boolean mHasStart = false;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CHECK_TRAFFIC:
				mRecordEndTime = System.currentTimeMillis();
				checkTraffic(false);
				mRecordStartTime = System.currentTimeMillis();
				scheduleNextCheck(mCheckInterval);
				break;
			default:
				break;
			}
		}
		
	};

	public TrafficMonitor(Context context, String monitorPackage) {
		mContext = context;
		mMonitorPackage = monitorPackage;
		mTrafficRecordList = new ArrayList<TrafficRecord>();
		NetworkMonitorManager networkMonitorManager = NetworkMonitorManager.getInstance(mContext);
		networkMonitorManager.registeNetworkChangeLisener(this);
		
		if (sDebug) {
			mCheckInterval = 10 * 1000;
		}
	}
	
	public void setTrafficMonitorCallback(ITrafficMonitorCallback callback) {
		mTrafficMonitorCallback = callback;
	}
	
	/**
	 * 设定预警值
	 * 
	 * @param interval 时间间隔，单位为s
	 * @param threshole 阈值，单位为KB
	 */
	public void setThreshold(long intervalByMilleconds, int thresholdByByte) {
		if (intervalByMilleconds < 0 || thresholdByByte < 0) {
			throw new IllegalArgumentException("interval and threshold must be positive!");
		}
		if (mHasStart) {
			throw new IllegalStateException("monitor has started!");
		}
		mInterval = intervalByMilleconds;
		mThreshold = thresholdByByte;
	}
	
	/**
	 * 添加一条流量记录，自动删除时间最旧的一条数据
	 * 
	 * @param traffic
	 */
	protected void addTrafficRecord(long traffic) {
		TrafficRecord record = new TrafficRecord();
		record.setStartTime(mRecordStartTime);
		record.setEndTime(mRecordEndTime);
		record.setTrafficCount(traffic);
		mTrafficRecordList.add(0, record);

		removeExtraRecord();
		
		if (sDebug) {
			Log.i(TAG, "add record " + record);
		}
		
		checkThreshold();
	}
	
	/**
	 * 清除超出时间范围的记录
	 */
	private void removeExtraRecord() {
		final int size = mTrafficRecordList.size();
		long totalTime = 0;
		int flag = -1;
		for (int i = 0; i < size; i++) {
			if (flag != -1) {
				mTrafficRecordList.remove(flag);
			} else {
				TrafficRecord record = mTrafficRecordList.get(i);
				totalTime += record.getInterval();
				if (totalTime >= mInterval) {
					flag = i + 1;
				}
			}
		}
	}
	
	private void checkThreshold() {
		final int size = mTrafficRecordList.size();
		long total = 0;
		for (int i = 0; i < size; i++) {
			TrafficRecord record = mTrafficRecordList.get(i);
			total += record.getTrafficCount();
		}
		
		if (sDebug) {
			Log.i(TAG, "total traffic " + total);
		}
		
		if (total >= mThreshold) {
			clearTrafficRecords();
			if (mTrafficMonitorCallback != null) {
				mTrafficMonitorCallback.onTrafficDataOverflow(total);
			}
		}
	}
	
	private void clearTrafficRecords() {
		mTrafficRecordList.clear();
	}
	
	/**
	 * 设置检查流量的间隔时间，注意：除非你很清楚要做什么，否则不要设置此值
	 * 
	 * @param checkInterval
	 */
	public void setCheckInterval(long checkInterval) {
		mCheckInterval = checkInterval;
	}
	
	protected void scheduleNextCheck(long delay) {
		if (sDebug) {
			Log.i(TAG, "next check " + delay);
		}
		mHandler.removeMessages(MSG_CHECK_TRAFFIC);
		mHandler.sendEmptyMessageDelayed(MSG_CHECK_TRAFFIC, delay);
	}
	
	public void start() {
		if (mInterval == 0 || mThreshold == 0) {
			throw new IllegalStateException("interval or threshold does not initialized!");
		}
		mHasStart = true;
		checkTraffic(false);
		mRecordStartTime = System.currentTimeMillis();
		scheduleNextCheck(mCheckInterval);
	}
	
	public void pause() {
		if (sDebug) {
			Log.i(TAG, "pause");
		}
		mHandler.removeMessages(MSG_CHECK_TRAFFIC);
		long tmp = System.currentTimeMillis() - mRecordStartTime;
		if (tmp < mCheckInterval) {
			mUsedTime = tmp;
		} else {
			/* 如果在运行的过程中有修改{@link $mCheckInterval}，是可能走到此分支的 */
			mUsedTime = mCheckInterval; 
		}
		mRecordEndTime = System.currentTimeMillis();
		checkTraffic(false);
	}
	
	public void resume() {
		if (sDebug) {
			Log.i(TAG, "resume");
		}
		checkTraffic(true);
		long delay = mCheckInterval - mUsedTime;
		if (delay > 0) {
			scheduleNextCheck(delay);
		} else {
			scheduleNextCheck(0);
		}
		mRecordStartTime = System.currentTimeMillis();
	}
	
	public void stop() {
		
	}
	
	/**
	 * 检查一次流量消耗，将新增部分添加到记录
	 * 
	 * @param ignore true 忽略本条记录， false 添加本条记录
	 */
	public abstract void checkTraffic(boolean ignore);

	@Override
	public void onWifiActive() {
		Log.i("CSH", "onWifiActive");
	}

	@Override
	public void onMobileNewworkActive() {
		Log.i("CSH", "onMobileNewworkActive");
	}

	@Override
	public void onNetworkActive() {
		Log.i("CSH","onNetworkActive");
	}

	@Override
	public void onNetworkDown() {
		Log.i("CSH", "onNetworkDown");
	}

	/**
	 * 流量监听回调
	 * 
	 * @author chenshihang
	 */
	public interface ITrafficMonitorCallback {
		void onTrafficDataOverflow(long amount);
	}

}
