package org.openwebflow.alarm.impl;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.task.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.openwebflow.alarm.MessageNotifier;
import org.openwebflow.alarm.TaskAlarmService;
import org.openwebflow.alarm.TaskNotificationManager;
import org.openwebflow.ctrl.command.CreateAndTakeTransitionCmd;
import org.openwebflow.identity.IdentityMembershipManager;
import org.openwebflow.identity.UserDetailsEntity;
import org.openwebflow.identity.UserDetailsManager;
import org.openwebflow.util.IdentityUtils;
import org.springframework.beans.factory.DisposableBean;

public class TaskAlarmServiceImpl implements TaskAlarmService, DisposableBean
{
	static Log logger = LogFactory.getLog(CreateAndTakeTransitionCmd.class);
	class MonitorTask extends TimerTask
	{
		Period _parsedPeriodInAdvance;

		public MonitorTask()
		{
			_parsedPeriodInAdvance = Period.parse(_periodInAdvance);
		}

		private void checkAndNotify() throws Exception
		{
			//检查即将过期的task
			Date dueDate = DateTime.now().minus(_parsedPeriodInAdvance).toDate();

			for (Task task : _processEngine.getTaskService().createTaskQuery().active().dueAfter(dueDate).list())
			{
				//是否已经通知？
				if (!_taskNotificationManager.isNotified(task.getId()))
				{
					//没有通知则现在通知
					List<UserDetailsEntity> involvedUsers = IdentityUtils.getUserDetailsFromIds(
						IdentityUtils.getInvolvedUsers(_processEngine.getTaskService(), task, _membershipManager),
						_userDetailsManager);

					if (!involvedUsers.isEmpty())
					{
						_messageNotifier.notify(involvedUsers.toArray(new UserDetailsEntity[0]), task);
					}
					//设置标志
					_taskNotificationManager.setNotified(task.getId());
					logger.debug(String.format("notified %s", involvedUsers));
				}
			}
		}

		@Override
		public void run()
		{
			try
			{
				checkAndNotify();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	long _checkInterval = 10000;

	IdentityMembershipManager _membershipManager;

	MessageNotifier _messageNotifier;

	private Timer _monitorTimer = new Timer(true);

	TaskNotificationManager _taskNotificationManager;

	public TaskNotificationManager getTaskNotificationManager()
	{
		return _taskNotificationManager;
	}

	public void setTaskNotificationManager(TaskNotificationManager taskNotificationManager)
	{
		_taskNotificationManager = taskNotificationManager;
	}

	String _periodInAdvance;

	ProcessEngine _processEngine;

	UserDetailsManager _userDetailsManager;

	@Override
	public void destroy() throws Exception
	{
		_monitorTimer.cancel();
	}

	public long getCheckInterval()
	{
		return _checkInterval;
	}

	public IdentityMembershipManager getMembershipManager()
	{
		return _membershipManager;
	}

	public MessageNotifier getMessageNotifier()
	{
		return _messageNotifier;
	}

	public String getPeriodInAdvance()
	{
		return _periodInAdvance;
	}

	public UserDetailsManager getUserDetailsManager()
	{
		return _userDetailsManager;
	}

	public void setCheckInterval(long checkInterval)
	{
		_checkInterval = checkInterval;
	}

	public void setMembershipManager(IdentityMembershipManager membershipManager)
	{
		_membershipManager = membershipManager;
	}

	public void setMessageNotifier(MessageNotifier messageNotifier)
	{
		_messageNotifier = messageNotifier;
	}

	public void setPeriodInAdvance(String periodInAdvance)
	{
		_periodInAdvance = periodInAdvance;
	}

	public void setUserDetailsManager(UserDetailsManager userDetailsManager)
	{
		_userDetailsManager = userDetailsManager;
	}

	@Override
	public void start(ProcessEngine processEngine) throws Exception
	{
		_processEngine = processEngine;
		_monitorTimer.schedule(new MonitorTask(), _checkInterval, _checkInterval);
	}
}
