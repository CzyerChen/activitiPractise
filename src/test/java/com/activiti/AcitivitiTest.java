package com.activiti;


import com.github.clairechen.ActivitiApplication;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.*;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ActivitiApplication.class)
public class AcitivitiTest {

    @Resource
    private ProcessEngine processEngine;

    /**
     * 创建部署流程
     * 可以通过xml或者bpmn的文件
     * 可以通过inputstream ，也就是读文件
     * 可以通过读取压缩包zip包的形式
     */
    @Test
    public void test1(){
        //bytearray
        DeploymentBuilder builder = processEngine.getRepositoryService().createDeployment();
        Deployment deploy = builder.addClasspathResource("process/vholiday.bpmn").deploy();
        //获取部署流程ID，用于后面流转的调用 procdef
        String id = deploy.getId();

    }


    /**
     * 开启休假申请流程
     */
    @Test
    public void test2(){
        //启动流程 id来自于deploy后的ID procdef
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById("xiujialiucheng:1:4");
        String id = processInstance.getId();
    }

    /**
     * 完成申请的提交，经理完成审批，这个结果主要有complete中的id决定
     */
    @Test
    public void test3(){
        //完成某一个流程，ID来自于processInstance的ID task
        processEngine.getTaskService().complete("2505");
    }


    @Test
    public void test4(){
        List<Task> list = processEngine.getTaskService()
                .createTaskQuery()
                .taskAssignee("经理")
                .list();
        System.out.println(Arrays.toString(list.toArray()));
    }

    /**
     * 删除一个流程
     */
    @Test
    public void test5(){
        processEngine.getRepositoryService().deleteDeployment("id",true);
    }


    /**
     * 根据名称查询流程，流程都在repo里面
     */
    @Test
    public  void test6(){
        List<Deployment> list = processEngine.getRepositoryService()
                .createDeploymentQuery()
                .orderByDeploymenTime()
                .desc()
                .deploymentName("xiujia")
                .list();
    }

    /**
     * 查询流程定义
     */
    @Test
    public void test7(){
        List<ProcessDefinition> list = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion()
                .list();
    }

    /**
     * 根据dpid/id/name来获取流程图
     */
    @Test
    public void test8() throws IOException {
        InputStream processDiagram = processEngine.getRepositoryService()
                .getResourceAsStream("5002","xiujia.png");
                //.getProcessDiagram("xiujialiucheng:1:4");


        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/usr/local/test.png")));
        int b =-1;
        while((b = processDiagram.read()) != -1){
            bufferedOutputStream.write(b);
        }
        processDiagram.close();
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

}
