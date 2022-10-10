package com.zhoujl.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-9-23 9:04
 * @see: com.zhoujl.demo.controller
 * @Version: 1.0
 */
@RestController
@RequestMapping("/flowable3")
@Slf4j
@Api(tags = "flowable—FlowableController3")
public class FlowableController3 {

    @Autowired
    ProcessEngine processEngine;


    /**
     * 部署流程
     */
    @GetMapping("deploy")
    @ApiOperation(value = "部署流程")
    public String deploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("FLOW_PerformanceGuaranteeOrderResult.bpmn20.xml")
                .name("履约保函申请")
                .deploy();
        System.out.println("deployment.getId(): " + deployment.getId());       //部署id,删除部署流程是需要
        System.out.println("deployment.getName(): " + deployment.getName());
        return "success";

    }


    /**
     * 启动流程定义  act_re_procdef  的 ID_
     */
    @GetMapping("run")
    @ApiOperation(value = "启动流程定义")
    public String processInstance() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> variables = new HashMap<>();
        variables.put("acceptId", "投标人");
        variables.put("platformOrgId", "平台");
        variables.put("guaranteeOrgId", "担保机构");
        //这里的key  就是xml里面的process Id
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("FLOW_PerformanceGuaranteeOrderResult", variables);
        System.out.println("processInstance.getId(): " + processInstance.getId());        //流程定义id
        System.out.println("processInstance.getDeploymentId(): " + processInstance.getDeploymentId());    //部署流程id
        return processInstance.getId();
    }


    @GetMapping("run1")
    @ApiOperation(value = "执行1-投标人执行")
    public String processInstance1(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("投标人").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }

        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        taskService.complete(task.getId(), processVariables);
        return "success";
    }


    @GetMapping("run2")
    @ApiOperation(value = "执行2-平台审核通过")
    public String processInstance2(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("平台").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }

        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //李四提交保险担保申请
        processVariables.put("commit", true);   //添加一个orderId参数
        taskService.complete(task.getId(), processVariables);
        return "success";
    }

    @GetMapping("run3")
    @ApiOperation(value = "执行3-平台审核不通过")
    public String processInstance3(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("平台").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }

        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //李四提交保险担保申请
        processVariables.put("commit", false);   //添加一个orderId参数
        taskService.complete(task.getId(), processVariables);
        return "success";
    }


    @GetMapping("run4")
    @ApiOperation(value = "执行4-投保人信息确认")
    public String processInstance4(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("投标人").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }

        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //李四提交保险担保申请
        processVariables.put("commit", true);   //添加一个orderId参数
        taskService.complete(task.getId(), processVariables);
        return "success";
    }

    @GetMapping("run5")
    @ApiOperation(value = "执行5-投保人信息否认")
    public String processInstance5(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("投标人").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }

        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //李四提交保险担保申请
        processVariables.put("commit", false);   //添加一个orderId参数
        taskService.complete(task.getId(), processVariables);
        return "success";
    }


    @GetMapping("run6")
    @ApiOperation(value = "执行6-投保人支付")
    public String processInstance6(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("投标人").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }

        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        taskService.complete(task.getId(), processVariables);
        return "success";
    }

    @GetMapping("run7")
    @ApiOperation(value = "执行7-担保机构审核通过")
    public String processInstance7(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("担保机构").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        processVariables.put("commit", true);   //添加一个orderId参数
        taskService.complete(task.getId(), processVariables);
        return "success";
    }

    @GetMapping("run8")
    @ApiOperation(value = "执行8-担保机构审核不通过")
    public String processInstance8(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("担保机构").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        processVariables.put("commit", false);   //添加一个orderId参数
        taskService.complete(task.getId(), processVariables);
        return "success";
    }

    @GetMapping("run9")
    @ApiOperation(value = "执行9-投保人信息补录")
    public String processInstance9(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("投标人").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        taskService.complete(task.getId(), processVariables);
        return "success";
    }


    @GetMapping("run10")
    @ApiOperation(value = "执行10-担保机构信息补录审核通过")
    public String processInstance10(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("担保机构").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        processVariables.put("commit", true);   //添加一个orderId参数
        taskService.complete(task.getId(), processVariables);
        return "success";
    }

    @GetMapping("run11")
    @ApiOperation(value = "执行11-担保机构信息补录审核不通过")
    public String processInstance11(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("担保机构").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        processVariables.put("commit", false);   //添加一个orderId参数
        taskService.complete(task.getId(), processVariables);
        return "success";
    }

}
