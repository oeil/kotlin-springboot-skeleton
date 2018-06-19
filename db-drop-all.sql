alter table clock_action drop constraint if exists fk_clock_action_user_id;
drop index if exists ix_clock_action_user_id;

alter table clock_action drop constraint if exists fk_clock_action_office_id;
drop index if exists ix_clock_action_office_id;

drop table if exists clock_action;

drop table if exists office;

drop table if exists user;

