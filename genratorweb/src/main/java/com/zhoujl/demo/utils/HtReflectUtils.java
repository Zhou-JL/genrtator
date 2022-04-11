package com.zhoujl.demo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**

 */
public class HtReflectUtils {
    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "getByKey";
    private static final String CGLIB_CLASS_SEPARATOR = "$$";
    private static final Map<Class<?>, List<Field>> classFieldsMap = new ConcurrentHashMap();
    private static Logger logger = LoggerFactory.getLogger(HtReflectUtils.class);

    public HtReflectUtils() {
    }

    public static Object getValueByFieldName(Object obj, String fieldName, String candidateFieldName) {
        if (fieldName == null) {
            return null;
        } else {
            Object value = null;

            try {
                Field field = getFieldByFieldName(obj, fieldName);
                if (field == null) {
                    field = getFieldByFieldName(obj, candidateFieldName);
                }

                if (field != null) {
                    if (field.isAccessible()) {
                        value = field.get(obj);
                    } else {
                        field.setAccessible(true);
                        value = field.get(obj);
                        field.setAccessible(false);
                    }
                }
            } catch (Exception var5) {
            }

            return value;
        }
    }

    public static Object getValueByFieldName(Object obj, String fieldName) {
        if (fieldName == null) {
            return null;
        } else {
            Object value = null;

            try {
                Field field = getFieldByFieldName(obj, fieldName);
                if (field != null) {
                    if (field.isAccessible()) {
                        value = field.get(obj);
                    } else {
                        field.setAccessible(true);
                        value = field.get(obj);
                        field.setAccessible(false);
                    }
                }
            } catch (Exception var4) {
            }

            return value;
        }
    }

    public static Field getFieldByFieldName(Object obj, String fieldName) {
        Class superClass = obj.getClass();

        while(superClass != Object.class) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException var4) {
                superClass = superClass.getSuperclass();
            }
        }

        return null;
    }

    public static void setValueByFieldName(Object obj, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        if (field.isAccessible()) {
            field.set(obj, value);
        } else {
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        }

    }





}
