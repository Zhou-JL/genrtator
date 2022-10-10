package com.zhoujl.demo.controller;

import com.zhoujl.demo.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-5-19 16:22
 * @see: com.zhoujl.demo.controller
 * @Version: 1.0
 */
@RestController
@RequestMapping("/flowable1")
@Slf4j
@Api(tags = "flowable—FlowableController1")
public class FlowableController1 {
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
                .addClasspathResource("traddingGuarrentOrder.bpmn20.xml")
                .name("保函申请")
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
        variables.put("entinfoId", "李四");
        variables.put("guarrentOrgId", "王五");
        //这里的key  就是xml里面的process Id
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("HanTang_BaoHangApply", variables);
        System.out.println("processInstance.getId(): " + processInstance.getId());        //流程定义id
        System.out.println("processInstance.getDeploymentId(): " + processInstance.getDeploymentId());    //部署流程id
        return processInstance.getId();
    }


    @GetMapping("run1")
    @ApiOperation(value = "执行1")
    public String processInstance1(String procInstId, Integer num) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("李四").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }

        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //张三提出请假两天
        processVariables.put("orderId", 11);
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


    /**
    * 删除定义的实例
     *  act_ru_task 的 PROC_INST_ID_ 为流程定义实例的id
     * @return
    */
    @GetMapping("/deleteDy")
    @ApiOperation(value = "删除定义的实例")
    public String deleteProcessInstanceById(String processInstanceId) {
        if (StringUtils.isBlank(processInstanceId)) {
            throw new RuntimeException("processInstanceId is null");
        }
        RuntimeService runtimeService = processEngine.getRuntimeService();
        HistoryService historyService = processEngine.getHistoryService();
        try {
            //根据流程实例id 去ACT_RU_EXECUTION与ACT_RE_PROCDEF关联查询流程实例数据
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (null != processInstance) {
                runtimeService.deleteProcessInstance(processInstanceId, "流程实例删除");
            } else {
                historyService.deleteHistoricProcessInstance(processInstanceId);
            }
            return "success";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("fail");
        }


    }


    /**
     * 删除流程部署   deploymentId为act_re_deployment  的 ID
     *
     * @return
     */
    @GetMapping("/delete")
    @ApiOperation(value = "删除部署的流程")
    public String deleteDeploy(String deploymentId) {
        //System.out.println("deployment.getId(): " + deployment.getId());
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //参数是id == processDefinition.getDeploymentId(),如果流程启动了就不允许删除
        //repositoryService.deleteDeployment("");
        //级联删除参数是id == processDefinition.getDeploymentId(),如果流程启动了也可以删除
        repositoryService.deleteDeployment(deploymentId, true);
        return "success";
    }


    /**
     * 领导查看需要审批的任务
     */
    @GetMapping("/queryTasks")
    @ApiOperation(value = "领导查看需要审批的任务")
    public String queryTasks(String procInstId) {

        TaskService taskService = processEngine.getTaskService();
        //根据任务的受理人，查询需要审批的任务
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("王五").singleResult();
        //根据该任务的taskid，获取到对应的任务变量
        Map<String, Object> processVariables = taskService.getVariables(task.getId());
        System.out.println("entinfoId: " + processVariables.get("entinfoId"));
        System.out.println("guarrentOrgId: " + processVariables.get("guarrentOrgId"));
        System.out.println("orderId "+ processVariables.get("orderId"));
        System.out.println("来自于"+processVariables.get("entinfoId")+"的保函申请"+"订单id:"+processVariables.get("orderId"));
        return "success";
    }
}
