package com.zhoujl.demo.controller;

import com.zhoujl.demo.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-3-26 12:43
 * @see: com.zhoujl.demo.controller
 * @Version: 1.0
 */
@RestController
@RequestMapping("/flowable")
@Slf4j
@Api(tags = "flowable—FlowableController")
public class FlowableController {


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
                .addClasspathResource("qingjiaApply.bpmn20.xml")
                .name("保函申请单")
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
        variables.put("assing0", "张三");
        variables.put("assing1", "李四");
        variables.put("assing2", "王五");
        variables.put("assing3", "赵会计");
        //这里的key  就是xml里面的process Id
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjiaApply", variables);
        System.out.println("processInstance.getId(): " + processInstance.getId());        //流程定义id
        System.out.println("processInstance.getDeploymentId(): " + processInstance.getDeploymentId());    //部署流程id
        return processInstance.getId();
    }


    /**
     * 拿到这个流程定义下的某个实例，这里相当于是张三同学发起请假2天的一个申请，进入到下一阶段（李四审批）
     * act_ru_task 的 PROC_INST_ID_ 为流程定义实例的id  这里是2501
     */
    @GetMapping("run1")
    @ApiOperation(value = "张三发起申请")
    public String processInstance1(String procInstId, Integer num) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("张三").singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //张三提出请假两天
        processVariables.put("num", num);
        taskService.complete(task.getId(), processVariables);
        return "success";

    }


    /**
     * 张三同学突然感到2天时间不够，于是更改请假天数为4天，这个时候申请单已到达李四这里，根据实例ID 来更新流程变量，相当于修改全局变量
     */
    @GetMapping("run2")
    @ApiOperation(value = "更改申请")
    public String processInstance2(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        //根据任务的受理人，查询需要审批的任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInstId)
                .taskAssignee("李四")
                .singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
        //根据该任务的taskid，获取到对应的任务变量
        Map<String, Object> processVariables = taskService.getVariables(task.getId());
        //将2天改成4天
        processVariables.put("num", 4);
        taskService.setVariables(task.getId(), processVariables);
        return "success";
    }


    /**
     * 李四或者王五，或者赵会计获取审批管理列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "李四或者王五，或者赵会计获取审批管理列表")
    public Object list(String name) {
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(name).orderByTaskCreateTime().desc().list();
        for (Task task : tasks) {
            System.out.println(task.toString());
        }
        return tasks.toArray().toString();
    }


    /**
     * 领导查看需要审批的任务
     */
    @GetMapping("/queryTasks")
    @ApiOperation(value = "领导查看需要审批的任务")
    public String queryTasks(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        //根据任务的受理人，查询需要审批的任务
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee("赵会计").singleResult();
        //根据该任务的taskid，获取到对应的任务变量
        Map<String, Object> processVariables = taskService.getVariables(task.getId());
        System.out.println("assing0: " + processVariables.get("assing0"));
        System.out.println("assing0: " + processVariables.get("assing1"));
        System.out.println("assing0: " + processVariables.get("assing2"));
        System.out.println("assing0: " + processVariables.get("assing3"));
        System.out.println("num: " + processVariables.get("num"));
        System.out.println(processVariables.get("assing0") + "请假" + processVariables.get("num") + "天，" + processVariables.get("assing1") + processVariables.get("flag"));
        return "success";
    }


    /**
     * 李四审批，同事加入新的变量 flag，flag 为局部变量
     */
    @GetMapping("run3")
    @ApiOperation(value = "李四审批")
    public String processInstance3(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInstId)
                .taskAssignee("李四")
                .singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //加入局部变量
        processVariables.put("flag", true);
        taskService.complete(task.getId(), processVariables);
        return "success";
    }


    /**
     * 财务审批
     */
    @GetMapping("run4")
    @ApiOperation(value = "财务审批")
    public String processInstance4(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInstId)
                .taskAssignee("赵会计")
                .singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //加入局部变量
        processVariables.put("status", true);
        taskService.complete(task.getId(), processVariables);
        return "success";
    }


    /**
     * 总结经理审批 num>=3
     */
    @GetMapping("run5")
    @ApiOperation(value = "总结经理审批")
    public String processInstance5(String procInstId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInstId)
                .taskAssignee("王五")
                .singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //加入局部变量
        processVariables.put("command", "agree");
        taskService.complete(task.getId(), processVariables);
        return "success";
    }


    /**
     * 张三，李四，王五，赵会计查看历史任务
     */
    @GetMapping("historyTask")
    @ApiOperation(value = "张三，李四，王五，赵会计查看历史任务")
    public String historyTask(String name) {
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery().taskAssignee(name).list();
        taskList.forEach(task -> {
            System.out.println(task.getName() + "=====" + task.getEndTime());
            System.out.println(task.getId() + "=====" + task.getEndTime());
        });
        return "success";
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
}
