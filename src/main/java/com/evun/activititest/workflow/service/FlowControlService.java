package com.evun.activititest.workflow.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandContextFactory;
import org.activiti.engine.impl.persistence.deploy.DeploymentManager;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class FlowControlService extends FlowControlServiceBase{

	/**
	 * 查询可驳回的节点
	 * @param taskId
	 * @return
	 */
	public List<ActivityImpl> findBackActity(String taskId){
		List<ActivityImpl> retList = null;
		if(isJoinTask(taskId)){
			retList = new ArrayList<>();
		}else{
			retList = iteratorBackActivity(taskId,findActivitiImpl(taskId,null),new ArrayList<ActivityImpl>(),new ArrayList<ActivityImpl>());
		}
		Collections.reverse(retList);
		return retList;
	}
	
	/**
	 * 查询当前节点可驳回的任务节点
	 * @param taskId
	 * @param findActivitiImpl
	 * @param arrayList
	 * @param arrayList2
	 * @return
	 */
	public List<ActivityImpl> iteratorBackActivity(String taskId, ActivityImpl findActivitiImpl,
			ArrayList<ActivityImpl> rtnList, ArrayList<ActivityImpl> tempList) {
		return null;
	}

	/**
	 * 根据任务id获取节点
	 * @param taskId
	 * @param object
	 * @return
	 */
	public ActivityImpl findActivitiImpl(String taskId, String activityId) {
		ProcessDefinitionEntity def = findProcessDefinitionEntityByTaskId(taskId);
		if(StringUtils.isEmpty(activityId)){
			activityId = findTaskByID(taskId).getTaskDefinitionKey();
		}
		
		if("END".equalsIgnoreCase(activityId)){
			List<ActivityImpl> list = def.getActivities();
			for (ActivityImpl activityImpl : list) {
				if(CollectionUtils.isEmpty(activityImpl.getOutgoingTransitions())){
					return activityImpl;
				}
			}
		}
		return def.findActivity(activityId);
	}

	public void test(){
		runtimeService.startProcessInstanceById("myProcess:13:350004");
		DeploymentManager manager =((ProcessEngineConfigurationImpl)processEngine.getProcessEngineConfiguration()).getDeploymentManager();
		ProcessDefinitionEntity pde = manager.findDeployedProcessDefinitionById("myProcess:13:350004");
		System.out.println(pde);
	}
	
}	
