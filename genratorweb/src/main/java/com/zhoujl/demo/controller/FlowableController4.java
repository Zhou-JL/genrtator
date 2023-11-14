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
import java.util.List;
import java.util.Map;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-3-3 9:16
 * @see: com.zhoujl.demo.controller
 * @Version: 1.0
 * 多候选人模式
 */
@RestController
@RequestMapping("/flowable4")
@Slf4j
@Api(tags = "flowable—FlowableController4")
public class FlowableController4 {

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
                .addClasspathResource("processes/houxuanren.bpmn20.xml")
                .name("多候选人请假")
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
        variables.put("yonghu1", "张三");
        variables.put("yonghu2", "李四");
        variables.put("yonghu3", "王五");
        //这里的key  就是xml里面的process Id
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjiua--houxuanren", variables);
        System.out.println("processInstance.getId(): " + processInstance.getId());        //流程定义id
        System.out.println("processInstance.getDeploymentId(): " + processInstance.getDeploymentId());    //部署流程id
        return processInstance.getId();
    }


    /**
     * 根据登录的用户查询对应的可以拾取的任务（相当于谁登陆了，谁就是那个候选人，相当于从多个候选人中找出某个候选人）
     *  传参登录的用户，这里写死 张三
     */
    @GetMapping("query")
    @ApiOperation(value = "查询确定某个候选人")
    public void queryTaskCandidate() {
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processInstanceId("85001")    //流程定义ID
                //.processDefinitionId("qingjiua--houxuanren:1:82504")     //act_re_procdef中的ID_, 这句话相当于 .processInstanceId("85001")
                .taskCandidateUser("张三")     //指定候选人的名字
                .list();
        for (Task task : list) {
            System.out.println("task.getId()="+task.getId());
            System.out.println("task.getName()="+task.getName());
        }
    }




    /**
     * 候选人拾取任务，这一步确定候选人成为这个任务的实施者，候选人拾取了这个任务，其他人就没有办法拾取了
     */
    @GetMapping("claim")
    @ApiOperation(value = "候选人拾取任务")
    public void claimTaskCandidate() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("85001")    //流程定义ID
                //.processDefinitionId("qingjiua--houxuanren:1:82504")     //act_re_procdef中的ID_, 这句话相当于 .processInstanceId("85001")
                .taskCandidateUser("张三")     //指定候选人的名字
                .singleResult();
       if (task != null){
           //拾取对应的任务
           taskService.claim(task.getId(),"张三");    //这里的张三应该参数传入
           System.out.println("拾取任务成功");
       }
    }


    /**
     * 如果一个候选人拾取了任务又不想处理了，可以退还任务，任务的候选人需要被重新确定
     */
    @GetMapping("unclaim")
    @ApiOperation(value = "候选人退回任务")
    public void unclaimTaskCandidate() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("85001")    //流程定义ID
                //.processDefinitionId("qingjiua--houxuanren:1:82504")     //act_re_procdef中的ID_, 这句话相当于 .processInstanceId("85001")
                .taskAssignee("张三")     //指定候选人的名字
                .singleResult();
        if (task != null){
            //拾取对应的任务
            taskService.unclaim(task.getId());
            System.out.println("候选人退回任务成功");
        }
    }



    /**
    * 如果一个候选人拾取了任务又不想处理了，除了可以退还任务之外，还可以把他交接给其他候选人
    */

    @GetMapping("taskDo")
    @ApiOperation(value = "候选人将任务交给其他候选人")
    public void TaskCandidatedo() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("85001")    //流程定义ID
                //.processDefinitionId("qingjiua--houxuanren:1:82504")     //act_re_procdef中的ID_, 这句话相当于 .processInstanceId("85001")
                .taskAssignee("张三")     //指定候选人的名字
                .singleResult();
        if (task != null){
            //拾取对应的任务
            taskService.setAssignee(task.getId(),"王五");
            System.out.println("候选人张三将任务交给了候选人王五");
        }
    }


    /**
     * 王五处理任务
     */
    @GetMapping("run1")
    @ApiOperation(value = "王五处理任务")
    public String processInstance1() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("85001")
                .taskAssignee("王五")
                .singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //加入局部变量
//        processVariables.put("status", true);
        taskService.complete(task.getId(), processVariables);
        return "success";
    }


    /**
     * 王总审批
     */
    @GetMapping("run2")
    @ApiOperation(value = "王总审批")
    public String processInstance2() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("85001")
                .taskAssignee("wz")
                .singleResult();
        if (task == null) {
            throw new RuntimeException("task null");
        }
        //获取当前流程实例的所有变量
        Map<String, Object> processVariables = task.getProcessVariables();
        //加入局部变量
//        processVariables.put("status", true);
        taskService.complete(task.getId(), processVariables);
        return "success";
    }

}
