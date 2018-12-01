package com.evun.activititest.fssc.cmd;

import java.util.Map;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.task.Task;

/**
 * 驳回只能由原来审过的人审
 * 
 * 先检查是否再同一个执行流中，如果不是则需要拷贝处理
 * 
 * 跳转,跳转之后按照流程继续执行
 * @author Tao.Zheng1
 *
 */
public class MoveToCmd extends NeedsActiveTaskCmd<java.lang.Void>{


	private static final String RESON="JUMP";
	
	private static final long serialVersionUID = 1L;
	protected Map<String, Object> variables;
	protected boolean localScope;
	protected String taskDefKey;
	
	public MoveToCmd(String taskId, Map<String, Object> variables, boolean localScope, String taskDefKey) {
		super(taskId);
		this.variables = variables;
		this.localScope = localScope;
		this.taskDefKey = taskDefKey;
	}

	

	@Override
	protected Void execute(CommandContext commandContext, TaskEntity task) {
		ExecutionEntity execution = commandContext.getExecutionEntityManager().findExecutionById(task.getExecutionId());
		if(execution == null){
        	throw new ActivitiObjectNotFoundException("Cannot find execution with id " + task.getExecutionId(), Task.class);
        }
        execution.destroyScope(RESON); 
        ProcessDefinitionImpl processDefinition = execution.getProcessDefinition();
        ActivityImpl findActivity = processDefinition.findActivity(taskDefKey);
        if(findActivity == null){
        	throw new ActivitiObjectNotFoundException("Cannot find activity with id " + taskDefKey, ActivityImpl.class);
        }
        execution.executeActivity(findActivity);
        return null;
	}
	
	

}
