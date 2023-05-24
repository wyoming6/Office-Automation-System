package com.atguigu.auth.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        if(delegateTask.getName().equals("经理审批")){
            //指定任务负责人
            delegateTask.setAssignee("Jack");
        } else if(delegateTask.getName().equals("人事审批")){
            delegateTask.setAssignee("Enis");
        }
    }
}
