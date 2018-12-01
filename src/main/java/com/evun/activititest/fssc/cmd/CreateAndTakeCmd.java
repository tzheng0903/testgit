package com.evun.activititest.fssc.cmd;

import java.lang.reflect.Field;
import java.util.UUID;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.runtime.AtomicOperation;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.reflect.FieldUtils;

public class CreateAndTakeCmd implements Command<java.lang.Void> {

	private String taskId;
	private Task task ;
	private ExecutionEntity execution;
	private ActivityImpl currentActivity;
	private ProcessDefinitionEntity processDefinitionEntity;
	public CreateAndTakeCmd(String taskId) {
		super();
		this.taskId = taskId;
	}


	@Override
	public Void execute(CommandContext commandContext) {
		task = commandContext.getTaskEntityManager().findTaskById(taskId);
		execution = commandContext.getExecutionEntityManager().findExecutionById(task.getExecutionId());
		processDefinitionEntity = commandContext.getProcessDefinitionEntityManager().findProcessDefinitionById(task.getProcessDefinitionId());
		execution.setActivity(cloneActivity(currentActivity));
		execution.performOperation(AtomicOperation.TRANSITION_CREATE_SCOPE);
		return null;
	}

	private ActivityImpl cloneActivity(ActivityImpl currentActivity){
		ActivityImpl clone = processDefinitionEntity.createActivity(UUID.randomUUID().toString());
		cloneProperties(currentActivity,clone,"properties");
		return clone;
	}
	
	private void cloneProperties(Object source,Object target,String... fieldNames){
		for (String fieldName : fieldNames)
		{
			try
			{
				Field field = FieldUtils.getField(source.getClass(), fieldName, true);
				field.setAccessible(true);
				field.set(target, field.get(source));
			}
			catch (Exception e)
			{
				throw new ActivitiObjectNotFoundException("properties copy error");
			}
		}
	}
}
