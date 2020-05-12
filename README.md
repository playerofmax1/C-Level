# Kudu Application



### TECHNOLOGY LIST

| Name       | File / Version       | Description                                                  |
| ---------- | -------------------- | ------------------------------------------------------------ |
| Web Server | Jboss Wildfly 18.0.1 | required by the TechTeam (C-Level Servers)                   |
| JDK        | JDK 13               | deal with LocalDate.ofInstant by K.Thammasak                 |
| Java EE    | Java EE8             | in runtime, this is provided by Wildfly<br />in devtime, this is the library named 'javax-lib' |



### JAVA LIBRARY LIST

| Parent Folder / Group | Description                                                  | PJT-LIB                 | API                     | FRONT                   | pjt-lib.jar | api.war                 | front.war               |
| --------------------- | ------------------------------------------------------------ | ----------------------- | ----------------------- | ----------------------- | ----------- | ----------------------- | ----------------------- |
| api-lib               | from Handover api.war                                        | :ballot_box_with_check: | :ballot_box_with_check: |                         |             | :ballot_box_with_check: |                         |
| common-lib            | from Handover api.war<br />(pjt-lib.jar need to put in this group) | :ballot_box_with_check: | :ballot_box_with_check: | :ballot_box_with_check: |             | :ballot_box_with_check: | :ballot_box_with_check: |
| front-lib             | from Handover front.war                                      | :ballot_box_with_check: |                         | :ballot_box_with_check: |             |                         | :ballot_box_with_check: |
| javax-lib             | from JavaEE 8 come with glassfish5                           | :ballot_box_with_check: | :ballot_box_with_check: | :ballot_box_with_check: |             |                         |                         |
| jboss-lib             | from Jboss Wildfly 18.0.1                                    | :ballot_box_with_check: |                         | :ballot_box_with_check: |             |                         |                         |
| hibernate-lib         | from Jboss Wildfly 18.0.1<br />(jpa-model-gen from Goldspot Project) |                         | :ballot_box_with_check: |                         |             |                         |                         |
| mapstruct-lib         | from Goldspot Project                                        |                         | :ballot_box_with_check: |                         |             |                         |                         |
| resteasy-lib          | from Goldspot Project                                        | :ballot_box_with_check: |                         |                         |             |                         |                         |
| mariadb-lib           | mariadb driver lib need to copied to Wildfly/modules folder<br />from Goldspot Project |                         |                         |                         |             |                         |                         |



### Wildfly Standalone.xml

>   Required: Datasource named 'kudu' in the /wildfly-18.0.1.Final/standalone/configuration/standalone.xml

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



### Wildfly Standalone.bat

>   Required: resteasy.preferJacksonOverJsonB=false
>
>   Module: org.jboss.resteasy.resteasy-json-binding-provider
>
>   Module-JAR: resteasy-json-binding-provider-3.9.1.Final.jar
>
>   Refer: [RESTFul Web Services with Jboss EAP (RESTeasy)](https://docs.jboss.org/resteasy/docs/3.9.1.Final/userguide/html_single/index.html)

```c

rem pz.Fix JSONB DateFormat error, pls find text after rem below
set "JAVA_OPTS= -Duser.timezone=GMT+7 -Duser.language=en -Duser.region=EN -Duser.country=US -Djava.net.preferIPv4Stack=true -Dresteasy.preferJacksonOverJsonB=false %JAVA_OPTS%"

rem Setup JBoss specific properties
set "JAVA_OPTS=-Dprogram.name=%PROGNAME% %JAVA_OPTS%"

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





### Software Architecture

![Software_Architecture](assets/Software_Architecture.svg)

----

-- end of document --