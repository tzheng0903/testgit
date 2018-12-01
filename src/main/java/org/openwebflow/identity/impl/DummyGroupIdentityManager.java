package org.openwebflow.identity.impl;

import java.util.List;
import java.util.Map;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openwebflow.ctrl.command.CreateAndTakeTransitionCmd;
import org.openwebflow.identity.IdentityMembershipManager;
import org.openwebflow.util.IdentityUtils;

public class DummyGroupIdentityManager implements GroupIdentityManager, Session
{
	static Log logger = LogFactory.getLog(CreateAndTakeTransitionCmd.class);
	IdentityMembershipManager _customMembershipManager;

	public DummyGroupIdentityManager(IdentityMembershipManager customMembershipManager)
	{
		super();
		_customMembershipManager = customMembershipManager;
	}

	@Override
	public void close()
	{
	}

	@Override
	public Group createNewGroup(String groupId)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public GroupQuery createNewGroupQuery()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteGroup(String groupId)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public long findGroupCountByNativeQuery(Map<String, Object> parameterMap)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public long findGroupCountByQueryCriteria(GroupQueryImpl query)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Group> findGroupsByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Group> findGroupsByUser(String userId)
	{
		logger.debug(
			String.format("%s#findGroupsByUser(\"%s\")", _customMembershipManager, userId));

		try
		{
			return IdentityUtils.getGroupsFromIds(_customMembershipManager.findGroupIdsByUser(userId));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void flush()
	{
	}

	@Override
	public void insertGroup(Group group)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNewGroup(Group group)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateGroup(Group updatedGroup)
	{
		throw new UnsupportedOperationException();
	}

}
