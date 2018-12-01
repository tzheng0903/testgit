package com.evun.activititest.fssc.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.springframework.beans.BeanUtils;

/**
 * 加签(串行加签)
 * @author Tao.Zheng1
 *
 */
public class AdditionCmd implements Command<java.lang.Void>{

	private static final String RESON = "add";
	private String taskId;
	private List<String> assignees;
	
	
	private TaskEntity taskEntity;
	private ProcessDefinitionEntity processDefinitionEntity;
	ActivityImpl currentActiviti;
	private ExecutionEntity execution;

	public AdditionCmd(String taskId, List<String> assignees) {
		super();
		this.taskId = taskId;
		this.assignees = assignees;
	}

	@Override
	public Void execute(CommandContext commandContext) {
		if(assignees == null || assignees.size() == 0){
			throw new ActivitiObjectNotFoundException("Assignees can not be null");
		}
		taskEntity = commandContext.getTaskEntityManager().findTaskById(taskId);
		processDefinitionEntity = commandContext.getProcessEngineConfiguration().getDeploymentManager().findDeployedProcessDefinitionById(taskEntity.getProcessDefinitionId());
		currentActiviti = processDefinitionEntity.findActivity(taskEntity.getTaskDefinitionKey());
		execution = commandContext.getExecutionEntityManager().findExecutionById(taskEntity.getExecutionId());
		List<ActivityImpl> activityImpls = new ArrayList<>();
		assignees.forEach(assignee->{activityImpls.add(createActivity(assignee));});
		
		execution.destroyScope(RESON); 
		
		createRout(activityImpls);
		execution.executeActivity(activityImpls.get(0));
		return null;
	}
	
	private void createRout(List<ActivityImpl> activityImpls){
		for (int i = 0; i < activityImpls.size(); i++)
		{
			//设置各活动的下线
			activityImpls.get(i).getOutgoingTransitions().clear();
			activityImpls.get(i).createOutgoingTransition("flow" + (i + 1))
					.setDestination(i == activityImpls.size() - 1 ? currentActiviti : activityImpls.get(i + 1));
		}
	}
	
	private ActivityImpl createActivity(String assignee){
		String cloneActivitiId = generatId();
		ActivityImpl cloneActivity = processDefinitionEntity.createActivity(cloneActivitiId);
		cloneActivity.setProperties(currentActiviti.getProperties());
		Map<String, List<ExecutionListener>> executionListeners = currentActiviti.getExecutionListeners();
		executionListeners.forEach((eventName,executionListenerlist)->{
			executionListenerlist.forEach(executionListener->{
				cloneActivity.addExecutionListener(eventName, executionListener);
			});
		});
		
		
		UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) currentActiviti.getActivityBehavior();
		
		TaskDefinition taskDefinition = userTaskActivityBehavior.getTaskDefinition();
		TaskDefinition cloneTaskDefinition = new TaskDefinition(taskDefinition.getTaskFormHandler());
		
		BeanUtils.copyProperties(taskDefinition, cloneTaskDefinition);
		cloneTaskDefinition.setKey(cloneActivitiId);
		
		if(assignee != null || !"".equals(assignee)){
			cloneTaskDefinition.setAssigneeExpression(new FixedValue(assignee));
		}
		
		UserTaskActivityBehavior cloneActivityBehavior = new UserTaskActivityBehavior(currentActiviti.getId(), cloneTaskDefinition);
		cloneActivity.setActivityBehavior(cloneActivityBehavior);
		return cloneActivity;
	}
	
	
	/**
	 * 创建唯一id
	 * @return
	 */
	private String generatId(){
		return taskEntity.getId() + currentActiviti.getId() +UUID.randomUUID().toString();
	}

}
