create table Bundle (
    base_name varchar(255) not null,
    country varchar(255) not null,
    key varchar(255) not null,
    language varchar(255) not null,
    variant varchar(255) not null,
    last_modified int8 not null,
    value text not null,
    primary key (base_name, country, key, language, variant)
);
