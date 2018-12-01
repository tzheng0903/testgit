package org.openwebflow.ctrl.command;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.runtime.AtomicOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class CreateAndTakeTransitionCmd implements Command<java.lang.Void>
{
	static Log logger = LogFactory.getLog(CreateAndTakeTransitionCmd.class);
	private ActivityImpl _activity;

	private String _executionId;

	public CreateAndTakeTransitionCmd(String executionId, ActivityImpl activity)
	{
		_executionId = executionId;
		_activity = activity;
	}

	@Override
	public Void execute(CommandContext commandContext)
	{
		//创建新任务
		logger.debug(
			String.format("executing activity: %s", _activity.getId()));

		ExecutionEntity execution = commandContext.getExecutionEntityManager().findExecutionById(_executionId);
		execution.setActivity(_activity);
		execution.performOperation(AtomicOperation.TRANSITION_CREATE_SCOPE);

		return null;
	}
}