package com.evun.activititest.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

public class CreateListener implements TaskListener{

	@Override
	public void notify(DelegateTask delegateTask) {
		String id   = delegateTask.getId();
		System.out.println("taskID:"+ id);
	}


}
