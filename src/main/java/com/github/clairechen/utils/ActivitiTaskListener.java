package com.github.clairechen.utils;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;


public class ActivitiTaskListener  implements TaskListener {
    private FixedValue roleCodeName; // 同流程图中fieldName


    @Override
    public void notify(DelegateTask task) {
        /**
         * 可以根据全局或者缓存的记录进行分配，也可以通过jdbc的方式进行处理人的分配
         */
         task.setAssignee("动态处理人");

        /*
            try {
                Object value = roleCodeName.getValue(task); // 获取fieldName的值
                String roleCode = PropertiesRead.getInstance().read(
                        String.valueOf(value));
                ApplicationContext context = ContextLoader
                        .getCurrentWebApplicationContext();
                //通过applicationcontext 获取一个全局bean，进行业务逻辑上的处理
                personSecurityService = (PersonSecurityService) context
                        .getBean("personSecurityService");
                List<String> users = personSecurityService
                        .findIdByRoleCode(roleCode);
                task.addCandidateUsers(users);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("指定任务代办人失败：" + e.getMessage());
            }*/
    }
}
