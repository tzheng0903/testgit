package com.evun.activititest.fssc;

import java.util.Properties;

import javax.sql.DataSource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngineLifecycleListener;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.openwebflow.ctrl.RuntimeActivityDefinitionManager;
import org.openwebflow.ctrl.TaskFlowControlServiceFactory;
import org.openwebflow.ctrl.impl.DefaultTaskFlowControlServiceFactory;
import org.openwebflow.mgr.mem.InMemoryRuntimeActivityDefinitionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.evun.activititest.fssc.service.FlowService;
import com.evun.activititest.listener.MyProcessEngineLifecycleListener;
import com.evun.activititest.workflow.service.FlowControlService;

@Configuration
public class ActConfiguration {

	@Bean
	public ProcessEngineConfiguration processEngineConfiguration(){
		SpringProcessEngineConfiguration conf = new SpringProcessEngineConfiguration();
		conf.setDataSource(dataSource());
		conf.setDatabaseSchemaUpdate("true");
		
		conf.setJobExecutorActivate(false);
		conf.setTransactionManager(transactionManager());
		return conf;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(dataSource());
		return tm;
	}

	@Bean
	public DataSource dataSource() {
		Properties properties = new Properties();
		properties.setProperty("driverClassName", "com.mysql.jdbc.Driver");
		properties.setProperty("url", "jdbc:mysql://127.0.0.1:3306/acttestx?useUnicode=true&amp;characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull");
		properties.setProperty("username", "root");
		properties.setProperty("password", "root");
		try {
			return BasicDataSourceFactory.createDataSource(properties );
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Bean
	public FlowService flowService(){
		FlowService fs = new FlowService();
		fs.setProcessEngine(processEngine());
		return fs;
	}
	@Bean
	public ProcessEngine processEngine(){
		return processEngineConfiguration().buildProcessEngine();
	}
	
	@Bean
	public RepositoryService repositoryService(){
		return processEngine().getRepositoryService();
	}
	
	@Bean
	public TaskService taskService(){
		return processEngine().getTaskService();
	}
	
	@Bean
	public IdentityService  identityService(){
		return processEngine().getIdentityService();
	}
	
	@Bean
	public ManagementService managementService(){
		return processEngine().getManagementService();
	}
	
	@Bean
	public RuntimeService runtimeService(){
		return processEngine().getRuntimeService();
	}
	
	@Bean
	public HistoryService historyService(){
		return processEngine().getHistoryService();
	}
	
	@Bean
	public FlowControlService flowControlService(){
		FlowControlService fService = new FlowControlService();
		fService.setHistoryService(historyService());
		fService.setIdService(identityService());
		fService.setProcessEngine(processEngine());
		fService.setRepositoryService(repositoryService());
		fService.setRuntimeService(runtimeService());
		fService.setTaskService(taskService());
		return fService;
	}
	
	@Bean(name="myProcessEngineLifecycleListener")
	public ProcessEngineLifecycleListener processEngineLifecycleListener(){
		return new MyProcessEngineLifecycleListener();
	}
	@Bean
	public RuntimeActivityDefinitionManager runtimeActivityDefinitionManager(){
		return new InMemoryRuntimeActivityDefinitionManager();
	}
	@Bean
	public TaskFlowControlServiceFactory taskFlowControlServiceFactory(){
		TaskFlowControlServiceFactory tff = new DefaultTaskFlowControlServiceFactory(runtimeActivityDefinitionManager(),processEngine());
		return tff;
	}
}
