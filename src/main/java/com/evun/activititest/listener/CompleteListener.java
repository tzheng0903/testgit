package com.evun.activititest.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;

public class CompleteListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		String a = null ;
		a.length();
	}


}
