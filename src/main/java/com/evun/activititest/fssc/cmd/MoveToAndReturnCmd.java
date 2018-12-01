package com.evun.activititest.fssc.cmd;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.HistoricTaskInstanceQueryImpl;
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
 * 驳回只能由原来审过的人审
 * 跳转,跳转之后从哪儿来回哪儿去
 * @author Tao.Zheng1
 *
 */
public class MoveToAndReturnCmd implements Command<java.lang.Void>{


	private static final String RESON="JUMP";
	
	private String taskId;
	private String targetTaskDefinitionKey;
	
	
	public MoveToAndReturnCmd(String taskId, String targetTaskDefinitionKey) {
		super();
		this.taskId = taskId;
		this.targetTaskDefinitionKey = targetTaskDefinitionKey;
	}


	private ProcessDefinitionEntity processDefinitionEntity;
	private ActivityImpl currentActivity ;
	private ActivityImpl targetActivity;
	private TaskEntity taskEntity;
	private ExecutionEntity execution;
	@Override
	public Void execute(CommandContext commandContext) {
		taskEntity = commandContext.getTaskEntityManager().findTaskById(taskId);
		processDefinitionEntity = commandContext.getProcessEngineConfiguration().getDeploymentManager().findDeployedProcessDefinitionById(taskEntity.getProcessDefinitionId());
		currentActivity = processDefinitionEntity.findActivity(taskEntity.getTaskDefinitionKey());
		targetActivity = processDefinitionEntity.findActivity(targetTaskDefinitionKey);
		execution = commandContext.getExecutionEntityManager().findExecutionById(taskEntity.getExecutionId());
		ActivityImpl activityImpl = createActivity(targetTaskDefinitionKey,getAssignee(commandContext));
		execution.destroyScope(RESON); 
		createRout(activityImpl);
		execution.executeActivity(activityImpl);
		return null;
	}
	
	private void createRout(ActivityImpl activityImpl){
		activityImpl.getOutgoingTransitions().clear();
		activityImpl.createOutgoingTransition("move-flow").setDestination(currentActivity);
	}
	
	private ActivityImpl createActivity(String targetTaskDefinitionKey,String assignee){
		String cloneActivitiId = generatId();
		ActivityImpl cloneActivity = processDefinitionEntity.createActivity(cloneActivitiId);
		cloneActivity.setProperties(targetActivity.getProperties());
		Map<String, List<ExecutionListener>> executionListeners = targetActivity.getExecutionListeners();
		executionListeners.forEach((eventName,executionListenerlist)->{
			executionListenerlist.forEach(executionListener->{
				cloneActivity.addExecutionListener(eventName, executionListener);
			});
		});
		
		
		UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) targetActivity.getActivityBehavior();
		
		TaskDefinition taskDefinition = userTaskActivityBehavior.getTaskDefinition();
		TaskDefinition cloneTaskDefinition = new TaskDefinition(taskDefinition.getTaskFormHandler());
		
		BeanUtils.copyProperties(taskDefinition, cloneTaskDefinition);
		cloneTaskDefinition.setKey(cloneActivitiId);
		if(assignee != null || !"".equals(assignee)){
			cloneTaskDefinition.setAssigneeExpression(new FixedValue(assignee));
		}
		
		UserTaskActivityBehavior cloneActivityBehavior = new UserTaskActivityBehavior(targetActivity.getId(), cloneTaskDefinition);
		cloneActivity.setActivityBehavior(cloneActivityBehavior);
		return cloneActivity;
	}
	
	/**
	 * 获取目标驳回目标节点的原来的审核人
	 * @param commandContext 
	 * @return 
	 */
	private String getAssignee(CommandContext commandContext){
		HistoricTaskInstanceQueryImpl historicTaskInstanceQuery = new HistoricTaskInstanceQueryImpl();
		historicTaskInstanceQuery.taskDefinitionKey(targetTaskDefinitionKey);
		List<HistoricTaskInstance> historyList = commandContext.getHistoricTaskInstanceEntityManager().findHistoricTaskInstancesByQueryCriteria(historicTaskInstanceQuery );
		for (HistoricTaskInstance historicTaskInstance : historyList) {
			String assignee = historicTaskInstance.getAssignee();
			if(assignee != null && !"".equals(assignee)){
				return assignee;
			}
		}
		return null;
	}
	
	/**
	 * 创建唯一id
	 * @return
	 */
	private String generatId(){
		return taskEntity.getId() + targetActivity.getId() +UUID.randomUUID().toString();
	}


}
