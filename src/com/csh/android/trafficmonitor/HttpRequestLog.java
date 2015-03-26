package com.csh.android.trafficmonitor;

import java.util.HashMap;
import java.util.Set;

public class HttpRequestLog {
	
	private HashMap<String, RequestLogBean> mLogMap;
	
	private static HttpRequestLog sInstance;
	
	private boolean mRecordLog;
	
	private HttpRequestLog() {
		mLogMap = new HashMap<String, HttpRequestLog.RequestLogBean>();
	}
	
	public synchronized static HttpRequestLog getInstance() {
		if (sInstance == null) {
			sInstance = new HttpRequestLog();
		}
		return sInstance;
	}
	
	private RequestLogBean getRequestLogBean(String url) {
		RequestLogBean bean = mLogMap.get(url);
		if (bean == null) {
			bean = new RequestLogBean(url);
			mLogMap.put(url, bean);
		}
		return bean;
	}
	
	public synchronized void startRecord() {
		mRecordLog = true;
	}
	
	public synchronized void stopRecord() {
		mRecordLog = false;
	}
	
	public synchronized void reset() {
		mLogMap.clear();
	}
	
	public synchronized void requestStart(String url) {
		if (mRecordLog) {
			getRequestLogBean(url).addRequestCount();
		}
	}
	
	public synchronized void requestReturn(String url) {
		if (mRecordLog) {
			getRequestLogBean(url).addReturnCount();
		}
	}
	
	public synchronized void requestException(String url) {
		if (mRecordLog) {
			getRequestLogBean(url).addExceptionCount();
		}
	}
	
	public String generateReport() {
		StringBuffer buffer = new StringBuffer("");
		final String separate = "||";
		if (mLogMap != null) {
			Set<String> set = mLogMap.keySet();
			for (String string : set) {
				RequestLogBean bean = mLogMap.get(string);
				buffer.append(bean.mUrl);
				buffer.append(separate);
				buffer.append(bean.mRequestCount);
				buffer.append(separate);
				buffer.append(bean.mReturnCount);
				buffer.append(separate);
				buffer.append(bean.mExceptionCount);
				buffer.append("\n");
			}
		}
		
		return buffer.toString();
	}
	
	class RequestLogBean {
		public String mUrl;
		public int mRequestCount;
		public int mReturnCount;
		public int mExceptionCount;
		
		public RequestLogBean(String url) {
			mUrl = url;
		}
		
		
		public void addRequestCount() {
			mRequestCount++;
		}
		
		public void addReturnCount() {
			mReturnCount++;
		}
		
		public void addExceptionCount() {
			mExceptionCount++;
		}
	}
}
