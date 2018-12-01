package org.openwebflow.ctrl.impl;

import org.activiti.engine.ProcessEngine;
import org.openwebflow.ctrl.RuntimeActivityDefinitionManager;
import org.openwebflow.ctrl.TaskFlowControlService;
import org.openwebflow.ctrl.TaskFlowControlServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultTaskFlowControlServiceFactory implements TaskFlowControlServiceFactory
{
	RuntimeActivityDefinitionManager _activitiesCreationStore;

	ProcessEngine _processEngine;
	

	public DefaultTaskFlowControlServiceFactory(RuntimeActivityDefinitionManager _activitiesCreationStore,
			ProcessEngine _processEngine) {
		super();
		this._activitiesCreationStore = _activitiesCreationStore;
		this._processEngine = _processEngine;
	}




	@Override
	public TaskFlowControlService create(String processId)
	{
		return new DefaultTaskFlowControlService(_activitiesCreationStore, _processEngine, processId);
	}
}
