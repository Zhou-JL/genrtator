package com.zhoujl.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
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
 * @time: 2023-3-3 10:22
 * @see: com.zhoujl.demo.controller
 * @Version: 1.0
 * 候选人祖模式， 当候选人很多的情况下，可以分组处理，先创建租，然后把用户分配到多个组中
 */
@RestController
@RequestMapping("/flowable5")
@Slf4j
@Api(tags = "flowable—FlowableController5")
public class FlowableController5 {

    @Autowired
    ProcessEngine processEngine;

    /**
     * 创建用户
     */
    @GetMapping("createUser")
    @ApiOperation(value = "创建用户")
    public String createUser() {
        IdentityService identityService = processEngine.getIdentityService();
        //通过 IdentityService 完成相关的用户和租 的管理
        User user1 = identityService.newUser("张无忌");
        user1.setFirstName("张");
        user1.setLastName("无忌");
        user1.setEmail("zhangwuji@qq.com");
        user1.setPassword("123456");
        identityService.saveUser(user1);
        User user2 = identityService.newUser("周芷若");
        user2.setFirstName("周");
        user2.setLastName("芷若");
        user2.setEmail("zhouzhiruo@qq.com");
        user2.setPassword("123456");
        identityService.saveUser(user2);
        User user3 = identityService.newUser("赵敏");
        user3.setFirstName("赵");
        user3.setLastName("敏");
        user3.setEmail("zhaomin@qq.com");
        user3.setPassword("123456");
        identityService.saveUser(user3);
        return "success";

    }



    /**
     * 创建用户组
     */
    @GetMapping("createUserGroup")
    @ApiOperation(value = "创建用户组")
    public String createUserGroup() {
        IdentityService identityService = processEngine.getIdentityService();
        //通过 IdentityService 完成相关的用户和租 的管理
        Group group1 = identityService.newGroup("group1");
        group1.setName("人力资源部");
        group1.setType("type1");
        identityService.saveGroup(group1);
        Group group2 = identityService.newGroup("group2");
        group2.setName("法务部");
        group2.setType("type2");
        identityService.saveGroup(group2);
        return "success";

    }


    /**
     * 分配用户到对应的组中
    */
    @GetMapping("apiUserGroup")
    @ApiOperation(value = "分配用户到对应的组中")
    public String apiUserGroup() {
        IdentityService identityService = processEngine.getIdentityService();
        //通过 IdentityService 完成相关的用户和租 的管理
        Group group = identityService.createGroupQuery().groupId("group1").singleResult();
        List<User> list = identityService.createUserQuery().list();
        for (User user : list) {
            identityService.createMembership(user.getId(),group.getId());
        }
        return "success";

    }



    /**
     * 部署流程
     */
    @GetMapping("deploy")
    @ApiOperation(value = "部署流程")
    public String deploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/yonghuzu-group.bpmn20.xml")
                .name("分组请假")
                .deploy();
        System.out.println("deployment.getId(): " + deployment.getId());       //部署id,删除部署流程是需要
        System.out.println("deployment.getName(): " + deployment.getName());
        return "success";

    }


    /**
     * 启动流程定义  act_re_procdef  的 ID_,   参数是前端传递的那个组
     */
    @GetMapping("run")
    @ApiOperation(value = "启动流程定义")
    public String processInstance(String gropuid) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        IdentityService identityService = processEngine.getIdentityService();
        //通过 IdentityService 完成相关的用户和租 的管理
        //根据groupId 查询group
        Group gp = identityService.createGroupQuery().groupId(gropuid).singleResult();
        Map<String, Object> variables = new HashMap<>();
        variables.put("g1", gp.getId());     //给个g1 设置组
        //这里的key  就是xml里面的process Id
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("group-yonghuzu", variables);
        System.out.println("processInstance.getId(): " + processInstance.getId());        //流程定义id
        System.out.println("processInstance.getDeploymentId(): " + processInstance.getDeploymentId());    //部署流程id
        return processInstance.getId();
    }



    /**
     * 根据登录的用户查询对应的可以拾取的任务（相当于谁登陆了，谁就是那个候选人，相当于从多个候选人中找出某个候选人）
     * 传参 应该是登录的用户，这里写死 张无忌
     */
    @GetMapping("query")
    @ApiOperation(value = "查询确定某个候选人")
    public void queryTaskCandidate() {
        TaskService taskService = processEngine.getTaskService();
        IdentityService identityService = processEngine.getIdentityService();
        //查询当前用户所在的组
        Group group = identityService.createGroupQuery().groupMember("张无忌").singleResult();
        //查询这个组可以拾取的任务
        List<Task> list = taskService.createTaskQuery()
                .processInstanceId("100005")    //流程定义ID
                //.processDefinitionId("qingjiua--houxuanren:1:82504")     //act_re_procdef中的ID_, 这句话相当于 .processInstanceId("85001")
                .taskCandidateGroup(group.getId())     //指定组ID
                .list();
        for (Task task : list) {
            System.out.println("task.getId()="+task.getId());
            System.out.println("task.getName()="+task.getName());
        }
    }


    /**
     * 用户组中的张无忌拾取任务，这一步确定张无忌成为这个任务的实施者，张无忌拾取了这个任务，其他人就没有办法拾取了
     */
    @GetMapping("claim")
    @ApiOperation(value = "候选人拾取任务")
    public void claimTaskCandidate() {
        String userId = "张无忌";     //应该参数传入
        TaskService taskService = processEngine.getTaskService();
        IdentityService identityService = processEngine.getIdentityService();
        //查询当前用户所在的组
        Group group = identityService.createGroupQuery().groupMember(userId).singleResult();
        Task task = taskService.createTaskQuery()
                .processInstanceId("100005")    //流程定义ID
                //.processDefinitionId("qingjiua--houxuanren:1:82504")     //act_re_procdef中的ID_, 这句话相当于 .processInstanceId("85001")
                .taskCandidateGroup(group.getId())     //指定组ID
                .singleResult();
        if (task != null){
            //拾取对应的任务
            taskService.claim(task.getId(),userId);
            System.out.println("拾取任务成功");
        }
    }



    /**
     * 如果一个候选人拾取了任务又不想处理了，可以退还任务，任务的候选人需要被重新确定
     */
    @GetMapping("unclaim")
    @ApiOperation(value = "候选人退回任务")
    public void unclaimTaskCandidate() {
        String userId = "张无忌";     //应该参数传入
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("100005")    //流程定义ID
                //.processDefinitionId("qingjiua--houxuanren:1:82504")     //act_re_procdef中的ID_, 这句话相当于 .processInstanceId("85001")
                .taskAssignee(userId)     //指定组ID
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
    @ApiOperation(value = "用户组成员将任务交给其他成员")
    public void TaskCandidatedo() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("100005")    //流程定义ID
                //.processDefinitionId("qingjiua--houxuanren:1:82504")     //act_re_procdef中的ID_, 这句话相当于 .processInstanceId("85001")
                .taskAssignee("张无忌")     //指定候选人的名字
                .singleResult();
        if (task != null){
            //拾取对应的任务
            taskService.setAssignee(task.getId(),"赵敏");
            System.out.println("用户组成员张无忌将任务交给了用户组成员赵敏");
        }
    }


    /**
     * 赵敏处理任务
     */
    @GetMapping("run1")
    @ApiOperation(value = "赵敏处理任务")
    public String processInstance1() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("100005")
                .taskAssignee("赵敏")
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
     * lisi审批
     */
    @GetMapping("run2")
    @ApiOperation(value = "lisi审批")
    public String processInstance2() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("100005")
                .taskAssignee("lisi")
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
