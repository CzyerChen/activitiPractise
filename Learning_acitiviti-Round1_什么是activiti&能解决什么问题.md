> 由于想做一个工单流转，不小心就看到了这个流程框架，就拿起来试了试

### 什么是activiti?
- activiti 使用mybatis 作为底层持久层框架
- 服务接口依赖process engine执行
- 服务接口
|服务接口|说明|
|:---:|:---:|
|RepositoryService|仓库服务，用于管理仓库，比如部署或删除流程定义、读取流程资源等。|
|IdentifyService|身份服务，管理用户、组以及它们之间的关系|
|RuntimeService|运行时服务，管理所有正在运行的流程实例、任务等对象。|
|TaskService|任务服务，管理任务。|
|FormService|表单服务，管理和流程、任务相关的表单。|
|HistroyService|历史服务，管理历史数据。|
|ManagementService|引擎管理服务，比如管理引擎的配置、数据库和作业等核心对象。|
- 流程设计器 基于eclipse的designer
- 原生支持Spring
- 分离运行时数据和历史数据
```text
        建模           运行时            管理
   业务模型设计器      流程引擎          流程管理器
   开发模型设计器                      流程rest服务
```
- 功能组件

|组件|说明|
|:---:|:---:|
|流程引擎（Activiti Engine）|提供针对 BPMN 2.0 规范的解析；执行 、创建和管理流程实例与任务；以及查询历史记录并根据结果生成报表等功能。|
|业务模型设计器（Activiti Modeler）|由 Signavio 公司设计实现，适用于业务人员把需求转换为流程定义。|
|开发模型设计器（Activiti Designer）|开发人员可以导入业务需求人员用业务模型设计器设计的流程定义文件（ XML 格式），这样就可以进一步加工成为可以运行的流程定义信息 。|
|流程管理器（Activiti Explorer|用于管理仓库、用户、组、流程实例和任务等流程对象|
|流程 REST 服务（Activiti REST|提供 Restful 风格的服务，允许客户端以 JSON 的数据格式与引擎的 REST API 进行交互。|


### 生命周期
- 定义流程：需求成员收集业务需求，交由开发人员转化为计算机可以识别的流程定义
- 发布流程：开启流程，执行任务流程监听
- 执行流程：以任务驱动的方式执行
- 监控流程：根据执行结果作出处理
- 优化流程


### 基础知识
- bpmn -> 业务流程建模与标注
- 关于业务流程化操作的图形化模型


- 基础28张表
```text
#部署对象和流程定义相关表
SELECT * FROM `act_re_deployment`; #部署对象表

SELECT * FROM `act_re_procdef`; #流程定义表`fsop`

SELECT * FROM `act_ge_bytearray`; #资源文件表（一个存储xml，一个存储图片）

SELECT * FROM `act_ge_property`; #逐渐生成策略表（与Id相关）


#流程实例，执行对象，任务
SELECT * FROM `act_ru_execution`; #正在执行的执行对象表（正在执行的流程实例）

SELECT * FROM `act_hi_procinst` WHERE `END_TIME_` IS NULL; #流程实例的历史表（一个流程实例）

SELECT * FROM `act_ru_task`; #正在执行的任务表（只有节点是UserTask的才有数据）

SELECT * FROM `act_hi_taskinst`; #任务历史表（只有节点是UserTask的时候该表存在数据）

SELECT * FROM `act_hi_actinst`; #所有活动节点的历史表（其中包括任务也不包括任务）


#流程变量
SELECT * FROM `act_ru_variable`;#正在执行的流程变量表

SELECT * FROM `act_hi_varinst`; #历史流程变量表

SELECT * FROM `act_ru_identitylink` #任务表（个人任务、组任务）

SELECT * FROM `act_hi_identitylink` #任务历史表

SELECT * FROM `act_id_group` #角色表

SELECT * FROM `act_id_user` #用户表

SELECT * FROM `act_id_membership` #用户角色关联表
```
### activiti 编程方式
- 通过ProcessEngineConfiguration，进行编码注入,通过@Bean 可以将对象注入
```text
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration
                                                           .createStandaloneProcessEngineConfiguration();
        // 设置数据库信息
        processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver"); 
        processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/activiti?useUnicode=true&characterEndocing=utf8");
        processEngineConfiguration.setJdbcUsername("root");
        processEngineConfiguration.setJdbcPassword("root");
        
        // 设置数据库操作的设置
        processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        // 获取工作流的核心对象
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
```
- 配置文件方式
```text

        ProcessEngine processEngine = ProcessEngineConfiguration
                                                .createProcessEngineConfigurationFromResource("activiti.cfg.xml")
                                                .buildProcessEngine();

activiti.cfg.xml:
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
  xmlns:jee="http://www.springframework.org/schema/jee" xmlns:aop="http://www.springframework.org/schema/aop"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd
       http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.0.xsd
       http://www.springframework.org/schema/jee http://www.springframework.org/schema/jee/spring-jee-3.0.xsd
       http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.0.xsd">
       
    <bean id="processEngineConfiguration" class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
        <property name="jdbcDriver" value="com.mysql.jdbc.Driver"></property>
        <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/activiti?useUnicode=true&amp;characterEndocing=utf8"></property>
        <property name="jdbcUsername" value="root"></property>
        <property name="jdbcPassword" value="root"></property>
        <property name="databaseSchemaUpdate" value="true"></property>
    </bean>
</beans>
 
```
- spring 方式：activiti对Spring是有很好的支持
```text
在spring的配置文件中做简单配置，就能实现engine的注入
spring:
  activiti:
    check-process-definitions: false
    database-schema-update: true
    process-definition-location-prefix: classpath:/process
```

### 三种流程部署方式
- bpmn方式
```text
@Test
    public void test1(){
        //bytearray
        DeploymentBuilder builder = processEngine.getRepositoryService().createDeployment();
        Deployment deploy = builder.addClasspathResource("process/vholiday.bpmn").deploy();
        //获取部署流程ID，用于后面流转的调用 procdef
        String id = deploy.getId();
    }
```
- stream方式
    - 读取文件
    - .addInputStream("processVariables.bpmn", inputStreambpmn)
- zip包方式
    - 读取文件
    - .addZipInputStream(zipInputStream)





- 通过engine操作每一个service ，或者将每一个service通过配置注入，方便实用
```text
RuntimeService runtimeService = processEngine.getRuntimeService();

RepositoryService repositoryService = processEngine.getRepositoryService();

TaskService taskService = processEngine.getTaskService();

ManagementService managementService = processEngine.getManagementService();

IdentityService identityService = processEngine.getIdentityService();

HistoryService historyService = processEngine.getHistoryService();

FormService formService = processEngine.getFormService();
```

### 候选人的配置
- 在流程图中指定名称或者ID，这显然不可能是正式使用的逻辑
- 配置监听器，能够通过DelegateTask对象，获取对应流程的所有信息，比如你有配置一些关联，就可以在监听器中动态设置处理人
```text
public class ActivitiTaskListener  implements TaskListener {
    @Override
    public void notify(DelegateTask task) {
        /**
         * 可以根据全局或者缓存的记录进行分配，也可以通过jdbc的方式进行处理人的分配
         */
        task.setAssignee("动态处理人");
    }
}
```
- 在assignee处填写表达式，在开启流程任务/完成任务的时候，传入对象参数，以map的形式
```text
${teacher}

 Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("student", "test");
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRuntimeService()
                .startProcessInstanceById("xiujia:1:1304",variables);

```
### 与自身业务关联
- act_ru_exectution 表中有一个bussiness_key,表中还有proc_def_id，能够与你开启的流程相对应，也就是对应着一张流程详单

### 附录
- [参考1](https://www.cnblogs.com/fly-piglet/p/6014270.html)
- [参考2,相当完整，包含画图和框架集成的指导](https://blog.csdn.net/cs_hnu_scw/article/details/79059965)
- [参考3](https://www.jianshu.com/p/f96f699d6310)
- [参考4](https://blog.csdn.net/fanxiangru999/article/details/79074730)
- [参考5](https://blog.csdn.net/fanxiangru999/article/details/79381966)