alter table wrk_project 
	add billableMD decimal(19,2) null;

alter table wrk_project
	add billableMDDuration varchar(255) null;

alter table wrk_project
	add billableMDMinute bigint null;
