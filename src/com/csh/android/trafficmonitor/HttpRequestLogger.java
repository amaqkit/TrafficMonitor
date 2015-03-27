package com.csh.android.trafficmonitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * HTTP请求记录器，以url为key值记录该请求的请求数、返回数、异常数
 * 
 * @author chenshihang
 *
 */
public class HttpRequestLogger {
	
	private HashMap<String, RequestLogBean> mLogMap;
	
	private static HttpRequestLogger sInstance;
	
	private boolean mStart;
	
	private long mStartTime;
	
	private long mEndTime;
	
	private HttpRequestLogger() {
		mLogMap = new HashMap<String, HttpRequestLogger.RequestLogBean>();
	}
	
	public synchronized static HttpRequestLogger getInstance() {
		if (sInstance == null) {
			sInstance = new HttpRequestLogger();
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
		mLogMap.clear();
		mStart = true;
		mStartTime = System.currentTimeMillis();
	}
	
	public boolean isRunning() {
		return mStart;
	}
	
	public synchronized void stopRecord() {
		mStart = false;
		mEndTime = System.currentTimeMillis();
	}
	
	public synchronized void reset() {
		mLogMap.clear();
	}
	
	public synchronized void requestStart(String url) {
		if (mStart) {
			getRequestLogBean(url).addRequestCount();
		}
	}
	
	public synchronized void requestReturn(String url) {
		if (mStart) {
			getRequestLogBean(url).addReturnCount();
		}
	}
	
	public synchronized void requestException(String url) {
		if (mStart) {
			getRequestLogBean(url).addExceptionCount();
		}
	}
	
	public String generateReport() {
		if (mStart) {
			throw new IllegalStateException("log is running, should stop record first");
		}
		final String separate = "||";
		final String newLine = "\n";
		StringBuffer buffer = new StringBuffer("");
		buffer.append("from:");
		buffer.append(TimeUnit.longToStr(mStartTime, TimeUnit.LONG_FORMAT));
		buffer.append(newLine);
		buffer.append("to:");
		buffer.append(TimeUnit.longToStr(mEndTime, TimeUnit.LONG_FORMAT));
		buffer.append(newLine);
		buffer.append("detail:");
		buffer.append(newLine);
		
		if (mLogMap != null) {
			Set<String> set = mLogMap.keySet();
			int count = 0;
			for (String string : set) {
				if (count > 100) {
					/*
					 * 防止日志数量过大，上传也是需要流量的。。目测不会发生，用户的流量异常应该是重复请求导致
					 */
					buffer.append("......\ntoo much! total size:");
					buffer.append(set.size());
					buffer.append(newLine);
					break;
				}
				RequestLogBean bean = mLogMap.get(string);
				buffer.append(bean.mUrl);
				buffer.append(separate);
				buffer.append(bean.mRequestCount);
				buffer.append(separate);
				buffer.append(bean.mReturnCount);
				buffer.append(separate);
				buffer.append(bean.mExceptionCount);
				buffer.append(newLine);
				count++;
			}
		}
		
		return buffer.toString();
	}
	
	/**
	 * 请求日志bean
	 * 
	 * @author chenshihang
	 *
	 */
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
