source realmdb-schema.sql;
insert into users values('alicia', MD5('alicia'), 'Alicia', 'alicia@acme.com');
insert into user_roles values ('alicia', 'registered');

insert into users values('blas', MD5('blas'), 'Blas', 'blas@acme.com');
insert into user_roles values ('blas', 'registered');

insert into users values('admin', MD5('admin'), 'Admin', 'admin@acme.com');
insert into user_roles values ('admin', 'admin');

insert into users values('test', MD5('test'), 'Test', 'test@acme.com');
insert into user_roles values ('test', 'registered');