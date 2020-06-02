# Kudu Application

>   KUDU = กู do = กูทำเอง (when search for Kudu in google will be found the Horned Animal, that's it = มีเขาหล่อ = กูหล่อไง ทำเองใช้เอง^^)



### TECHNOLOGY LIST

| Name                                        | File / Version                                 | Description                                                  |
| ------------------------------------------- | ---------------------------------------------- | ------------------------------------------------------------ |
| Web Server                                  | Jboss Wildfly 18.0.1                           | required by the TechTeam (C-Level Servers)                   |
| JDK                                         | JDK 11<br />(tested on JDK 13 is working well) | deal with LocalDate.ofInstant by K.Thammasak                 |
| Java EE                                     | Java EE8                                       | in runtime, this is provided by Wildfly<br />in devtime, this is the library named 'javax-lib' |
| Spring Security                             | Spring Framework:<br />Spring Security v5.2.1  | SessionRegistry                                              |
| ~~Primefaces~~                              | ~~Primefaces 8.0~~                             | Primefaces 8.0 fix many issues about DatePicker and Calendar include security (XSS Attack)<br />but has many changes need to test before, this is not a time to test, no new features is required for this project. |
| ~~Primefaces~~                              | ~~Primefaces 7.0~~                             | Primefaces 7.0 has TimeOnly Issues in DatePicker and Calendar.<br /><br />bundled with<br />+ jQuery v3.3.1<br />+ jQuery UI v1.12.1 |
| Primefaces                                  | Primefaces 6.2                                 | Downgraded by New issues in Calendar when TimeOnly = True and no features of 7.0 is needed by this project.<br />Other projects that already Upgraded to 7.0 : need Upload UTF-8 file (AR) and it's already fixed in this version (speaker: Art Spider).<br />bundled with<br />+ jQuery v3.2.1<br />+ jQuery UI v1.12.1 |
| Apache POI                                  | Apache POI 3.17                                | as Dependencies of Primefaces 7.0 DataExporter (Excel) and some issues are fixed (know as Final Version of 3.1)<br />+ 10 dependency libraries are required by POI |
| ~~Apache POI~~                              | ~~Apache POI 3.13~~                            | as Dependencies of Primefaces 6.2 DataExporter (Excel).      |
| ~~Primefaces Extension~~                    | ~~Primefaces Extension 7.0.2~~                 | This extension already has Timepicker and Selenium ready but<br />this extension is not compatible with Bootstrap CSS (tested: Timesheet -> View/Enable will not operate).<br /><br />(tested on primefaces-extensions-7.0.2.jar) |
| Deprecated: Unused ~~jQuery Newer Version~~ | ~~jquery-3.4.1.slim.min.js~~                   | read this : https://stackoverflow.com/questions/25508564/how-to-solve-a-conflict-with-primefaces-jquery<br /><br /><br />already tested to replace jquery by this newer version (/resources/primefaces/jquery/jquery.js) it will make primefaces actions can't operate.<br />already tested to remove this newer version to see what's happen in timesheet, but still work as well. |
| Bootstrap CSS                               | Bootstrap v3.3.7                               | deal with the top-menu, side-menu and some UI states by K.Thammasak. |



### JAVA LIBRARY LIST

>   This project has 3 modules with 3 packed files, it's need to build and pack in this sequence.
>
>   1.  PJT-LIB : pjt-lib.jar
>   2.  API : api.war
>   3.  FRONT END : front.war

| Parent Folder / Group | Description                                                  | PJT-LIB                 | API                     | FRONT                   | pjt-lib.jar | api.war                 | front.war               |
| --------------------- | ------------------------------------------------------------ | ----------------------- | ----------------------- | ----------------------- | ----------- | ----------------------- | ----------------------- |
| api-lib               | from Handover api.war                                        | :ballot_box_with_check: | :ballot_box_with_check: |                         |             | :ballot_box_with_check: |                         |
| common-lib            | from Handover api.war<br />(pjt-lib.jar need to put in this group) | :ballot_box_with_check: | :ballot_box_with_check: | :ballot_box_with_check: |             | :ballot_box_with_check: | :ballot_box_with_check: |
| front-lib             | from Handover front.war<br />(poi and math for Export to Excel)<br />(theme for all alternate free primefaces themes)<br />(+10 libraries required by Primefaces DataExporter) | :ballot_box_with_check: |                         | :ballot_box_with_check: |             |                         | :ballot_box_with_check: |
| javax-lib             | from JavaEE 8 come with glassfish5                           | :ballot_box_with_check: | :ballot_box_with_check: | :ballot_box_with_check: |             |                         |                         |
| jboss-lib             | from Jboss Wildfly 18.0.1                                    | :ballot_box_with_check: |                         | :ballot_box_with_check: |             |                         |                         |
| resteasy-lib          | from Jboss Wildfly 18.0.1                                    | :ballot_box_with_check: |                         |                         |             |                         |                         |
| hibernate-lib         | from Jboss Wildfly 18.0.1<br />(jpa-model-gen from Goldspot Project) |                         | :ballot_box_with_check: |                         |             |                         |                         |
| mapstruct-lib         | from Goldspot Project                                        |                         | :ballot_box_with_check: |                         |             |                         |                         |
| dconvers-lib          | from LHBank ETL Project                                      |                         |                         | :ballot_box_with_check: |             |                         | :ballot_box_with_check: |
| mariadb-lib           | mariadb driver lib need to copied to Wildfly/modules folder<br />from Goldspot Project |                         |                         |                         |             |                         |                         |



### Wildfly Standalone.xml

>   Required: Datasource named 'kudu' need to defined in the Standalone.xml (/wildfly-18.0.1.Final/standalone/configuration/Standalone.xml)

```xml
<datasource jta="true" jndi-name="java:/kudu" pool-name="kudu" enabled="true" use-ccm="false">
    <connection-url>jdbc:mysql://localhost:3306/kudu</connection-url>
    <driver-class>org.mariadb.jdbc.Driver</driver-class>
    <driver>mariadb</driver>
    <pool>
        <min-pool-size>10</min-pool-size>
        <max-pool-size>10</max-pool-size>
    </pool>
    <security>
        <user-name>user</user-name>
        <password>password</password>
    </security>
</datasource>
<drivers>
    <driver name="mariadb" module="org.mariadb"/>
</drivers>
```



### Wildfly Standalone.conf

>   Required: resteasy.preferJacksonOverJsonB=false
>
>   Module: org.jboss.resteasy.resteasy-json-binding-provider
>
>   Module-JAR: resteasy-json-binding-provider-3.9.1.Final.jar
>
>   Refer: [RESTFul Web Services with Jboss EAP (RESTeasy)](https://docs.jboss.org/resteasy/docs/3.9.1.Final/userguide/html_single/index.html)

```c
#
# Specify options to pass to the Java VM.
#
if [ "x$JAVA_OPTS" = "x" ]; then
   JAVA_OPTS="-Xms64m -Xmx512m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=256m -Djava.net.preferIPv4Stack=true"
   JAVA_OPTS="$JAVA_OPTS -Djboss.modules.system.pkgs=$JBOSS_MODULES_SYSTEM_PKGS -Djava.awt.headless=true"
   JAVA_OPTS="$JAVA_OPTS -Dresteasy.preferJacksonOverJsonB=false"
else
   echo "JAVA_OPTS already set in environment; overriding default settings with values: $JAVA_OPTS"
fi
```



### API Web.xml

>   Resteasy Registry Listing : Used to see all available API and recheck server is working well.
>   URL: http://localhost:8080/api/rest/resteasy/registry

```xml
<context-param>
        <param-name>resteasy.resources</param-name>
        <param-value>org.jboss.resteasy.plugins.stats.RegistryStatsResource</param-value>
</context-param>
```



### GIT Branches and Application States

>   All 3 Modules of this project will be release at the same time and use single version control (version_control.xml).

| GIT Branch | Version Name                                                 | Application State        | Owner                   | Server               | Description                                                  |
| ---------- | ------------------------------------------------------------ | ------------------------ | ----------------------- | -------------------- | ------------------------------------------------------------ |
| develop    | Development<br />(no version number lets use git revision number instead) | New Featured and Fix Bug | Developer               | Localhost (notebook) | All branches that prefix by 'dev' are included in this state.<br />This state has many branches without Integration Testing (Unit Test Result may be required at this step). |
| alpha      | Alpha Version                                                | VIT/SIT                  | QA                      | DEV                  | Know as Vendor Integration Testing, after developer-leader already merge some develop branches into this branch with the version name is Alpha still need to test by Leader or QA before confirm to release to UAT. |
| beta       | Beta Version                                                 | UAT                      | User Acceptance Testing | UAT                  | Know as User Acceptance Testing, after QA confirm to release to UAT the developer-leader need to merge that alpha version into this branch and change the version name to Beta. |
| final      | Final Version                                                | Go Live                  | Sales / Users           | PROD                 | Know as Stable Version, after UAT process is completed, leader need to merge from beta branch into master branch and change the version name to Final before pack and sent to Sales or Deployment Team. |



### version_control.xml

>    Assume you stand in the IntelliJ IDEA and current project is KUDU.

1.  Right Click on version_control.xml and select 'Add as Ant Build File' you will see 'Ant Window' and all available targets.
2.  The artifact of 'PJT-LIB' need to enable 'Run Ant Target' and point to 'stamp build-date' (in the Pre-processing tab).
3.  The artifact of 'PJT-LIB' need to enable 'Include in project build'.



After this steps completed, when you build project the date will be stamped into version.property file and distributed to all modules before build artifact for the pjt-lib.jar immediately. 

The version.property is used in the front-end to show full version text on the bottom of screen.

>   Remark: When you want to change number of the version you can modify it directly or use Ant Targets defined in the Ant Window.
>   File: /front/src/main/resources/version.property



### KUDU Software Architecture

![Software_Architecture](assets/Software_Architecture.svg)



### TERMINOLOGY

| Term         | Description                                                  |
| ------------ | ------------------------------------------------------------ |
| Customer     | Partnership with the C-Level Co., Ltd.                       |
| User         | Employee of the C-Level Co., Ltd.                            |
| Rate         | (Not use for now) The Price of One Manday for Project.       |
| Project      | The Project is any Product sell to customer refer by PID.    |
| Project Task | The Project Task is sub task of the Product sold to customer refer by PID and Task ID.<br />Somebody know as chargeable tasks assigned to a user. |
| Task         | Template for Project Task.                                   |
| Holiday      | Specified day marked as a holiday for User and not count as working day of a year in %CU chapter. |
| Working Day  | Working Day of a month is <days-in-a-month> - <weekends> - <holidays-in-a-month>, working day is used in %CU calculation. |
| Screen       | Web pages that already listed in the Left Side Menu to show for a user filtered by Screen Permission (Role-Screen)<br />All screens need a defined code in a Screen(.java) class. |
| Function     | Specific Function defined in a Function(.java) class and used in many screens that refer to the function description (Role-Function) |
| Manday       | 1 mandays = 8 hours                                          |
| BMD          | Billable Mandays, entered mandays from Project Management screen. |
| PMD          | Plan Mandays, planned mandays from Project Task screen.      |
| AMD          | Actual Mandays, changed mandays from Time Sheet screen.      |
| RMD          | Remaining Mandays = BMD - PMD                                |
| %AMD         | (PMD - AMD) / PMD                                            |
| Weight       | %PMD / Net Workdays x %AMD                                   |
| Final %AMD   | Average Weight = sum(Weight) / recordCount                   |



### Database Table Notes

| Table Name    | Notes                                                        |
| ------------- | ------------------------------------------------------------ |
| wrk_timesheet | for the column 'Project Task' with Chargable Flag = false, will use column 'Task' instead and leave null for both columns 'Project' and 'Project Task', @2020.05.21 found this technique controlled by the UI of Timesheet Detail Dialog (not recommended by me) but not worry because of it's already work. |



### Dev Environment Passwords

>   The C-LEVEL VPN is required in order to use the information from this table.
>
>   Last updated: 2020.05.14

| Env. | Name                                                         | IP / URL                                     | User         | Password |
| ---- | ------------------------------------------------------------ | -------------------------------------------- | ------------ | -------- |
| DEV  | Cent OS 8<br />(SFTP Home Path: /Root/)<br />(SSH Home Path: ) | 192.168.88.19                                | root         | P@$$w0rd |
| DEV  | MariaDB 10.3.17                                              | 192.168.88.19:3360                           | root         | P@$$w0rd |
| DEV  | Wildfly 19.0.0 Web Console<br />(Open JDK 11.0.7 2020-04-14 LTS)<br />(Home Path: /opt/wildfly/)<br />(Deployment Path: /opt/wildfly/standalone/deployments/) | http://192.168.88.19:9990/console/index.html | wildflyadmin | P@$$w0rd |



### Deployment Notes

| Env. | Description                                                  |
| ---- | ------------------------------------------------------------ |
| DEV  | **Know Issues**: Deploy using Web Console will got some exception about class not found, not work!<br />**Recommended**: Copy war file to this path '/opt/wildfly/standalone/deployments' and wait for file-name.isDeployed is appeared and then check server.log<br />(The api.war need to deploy first follow by the front.war if it's successful without error) |
| DEV  | **Know Issues**: Time Zone on the server need to be 'Asia/Bangkok' +07 +0700 |
| DEV  | Know Issues: Got error #1 (know issue #1) when code in sys_config.name is not found in the SystemConfig.code (Enum Class). |
| DEV  | **C-Level 2020 VPN is Required**:<br />Front-end URL: http://192.168.88.19:8080/signin.jsf<br />API URL: http://192.168.88.19:8080/api/rest/resteasy/registry |
| DEV  | **Public URL Here (no VPN)**:<br />Front-end URL: http://6f3907e3bcf0.sn.mynetname.net:8888/signin.jsf<br />API URL: http://6f3907e3bcf0.sn.mynetname.net:8888/api/rest/resteasy/registry |
| PROD | Know Issues: @2020.05.16 Application Server use 8087 as internal port, so the front-end configuration need to change from 8080 to 8087 (see web.xml::param-name=rest.api.url)<br />Recommended: Pack war files and put to SFTP of DEV env and notify to Deployment Team (Tech Team)  and wait for response. |
| PROD | **Public URL Here (no VPN)**:<br />Front-end URL: https://kudu.the-c-level.com/signin.jsf<br />API URL: https://kudu.the-c-level.com/api/rest/resteasy/registry |



**Know Issue #1**

```
Caused by: java.lang.NullPointerException
	at java.base/java.util.concurrent.ConcurrentHashMap.putVal(ConcurrentHashMap.java:1011)
	at java.base/java.util.concurrent.ConcurrentHashMap.put(ConcurrentHashMap.java:1006)
	at deployment.api.war//com.clevel.kudu.api.system.Application.loadConfiguration(Application.java:41)
	at deployment.api.war//com.clevel.kudu.api.system.Application$Proxy$_$$_WeldClientProxy.loadConfiguration(Unknown Source)
	at deployment.api.war//com.clevel.kudu.api.system.ContextListener.contextInitialized(ContextListener.java:25)
```



#### Often Use Shell Commands

>   For DEV Environment.

| Command                      | Description                                                  |
| ---------------------------- | ------------------------------------------------------------ |
| systemctl status wildfly     | **Check wildfly service status**:<br /><br />[root@localhost ~]# systemctl status wildfly<br/>● wildfly.service - The WildFly Application Server<br/>   Loaded: loaded (/etc/systemd/system/wildfly.service; enabled; vendor preset: disabled)<br/>   **Active: active (running) since Tue 2020-05-12 11:58:49 EDT; 1 day 16h ago**<br/> Main PID: 6910 (launch.sh)<br/>    Tasks: 68 (limit: 23984)<br/>   Memory: 359.8M<br/>   CGroup: /system.slice/wildfly.service<br/>           ├─6910 /bin/bash /opt/wildfly/bin/launch.sh standalone standalone.xml 0.0.0.0<br/>           ├─6912 /bin/sh /opt/wildfly/bin/standalone.sh -c standalone.xml -b 0.0.0.0 -bmanagement=0.0.0.0<br/>           └─7005 java -D[Standalone] -server -Xms64m -Xmx512m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=256m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman -Djava.awt.headless=true><br/>May 12 11:58:49 localhost.localdomain systemd[1]: Started The WildFly Application Server.<br/>lines 1-12/12 (END) |
| sudo systemctl stop wildfly  | **stop Wildfly service**:<br /><br />[root@localhost ~]# sudo systemctl stop wildfly<br/>[root@localhost ~]# systemctl status wildfly<br/>● wildfly.service - The WildFly Application Server<br/>   Loaded: loaded (/etc/systemd/system/wildfly.service; enabled; vendor preset: disabled)<br/>   **Active: inactive (dead) since Thu 2020-05-14 04:50:14 EDT; 3s ago**<br/>  Process: 6910 ExecStart=/opt/wildfly/bin/launch.sh $WILDFLY_MODE $WILDFLY_CONFIG $WILDFLY_BIND (code=killed, signal=TERM)<br/> Main PID: 6910 (code=killed, signal=TERM)<br/><br/>May 12 11:58:49 localhost.localdomain systemd[1]: Started The WildFly Application Server.<br/>May 14 04:50:13 localhost.localdomain systemd[1]: Stopping The WildFly Application Server...<br/>May 14 04:50:14 localhost.localdomain systemd[1]: Stopped The WildFly Application Server. |
| sudo systemctl start wildfly | **start Wildfly service**:<br /><br />[root@localhost ~]# sudo systemctl start wildfly<br/>[root@localhost ~]# systemctl status wildfly<br/>● wildfly.service - The WildFly Application Server<br/>   Loaded: loaded (/etc/systemd/system/wildfly.service; enabled; vendor preset: disabled)<br/>   Active: active (running) since Thu 2020-05-14 04:51:41 EDT; 4min 17s ago<br/> Main PID: 17789 (launch.sh)<br/>    Tasks: 66 (limit: 23984)<br/>   Memory: 343.9M<br/>   CGroup: /system.slice/wildfly.service<br/>           ├─17789 /bin/bash /opt/wildfly/bin/launch.sh standalone standalone.xml 0.0.0.0<br/>           ├─17790 /bin/sh /opt/wildfly/bin/standalone.sh -c standalone.xml -b 0.0.0.0 -bmanagement=0.0.0.0<br/>           └─17886 java -D[Standalone] -server -Xms64m -Xmx512m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=256m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman -Djava.awt.headless=tru><br/>May 14 04:51:41 localhost.localdomain systemd[1]: Started The WildFly Application Server. |
| timedatectl                  | **Check Current Time Zone on the Server**:<br /><br />[root@localhost]# timedatectl<br/>               Local time: Fri 2020-05-22 12:09:31 +07<br/>           Universal time: Fri 2020-05-22 05:09:31 UTC<br/>                 RTC time: Fri 2020-05-22 05:09:31<br/>                Time zone: Asia/Bangkok (+07, +0700)<br/>System clock synchronized: yes<br/>              NTP service: active<br/>          RTC in local TZ: no |
| date                         | **Basic check Time Zone on the Server**:<br /><br />[root@localhost ~]# date<br/>Fri May 22 12:18:20 +07 2020 |



### Issue Priority for Fixer

>   @2020.05.27
>   Prazit: เรียงลำดับทำก่อนหลังตามนี้ไหมครับ bug, usability problem, performance problem, feature, enhancement
>   Phongsathorn: bug ก่อน ใช้ครับ แต่อันอื่น น่าจะดูตาม priority ประกอบด้วยนะครับ

| Column   | 1=first to see |
| -------- | -------------- |
| State    | 1              |
| Priority | 2              |
| Type     | 3              |

| State       | 1=first to do |
| ----------- | ------------- |
| IN PROGRESS | 1             |
| REOPEN      | 2             |
| OPEN        | 3             |
| SUBMITTED   | 4             |

| Priority | 1=first to do |
| -------- | ------------- |
| High     | 1             |
| Medium   | 2             |
| Low      | 3             |

| Type                | 1=first to do |
| ------------------- | ------------- |
| Bug                 | 1             |
| Usability Probelm   | 2             |
| Performance Problem | 3             |
| Feature             | 4             |
| Enhancement         | 5             |



----

-- end of document --