# Kudu - Released Notes



## Kudu (Alpha) Version 1.3.1
> From: Kudu (Alpha)(RC2) Version 1.2.4 build 29/5/2020 18:30
> Marked: Main screen layout are changed from Bootstrap-CSS to Primefaces6.2.

Fixed: KUDU-3: New Project Task need to send email after save.
Fixed: KUDU-15: create new Admin Settings Screen.
Fixed: KUDU-18: Need to remove BootstrapCSS
+   change title-bar from Bootstrap to Primefaces.
+   change side-bar(screen menu) from Bootstrap to Primefaces.
+   improve UI of Admin Setting Screen.
+   fix all impacts.
Fixed: KUDU-24: User Management: Delete user still show on screens.
+   Time Sheet : User dropdown-list
+   Time Sheet Summary : User dropdown-list
+   User Management : Main user list
+   User Management : Main user list > V-button > User List in both sides
+   Project Management : Main list > M-button > New/Edit-button > User dropdown-list
+   Improve UI/UX & fix a bug in Timesheet Detail Dialog.
Fixed: KUDU-29: Send email after extend task/MD for Employee.
+   Revise all 3 email-services to use one standard for content and subject, improve related UI.
Fixed: KUDU-30: Employee need to see all Holidays in this year.
Fixed: KUDU-31: Project Management : Closed Project need to lock timesheet.
+   Improve appearance: inputs on panelGrid (timesheet,project,manageProject,user).
SQL: V6__email_templates.sql



## Kudu (Alpha) Version 1.2.3 (RC2)

Fixed: KUDU-20: Duration display incorrectly
Fixed: KUDU-21: BillableMD: Reopen as Bug: 1MD = 8hours not 24hours.
Fixed: KUDU-25: Add Project Task - PlanMD change unit from HH:mm to mandays.
Fixed: KUDU-26: Timesheet Summary - Add columns PMD, AMD and %AMD with Final %AMD under Utilization
+   add target utilization to user, year by year (in database layer).

Fixed: KUDU-27: Need UI to edit Target Utilization
+   Save target utilization on TimeSheet Summary Screen.
+   Edit target utilization on User Screen.

Fixed: KUDU-26: REOPEN
+ Column "Plan" ขอเปลี่ยนเป็น คำว่า YES (จาก True) และ NO (จาก False)
+ (plannedLabel) สีที่ใช้แสดงราย Plan = YES ให้แสดงเป็น**สีเขียว** หรือ น้ำเงิน
+ (nonPlanLabel) สีที่ใช้แสดงราย Plan = NO ขอเปลี่ยนจาก สีแดง (สีแดง สื่อให้เข้าใจผิด ว่า ทำอะไรผิด หรือไม่ครับ) ขอเปลี่ยนเป็น **ดำ** หรือ เทาเข้ม ก็พอ เพื่อแสดงว่า ปกติ แต่ไม่ คิด Final AMD
+ (noProjectLabel) สีที่ใช้แสดงราย Plan = NO และ PID = A00X ให้เปลี่ยนจาก สีแดง เป็น **ดำ** หรือ เทาเข้ม

SQL: V5__modify_rpt_mandays.sql
More:
+   Improve layout of Manage Project Screen.



## Kudu (Alpha) Version 1.2.2 (RC3)

Fixed: KUDU-4: insert button beside Charge Hours.
Fixed: KUDU-8: Reopen: Export as XLSX, fix file name, fix project column and task column.
Fixed: KUDU-12: %CU Calculation.
Fixed: KUDU-13: %CU Summary Report.
Fixed: KUDU-16: Project - Search by project status
Fixed: KUDU-17: MD(Mandays) fields need validation message.
Fixed: KUDU-21: Project Management - Unit of Billable-MD should be Days.
Fixed: KUDU-22: Out Time before midday make Duration incorrectly.
SQL: V4__add_performance_year.sql
More: 
+ Improve TimeSheet UI, decimal Numbers in Summary need Round Half Up with 2 Decimal Points.
+ Improve the UX of Calendar Fields
+ Use same template for all screen (one standard)
+ downgrade to Primefaces 6.2 by the issues in Calendar when TimeOnly = True on Primefaces7.0 (no new features of 7.0 are needed by this project)
+ Remove sample data in SQL:V3__add_timesheet_month_locker.sql
+ Fix bad layouts some screens and some dialogs.



## Kudu (Alpha) Version 1.2.1

Fixed: KUDU-10: Lock/unlock timesheet month by month.
Fixed: KUDU-11: Time sheet screen: Add summary of each Project in a month.
SQL: V2__add_billableMD.sql
SQL: V3__add_timesheet_month_locker.sql



## Kudu (Alpha) Version 1.2.0

Fixed: KUDU-6: User Management: after click V button this screen always show unexpected user list until refresh/reopen this screen.
Fixed: KUDU-7: User Management: incorrect display list
Fixed: KUDU-8: Export timesheet to excel file (current month only).
Fixed: KUDU-9: Timesheet screen: need to show full text on column description.
Fixed: KUDU-14:
+ Add Billable MD field
+ Show on top section of manage project screen
       - Billable MD := entered value
       - Total Plan MD:= Sum of Total MD column
       - Remaining MD:= Billable MD - Total Plan MD (in RED when under ZERO)
SQL: V1__handover.sql



----
-- end of document --