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
 * @time: 2022-7-14 10:48
 * @see: com.zhoujl.demo.controller
 * @Version: 1.0
 */
@RestController
@RequestMapping("/flowable2")
@Slf4j
@Api(tags = "flowable—FlowableController2")
public class FlowableController2 {

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
                .addClasspathResource("HanTangBaoXianBaoHanApply.bpmn20.xml")
                .name("保险保函申请")
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
        variables.put("entInfoId", "李四");
        variables.put("guarrentOrgId", "王五");
        //这里的key  就是xml里面的process Id
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("HanTangBaoXianBaoHanApply", variables);
        System.out.println("processInstance.getId(): " + processInstance.getId());        //流程定义id
        System.out.println("processInstance.getDeploymentId(): " + processInstance.getDeploymentId());    //部署流程id
        return processInstance.getId();
    }



    @GetMapping("run1")
    @ApiOperation(value = "执行1")
    public String processInstance1(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("李四").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }

        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //李四提交保险担保申请
        processVariables.put("orderId", 11);   //添加一个orderId参数
        taskService.complete(task.getId(), processVariables);
        return "success";

    }


    @GetMapping("run2")
    @ApiOperation(value = "执行2")
    public String processInstance3(String procInstId,Integer flag) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("王五").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
//        Task task1 = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("22").singleResult();
//        if (task1 == null) {
//            throw new RuntimeException("task null1");
//        }
//        获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //张三提出请假两天
        processVariables.put("flag", flag);
        taskService.complete(task.getId(), processVariables);
        return "success";
    }


    @GetMapping("run3")
    @ApiOperation(value = "执行3")
    public String processInstance4(String procInstId,String payStatus) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
//        Task task1 = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("22").singleResult();
//        if (task1 == null) {
//            throw new RuntimeException("task null1");
//        }
//        获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();

        processVariables.put("payStatus", payStatus);
        taskService.complete(task.getId(), processVariables);
        return "success";
    }

}
