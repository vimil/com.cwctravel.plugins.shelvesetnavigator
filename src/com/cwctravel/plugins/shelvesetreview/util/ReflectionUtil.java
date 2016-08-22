package com.cwctravel.plugins.shelvesetreview.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.IStatus;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;

public class ReflectionUtil {

	public static Object getFieldValue(Object obj, String fieldName, boolean isAccessible) {
		Object result = null;
		if (obj != null) {
			Field field = findField(obj.getClass(), fieldName);
			if (field != null) {
				if (!isAccessible) {
					field.setAccessible(true);
				}
				try {
					result = field.get(obj);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					ShelvesetReviewPlugin.log(IStatus.ERROR, e.getMessage(), e);
				}
			}
		}
		return result;
	}

	public static Object invokeMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object[] parameters, boolean isAccessible) {
		Object result = null;
		if (obj != null) {
			Method m = findMethod(obj.getClass(), methodName, parameterTypes);
			if (m != null) {
				if (!isAccessible) {
					m.setAccessible(true);
				}
				try {
					result = m.invoke(obj, parameters);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					ShelvesetReviewPlugin.log(IStatus.ERROR, e.getMessage(), e);
				}
			}
		}
		return result;
	}

	private static Method findMethod(Class<? extends Object> clazz, String methodName, Class<?>[] parameterTypes) {
		Method result = null;
		if (clazz != null) {
			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods) {
				if (methodName.equals(method.getName())) {
					Class<?>[] methodParameterTypes = method.getParameterTypes();
					if (parameterTypesCompatible(parameterTypes, methodParameterTypes)) {
						result = method;
						break;
					}
				}
			}
			if (result == null) {
				result = findMethod(clazz.getSuperclass(), methodName, parameterTypes);

			}
		}
		return result;
	}

	private static boolean parameterTypesCompatible(Class<?>[] parameterTypes1, Class<?>[] parameterTypes2) {
		boolean result = false;
		if (parameterTypes1 != null && parameterTypes2 != null) {
			if (parameterTypes1.length == parameterTypes2.length) {
				result = true;
				for (int i = 0; i < parameterTypes1.length; i++) {
					if (!parameterTypes2[i].isAssignableFrom(parameterTypes1[i])) {
						result = false;
						break;
					}
				}
			}
		}
		return result;
	}

	private static Field findField(Class<? extends Object> clazz, String fieldName) {
		Field result = null;
		if (clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (fieldName.equals(field.getName())) {
					result = field;
					break;
				}
			}
			if (result == null) {
				result = findField(clazz.getSuperclass(), fieldName);
			}
		}
		return result;
	}
}
