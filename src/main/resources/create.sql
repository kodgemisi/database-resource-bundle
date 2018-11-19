create table Bundle (
  id bigint not null,
  name varchar(255) not null,
  language varchar(255) not null,
  country varchar(255) not null,
  variant varchar(255) not null,
  key varchar(255) not null,
  value clob not null,
  last_modified bigint not null,
  primary key (id)
);

alter table bundle add constraint UC_bundle_constraint unique (name, key, language, country, variant);