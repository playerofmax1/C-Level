# Kudu - Released Notes

## Kudu (Alpha) Version 1.2.0 build 14/5/2020 15:29
Fixed: KUDU-6: User Management after click V button this screen always show unexpected user list until refresh/reopen this screen.
Fixed: KUDU-8: Export timesheet to excel file (current month only).
Fixed: KUDU-9: Timesheet screen: need to show full text on column description.
Fixed: KUDU-14:
+ Add Billable MD field
+ Show on top section of manage project screen
       - Billable MD := entered value
       - Total Plan MD:= Sum of Total MD column
       - Remaining MD:= Billable MD - Total Plan MD (in RED when under ZERO)
InProgress: KUDU-10: Prepare UI for lock/unlock timesheet month by month.

----
-- end of document --