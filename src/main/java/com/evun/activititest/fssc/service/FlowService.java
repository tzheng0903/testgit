package com.evun.activititest.fssc.service;

import java.util.Arrays;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.RuntimeServiceImpl;

import com.evun.activititest.fssc.cmd.AdditionCmd;
import com.evun.activititest.fssc.cmd.MoveToAndReturnCmd;
import com.evun.activititest.fssc.cmd.MoveToCmd;

public class FlowService {

	private ProcessEngine processEngine;
	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}


	public void moveTo(String taskId,String taskDefKey){
		((RuntimeServiceImpl)processEngine.getRuntimeService()).getCommandExecutor().execute(new MoveToCmd(taskId, null, false, taskDefKey));
	}
	public void moveToAndReturn(String taskId,String taskDefKey){
		((RuntimeServiceImpl)processEngine.getRuntimeService()).getCommandExecutor().execute(new MoveToAndReturnCmd(taskId,taskDefKey));
	}
	
	public void addassignees(String taskId,String... assignees){
		((RuntimeServiceImpl)processEngine.getRuntimeService()).getCommandExecutor().execute(new AdditionCmd(taskId,Arrays.asList(assignees)));
	}
	
}
