package com.evun.activititest.fssc;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.ReadOnlyProcessDefinition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwebflow.ctrl.TaskFlowControlService;
import org.openwebflow.ctrl.TaskFlowControlServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.evun.activititest.fssc.service.FlowService;
import com.evun.activititest.workflow.service.FlowControlService;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ActConfiguration.class)
public class Test1 {

	Log logger = LogFactory.getLog(Test1.class);
	
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private ProcessEngine processEngine;
	@Autowired
	private TaskService taskService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private IdentityService idService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private TaskFlowControlServiceFactory tff;
	@Autowired
	private FlowService fs;
	
	private void addGroup(String gid,String gname,String gType,User... user){
		Group group = idService.createGroupQuery().groupId(gid).singleResult();
		if(group == null){
			group = idService.newGroup(gid);
		}
		group.setName(gname);
		group.setType(gType);
		idService.saveGroup(group);
		if(user != null){
			for (User user2 : user) {
				idService.createMembership(user2.getId(),group.getId());
			}
		}
	}
	
	private User addUser(String userId,String firstName,String lastName,String password,String  email){
		User user = idService.newUser(userId);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPassword(password);
		user.setEmail(email);
		idService.saveUser(user);
		return user;
	}
	
//	@Test
//	public void addUserGroup(){
//		addGroup("group_input","填单组","group_fssc",addUser("user_input_A","zheng","taoA","password1","tunalover@163.com"),addUser("user_input_B","zheng","taoB","password1","tunalover@163.com"),addUser("user_input_C","zheng","tao","password1","tunalover@163.com"));
//		addGroup("group_approve_1","审核1组","group_fssc",addUser("user_approve_1_A","zheng","tao1","password1","tunalover@163.com"),addUser("user_approve_1_B","zheng","tao1B","password1","tunalover@163.com"));
//		addGroup("group_approve_2","审核2组","group_fssc",addUser("user_approve_2_A","zheng","tao2","password1","tunalover@163.com"),addUser("user_approve_2_B","zheng","tao2B","password1","tunalover@163.com"),addUser("user_approve_2_C","zheng","tao2C","password1","tunalover@163.com"));
//		addGroup("group_approve_merge","合并审核组","group_fssc",addUser("user_approve_merge_A","zheng","tao3","password1","tunalover@163.com"));
//		addGroup("group_approve_and_1","并行1组","group_fssc",addUser("user_approve_and_1_A","zheng","tao4","password1","tunalover@163.com"),addUser("user_approve_and_1_B","zheng","tao4B","password1","tunalover@163.com"),addUser("user_approve_and_1_C","zheng","tao4C","password1","tunalover@163.com"),addUser("user_approve_and_1_D","zheng","tao4D","password1","tunalover@163.com"));
//		addGroup("group_approve_and_1.2","并行1.2组","group_fssc",addUser("user_approve_and_1.2_A","zheng","tao5","password1","tunalover@163.com"),addUser("user_approve_and_1.2_B","zheng","tao5B","password1","tunalover@163.com"));
//		addGroup("group_approve_and_2","并行2组","group_fssc",addUser("user_approve_and_2_A","zheng","tao6","password1","tunalover@163.com"));
//		addGroup("group_approve_boss","终极审核组","group_fssc",addUser("user_boss_A","zheng","tao7","password1","tunalover@163.com"));
//		
//		addGroup("group_emp","员工","group_fsscx",addUser("user_emp","qin","longemp","pass1","tunalover@163.com"));
//		addGroup("group_manager","项目经理","group_fsscx",addUser("user_manager","qin","longmanager","pass1","tunalover@163.com"));
//		addGroup("group_depmanager","部门领导","group_fsscx",addUser("user_depmanagerA","qin","longdepmanagerA","pass1","tunalover@163.com"),addUser("user_depmanagerB","qin","longdepmanagerB","pass1","tunalover@163.com"));
//		addGroup("group_boss","总经理","group_fsscx",addUser("user_boss","qin","longboss","pass1","tunalover@163.com"));
//	}
//	@Test
//	public void deploytest() {
//		deploy("MyProcess1.bpmn");
//	}
	
	Deployment deploy(String path){
		System.out.println("部署文件"+path);
		Deployment deployMent = repositoryService.createDeployment().addClasspathResource(path).deploy();
		System.out.println("部署成功："+deployMent.getId());
		return deployMent;
	}
	
	private void startInstance(String dpid){
		ProcessDefinition prodef = repositoryService.createProcessDefinitionQuery().deploymentId(dpid).singleResult();
		System.out.println(prodef.getId());
		Map<String,Object> map = new HashMap<>();
		map.put("day", 11);
		ProcessInstance instance = runtimeService.startProcessInstanceById(prodef.getId(), map);
		System.out.println(instance.getId());
	}
	
	@Test
	public void test() throws Exception {
//		deploy("fssc2.bpmn");
//		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("fssc2");
		taskService.complete("525002");
//		runtimeService.deleteProcessInstance("465001", "");
//		fs.moveTo("347503", "audit2");
//		taskService.claim("357502", "user_manager");
//		taskService.delegateTask("357502", "user_depmanagerA");
//		taskService.resolveTask("357502");
//		TaskFlowControlService ts = tff.create("355001");
//		ts.insertTasksBefore("manager_audit", "user_approve_1_A","user_approve_1_B");
//		System.out.println();
//		fs.addassignees("460002", "user_approve_1_A","user_approve_1_B");
//		fs.moveToAndReturn("470002","emp_input");
		System.out.println();
	}
	
	
	
	public void queryAllActivities(){
		
		RepositoryServiceImpl repimpl = (RepositoryServiceImpl)repositoryService;
		ReadOnlyProcessDefinition rpd = repimpl.getDeployedProcessDefinition("geely-test:1:142504");
		List<? extends PvmActivity> list = rpd.getActivities();
		list.forEach(pvmActivity->{
			ActivityImpl impl = (ActivityImpl)pvmActivity;
			System.out.println(impl.getClass()+"--"+impl.getId()+"--"+impl.getOutgoingTransitions());
		});
	}
	public void queryActivities(){
		
		RepositoryServiceImpl repimpl = (RepositoryServiceImpl)repositoryService;
		ReadOnlyProcessDefinition rpd = repimpl.getDeployedProcessDefinition("geely-test:1:142504");
		PvmActivity impl = rpd.findActivity("audit1");
		System.out.println(impl.getClass()+"--"+impl.getId()+"--"+impl.getOutgoingTransitions());
	}
	
	
	private void completeInstance(String processInstanceId){
		Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		taskService.complete(task.getId());
	}
	
	private void showTask(Task task){
		if(task == null){
			System.out.println("空记录");
		}
		System.out.println("name:"+task.getName()+",id:"+task.getId());
	}
	private void showTask(List<Task> task){
		if(task == null){
			System.out.println("没有记录");
		}
		task.forEach(t->{
			showTask(t);
		});
	}

}
