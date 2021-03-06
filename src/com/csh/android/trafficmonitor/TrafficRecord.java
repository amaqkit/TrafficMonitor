package com.csh.android.trafficmonitor;

/**
 * 流量记录bean
 * 
 * @author chenshihang
 *
 */
public class TrafficRecord {

	private long mStartTime;
	
	private long mEndTime;
	
	private long mTrafficCount;
	
	public long getTrafficCount() {
		return mTrafficCount;
	}
	
	public void setTrafficCount(long count) {
		mTrafficCount = count;
	}
	
	public long getInterval() {
		return mEndTime - mStartTime;
	}
	
	public void setStartTime(long time) {
		if (time == 0) {
			time = System.currentTimeMillis();
		}
		mStartTime = time;
	}
	
	public void setEndTime(long time) {
		if (time == 0) {
			time = System.currentTimeMillis();
		}
		mEndTime = time;
	}

	@Override
	public String toString() {
		return "TrafficRecord [mStartTime=" + mStartTime + ", mEndTime="
				+ mEndTime + ", mTrafficCount=" + mTrafficCount + "/" + getInterval() + "]";
	}
	
	
}
