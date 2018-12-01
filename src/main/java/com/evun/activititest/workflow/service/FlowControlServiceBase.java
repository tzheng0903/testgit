package com.evun.activititest.workflow.service;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;

public class FlowControlServiceBase {
	protected RuntimeService runtimeService;
	protected ProcessEngine processEngine;
	protected TaskService taskService;
	protected RepositoryService repositoryService;
	protected IdentityService idService;
	protected HistoryService historyService;
	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}
	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}
	public void setIdService(IdentityService idService) {
		this.idService = idService;
	}
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}
	
	
	/**
	 * 根据流程曾任务id获取流程定义实例
	 * @param taskId
	 * @return
	 */
	public ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(String taskId){
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		String processDefId = task.getProcessDefinitionId();
		ProcessDefinition processDef = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefId).singleResult();
		return (ProcessDefinitionEntity)processDef;
	}
	/**
	 * 根据任务id获取任务
	 * @param taskId
	 * @return
	 */
	public Task findTaskByID(String taskId){
		return taskService.createTaskQuery().taskId(taskId).singleResult();
	}
	
	public boolean isJoinTask(String taskId){
		return false;
	}
}
