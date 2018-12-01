package com.evun.activititest.listener;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineLifecycleListener;

public class MyProcessEngineLifecycleListener implements ProcessEngineLifecycleListener{

	@Override
	public void onProcessEngineBuilt(ProcessEngine processEngine) {
		System.out.println("引擎开启-----------------" + processEngine);
	}

	@Override
	public void onProcessEngineClosed(ProcessEngine processEngine) {
		System.out.println("引擎关闭-----------------" + processEngine);
	}

}
