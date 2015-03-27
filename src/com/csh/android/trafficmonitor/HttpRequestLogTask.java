package com.csh.android.trafficmonitor;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * HTTP请求日志任务，需设定时间，定时结束并上传report
 * 
 * @author chenshihang
 *
 */
public class HttpRequestLogTask {

	private static final String TAG = "HttpRequestLogTask";
	
	private Context mContext;
	
	private long mInterval;
	
	private long mTraffic;
	
	private static final int MSG_TIME_UP = 1;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_TIME_UP:
				finish();
				break;

			default:
				break;
			}
		}
		
	};
	
	public HttpRequestLogTask(Context context) {
		mContext = context;
	}
	
	public HttpRequestLogTask setTrafficAmount(long traffic) {
		mTraffic = traffic;
		return this;
	}
	
	public HttpRequestLogTask setInterval(long interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("interval should be positive number");
		}
		mInterval = interval;
		return this;
	}
	
	public void start() {
		if (mInterval == 0) {
			throw new IllegalStateException("should set record time first");
		}
		Log.i(TAG, "start");
		HttpRequestLogger httpRequestLog = HttpRequestLogger.getInstance();
		httpRequestLog.startRecord();
		mHandler.sendEmptyMessageDelayed(MSG_TIME_UP, mInterval);
	}
	
	public void abort() {
		mHandler.removeMessages(MSG_TIME_UP);
		HttpRequestLogger logger = HttpRequestLogger.getInstance();
		logger.stopRecord();
	}
	
	private void finish() {
		Log.i(TAG, "finish");
		HttpRequestLogger logger = HttpRequestLogger.getInstance();
		logger.stopRecord();
		String report = logger.generateReport();
		report(report);
	}
	
	private void report(String httpRequestReport) {
		StringBuffer buffer = new StringBuffer(httpRequestReport);
		buffer.append("traffic:");
		buffer.append(mTraffic);
		
		//TODO send report
		Log.i("CSH", "report --- " + buffer.toString());
		
	}
}
