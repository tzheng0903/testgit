package org.openwebflow.util;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openwebflow.ctrl.command.CreateAndTakeTransitionCmd;

/**
 * 流程定义相关操作的封装
 * 
 * @author bluejoe2008@gmail.com
 *
 */
public abstract class ProcessDefinitionUtils
{
	static Log logger = LogFactory.getLog(CreateAndTakeTransitionCmd.class);
	public static ActivityImpl getActivity(ProcessEngine processEngine, String processDefId, String activityId)
	{
		ProcessDefinitionEntity pde = getProcessDefinition(processEngine, processDefId);
		return (ActivityImpl) pde.findActivity(activityId);
	}

	public static ProcessDefinitionEntity getProcessDefinition(ProcessEngine processEngine, String processDefId)
	{
		return (ProcessDefinitionEntity) ((RepositoryServiceImpl) processEngine.getRepositoryService())
				.getDeployedProcessDefinition(processDefId);
	}

	public static void grantPermission(ActivityImpl activity, String assigneeExpression,
			String candidateGroupIdExpressions, String candidateUserIdExpressions) throws Exception
	{
		TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activity.getActivityBehavior()).getTaskDefinition();
		taskDefinition.setAssigneeExpression(assigneeExpression == null ? null : new FixedValue(assigneeExpression));
		FieldUtils.writeField(taskDefinition, "candidateUserIdExpressions",
			ExpressionUtils.stringToExpressionSet(candidateUserIdExpressions), true);
		FieldUtils.writeField(taskDefinition, "candidateGroupIdExpressions",
			ExpressionUtils.stringToExpressionSet(candidateGroupIdExpressions), true);

		logger.info(
			String.format("granting previledges for [%s, %s, %s] on [%s, %s]", assigneeExpression,
				candidateGroupIdExpressions, candidateUserIdExpressions, activity.getProcessDefinition().getKey(),
				activity.getProperty("name")));
	}
}
