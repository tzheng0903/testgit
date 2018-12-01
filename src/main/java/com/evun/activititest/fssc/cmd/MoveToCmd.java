package com.evun.activititest.fssc.cmd;

import java.util.List;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.HistoricTaskInstanceQueryImpl;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
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
	protected String taskDefKey;
	
	public MoveToCmd(String taskId, String taskDefKey) {
		super(taskId);
		this.taskDefKey = taskDefKey;
	}

	

	@Override
	protected Void execute(CommandContext commandContext, TaskEntity task) {
		
		HistoricTaskInstance historyTaskInstance = getHistoryTask(commandContext);
		ExecutionEntity execution = commandContext.getExecutionEntityManager().findExecutionById(task.getExecutionId());//当前执行流
		if(execution == null){
			throw new ActivitiObjectNotFoundException("Cannot find execution with id " + task.getExecutionId(), Task.class);
		}
		ProcessDefinitionImpl processDefinition = execution.getProcessDefinition();//流程定义
		ActivityImpl targetActivity = processDefinition.findActivity(taskDefKey);//目标流程跳转点
		ActivityImpl currentActivity = processDefinition.findActivity(task.getTaskDefinitionKey());//当前任务定义点
		if(targetActivity == null){
			throw new ActivitiObjectNotFoundException("Cannot find activity with id " + taskDefKey, ActivityImpl.class);
		}
		
		if(historyTaskInstance.getExecutionId().equals(task.getExecutionId())){//如果再统一个执行流
			execution.destroyScope(RESON); 
			execution.executeActivity(targetActivity);
		}else{//如果不是同一个执行流，也就是再分支上面，则进行复制处理
			
		}
		
        return null;
	}
	
	private HistoricTaskInstance getHistoryTask(CommandContext commandContext){
		HistoricTaskInstanceQueryImpl historicTaskInstanceQuery = new HistoricTaskInstanceQueryImpl();
		historicTaskInstanceQuery.taskDefinitionKey(taskDefKey);
		List<HistoricTaskInstance> list = commandContext.getHistoricTaskInstanceEntityManager().findHistoricTaskInstancesByQueryCriteria(historicTaskInstanceQuery);
		return list.get(list.size()-1);
	}
	
	/**
	 * 获取从目标点到当前点经过的任务点
	 * @param currentActivity
	 * @param targetActivity
	 * @return
	 */
	private List<ActivityImpl> getRoutList(ActivityImpl currentActivity,ActivityImpl targetActivity){
		List<PvmTransition> list = targetActivity.getOutgoingTransitions();
		for (PvmTransition pvmTransition : list) {
			PvmActivity activity = pvmTransition.getDestination();
		}
		return null;
	}
	

}
