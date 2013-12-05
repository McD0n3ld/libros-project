drop database if exists realmdb;
create database realmdb;
 
use realmdb;
 
create table users (
	username	varchar(20) not null primary key,
	userpass	char(32) not null,
	name		varchar(70) not null,
	email		varchar(255) not null
);
 
create table user_roles (
	username			varchar(20) not null,
	rolename 			varchar(20) not null,
	foreign key(username) references users(username) on delete cascade,
	primary key (username, rolename)
);