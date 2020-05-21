# Kudu - Released Notes




## Kudu (Alpha) Version 1.2.2 (RC2)

Fixed: KUDU-4: insert button beside Charge Hours.
Fixed: KUDU-8: Reopen: Export as XLSX, fix file name, fix project column and task column.
Fixed: KUDU-12: %CU Calculation.
Fixed: KUDU-13: %CU Summary Report.
Fixed: KUDU-16: Project - Search by project status
Fixed: KUDU-17: MD(Mandays) fields need validation message.
SQL: V4__add_performance_year.sql
More: 
+ Improve TimeSheet UI, decimal Numbers in Summary need Round Half Up with 2 Decimal Points.
+ Improve the UX of Calendar Fields
+ Use same template for all screen (one standard)
+ downgrade to Primefaces 6.2 by the issues in Calendar when TimeOnly = True on Primefaces7.0 (no new features of 7.0 are needed by this project)
+ Remove sample data in SQL:V3__add_timesheet_month_locker.sql



## Kudu (Alpha) Version 1.2.1

Fixed: KUDU-10: Lock/unlock timesheet month by month.
Fixed: KUDU-11: Time sheet screen: Add summary of each Project in a month.
SQL: V2__add_billableMD.sql
SQL: V3__add_timesheet_month_locker.sql



## Kudu (Alpha) Version 1.2.0

Fixed: KUDU-6: User Management after click V button this screen always show unexpected user list until refresh/reopen this screen.
Fixed: KUDU-7: 
Fixed: KUDU-8: Export timesheet to excel file (current month only).
Fixed: KUDU-9: Timesheet screen: need to show full text on column description.
Fixed: KUDU-14:
+ Add Billable MD field
+ Show on top section of manage project screen
       - Billable MD := entered value
       - Total Plan MD:= Sum of Total MD column
       - Remaining MD:= Billable MD - Total Plan MD (in RED when under ZERO)
InProgress: KUDU-10: Prepare UI for lock/unlock timesheet month by month.
SQL: V1__handover.sql



----
-- end of document --