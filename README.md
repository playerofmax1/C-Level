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



### Software Architecture

![Software_Architecture](assets/Software_Architecture.svg)

----

-- end of document --