package org.openwebflow.util;

import java.lang.reflect.Field;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.openwebflow.ctrl.command.CreateAndTakeTransitionCmd;

/**
 * 实现对象的克隆功能
 * 
 * @author bluejoe2008@gmail.com
 *
 */
public abstract class CloneUtils
{
	static Log logger = LogFactory.getLog(CreateAndTakeTransitionCmd.class);
	public static void copyFields(Object source, Object target, String... fieldNames)
	{
		Assert.assertNotNull(source);
		Assert.assertNotNull(target);
		Assert.assertSame(source.getClass(), target.getClass());

		for (String fieldName : fieldNames)
		{
			try
			{
				Field field = FieldUtils.getField(source.getClass(), fieldName, true);
				field.setAccessible(true);
				field.set(target, field.get(source));
			}
			catch (Exception e)
			{
				logger.warn(e.getMessage());
			}
		}
	}
}
