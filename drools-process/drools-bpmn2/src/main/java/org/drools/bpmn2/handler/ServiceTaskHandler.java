package org.drools.bpmn2.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class ServiceTaskHandler implements WorkItemHandler {

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        String i = (String) workItem.getParameter("Interface");
        String operation = (String) workItem.getParameter("Operation");
        String parameterType = (String) workItem.getParameter("ParameterType");
        Object parameter = workItem.getParameter("Parameter");
        try {
            Class<?> c = Class.forName(i);
            Object instance = c.newInstance();
            Class<?>[] classes = null;
            Object[] params = null;
            if (parameterType != null) {
                classes = new Class<?>[] {
                    Class.forName(parameterType)
                };
                params = new Object[] {
                    parameter
                };
            }
            Method method = c.getMethod(operation, classes);
            Object result = method.invoke(instance, params);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result", result);
            manager.completeWorkItem(workItem.getId(), results);
        } catch (ClassNotFoundException e) {
            System.err.println(e);
        } catch (InstantiationException e) {
            System.err.println(e);
        } catch (IllegalAccessException e) {
            System.err.println(e);
        } catch (NoSuchMethodException e) {
            System.err.println(e);
        } catch (InvocationTargetException e) {
            System.err.println(e);
        }
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Do nothing, cannot be aborted
    }

}
