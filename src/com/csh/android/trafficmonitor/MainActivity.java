package com.csh.android.trafficmonitor;

import com.csh.android.trafficmonitor.TrafficMonitor.OnTrafficOverflowListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	final static String URL1 = "http://svn.gomo.cn";
	final static String URL2 = "http://xuan.3g.cn";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
		findViewById(R.id.button5).setOnClickListener(this);
		findViewById(R.id.button6).setOnClickListener(this);
		
		TrafficOverflowDebugTool.start(this);
	}

	@Override
	public void onClick(View v) {
		HttpRequestLogger logger = HttpRequestLogger.getInstance();
		switch (v.getId()) {
		case R.id.button1:
			logger.requestStart(URL1);
			break;
		case R.id.button2:
			logger.requestReturn(URL1);
			break;
		case R.id.button3:
			logger.requestException(URL1);
			break;
		case R.id.button4:
			logger.requestStart(URL2);
			break;
		case R.id.button5:
			logger.requestReturn(URL2);
			break;
		case R.id.button6:
			logger.requestException(URL2);
			break;
		default:
			break;
		}
	}
}
