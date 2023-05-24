package com.atguigu.auth.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProcessTest4 {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    /**
     * 部署流程定义，启动流程实例
     */
    @Test
    public void deployAndStartProcess() {
        //部署流程定义
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban04.bpmn20.xml")
                .name("加班申请流程04")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());

        //启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban04");
        System.out.println(processInstance.getId());
    }

    /**
     * 查询组任务
     */
    @Test
    public void findGroupTaskList() {
        //查询组任务
        List<Task> list = taskService.createTaskQuery()
                .taskCandidateUser("zhangsan01")//MySQL中sys_user表中必须有这个candidate
                .list();
        for (Task task : list) {
            System.out.println("----------------------------");
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    /**
     * 认领任务
     */
    @Test
    public void claimTask(){
        //拾取任务,即使该用户不是候选人也能拾取(建议拾取时校验是否有资格)
        Task task = taskService.createTaskQuery()
                .taskCandidateUser("zhangsan01")
                .singleResult();
        if(task!=null){
            //如果有task，则拾取任务
            taskService.claim(task.getId(), "zhangsan01");
            System.out.println("任务认领成功");
        }
    }

    /**
     * 查询用户当前待执行的任务
     */
    @Test
    public void findPendingTaskList() {
        //任务负责人
        String assignee = "zhangsan01";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)//只查询该任务负责人的任务
                .list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    /**
     * 完成任务
     */
    @Test
    public void completGroupTask() {
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhangsan01")  //要查询的负责人
                .singleResult();//返回一条
        taskService.complete(task.getId());
    }

    /**
     * 归还组任务
     */
    @Test
    public void returnGroupTask() {
        String taskId = "d96c3f28-825e-11ed-95b4-7c57581a7819";
        // 任务负责人
        String userId = "zhangsan01";
        // 校验userId是否是taskId的负责人，如果是负责人才可以归还组任务
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .taskAssignee(userId)
                .singleResult();
        //归还任务
        if (task != null) {
            taskService.setAssignee(taskId, null);
        }
    }

    /**
     * 任务交接。任务负责人将任务交给其它候选人办理该任务。
     */
    @Test
    public void handover() {
        // 当前待办任务
        String taskId = "d96c3f28-825e-11ed-95b4-7c57581a7819";
        // 校验zhangsan01是否是taskId的负责人，如果是负责人才可以归还组任务
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .taskAssignee("zhangsan01")
                .singleResult();
        if (task != null) {
            // 将此任务交给其它候选人zhangsan02办理
            taskService.setAssignee(taskId, "zhangsan02");
        }
    }

}

