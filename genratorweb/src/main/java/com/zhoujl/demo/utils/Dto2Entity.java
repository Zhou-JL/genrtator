package com.zhoujl.demo.utils;

import org.springframework.beans.BeanUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**

 */
public class Dto2Entity {


    public static <T, E> List<E> transalteList(List<T> source, Class<E> targetClass) {
        List<E> result = new LinkedList();
        if (source != null && source.size() != 0) {
            try {
                Iterator var3 = source.iterator();

                while(var3.hasNext()) {
                    Object item = var3.next();
                    E dest = targetClass.newInstance();
                    BeanUtils.copyProperties(item, dest);
                    result.add(dest);
                }
            } catch (InstantiationException var6) {
                var6.printStackTrace();
            } catch (IllegalAccessException var7) {
                var7.printStackTrace();
            }

            return result;
        } else {
            return result;
        }
    }
}
