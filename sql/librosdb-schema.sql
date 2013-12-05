drop database if exists librosdb;
create database librosdb;

use librosdb;

create table users (
    username	varchar(20) not null primary key,
	name		varchar(70) not null,
	email		varchar(255) not null
);

create table libros (
	libroid 			int not null auto_increment primary key,
	titulo	 			varchar(100) not null,
	autor				varchar(100) not null,
	lengua				varchar(50) not null,
	edicion				varchar(100) not null,
	fecha_edicion		DATE not null,
	fecha_impresion		DATE not null,
	editorial			varchar(100) not null,
	lastUpdate			timestamp
);

create table resenas (
	resenaid 			int not null auto_increment primary key,
	libroid 			int not null,
	username	 		varchar(20) not null,
	lastUpdate			timestamp, 
	content				varchar(500) not null,
	foreign key(username) references users(username) ON DELETE CASCADE,
	foreign key(libroid) references libros(libroid) ON DELETE CASCADE
);