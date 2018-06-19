create table clock_action (
  id                            integer auto_increment not null,
  timestamp                     timestamp,
  type                          integer not null,
  description                   varchar(255) not null,
  user_id                       integer,
  office_id                     integer,
  constraint pk_clock_action primary key (id)
);

create table office (
  id                            integer auto_increment not null,
  name                          varchar(255) not null,
  constraint uq_office_name unique (name),
  constraint pk_office primary key (id)
);

create table user (
  id                            integer auto_increment not null,
  name                          varchar(255) not null,
  constraint uq_user_name unique (name),
  constraint pk_user primary key (id)
);

create index ix_clock_action_user_id on clock_action (user_id);
alter table clock_action add constraint fk_clock_action_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;

create index ix_clock_action_office_id on clock_action (office_id);
alter table clock_action add constraint fk_clock_action_office_id foreign key (office_id) references office (id) on delete restrict on update restrict;

