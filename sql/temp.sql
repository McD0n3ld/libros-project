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

insert into users values('alicia', 'Alicia', 'alicia@acme.com');
insert into users values('blas', 'Blas', 'blas@acme.com');
insert into users values('admin', 'admin', 'admin@acme.com');
insert into users values('test', 'test', 'admin@acme.com');
insert into libros (libroid,titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (1,'El clan de Oso cabernario', 'Periquito delso palotes', 'castellano','14','2001-02-01','2002-06-03','Aliansa');
insert into libros (libroid,titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (2,'El holocausto bajo la Lupa', 'Jurgen Graff', 'castellano','14','2001-04-01','2002-07-03','Graff');
insert into libros (libroid,titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (3,'Auschwitz los hechos y las leyendas', 'Robert Faurison','castellano', '14','2001-02-01','2002-06-03','Aliansa');
insert into libros (libroid,titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (4,'La fabula del Holocausto', 'Arthur Butz', 'castellano','14','2001-02-01','2002-06-03','Aliansa');
insert into libros (libroid,titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (5,'Auschwitz los hechos y las leyendas', 'Robert Faurison', 'castellano','14','2001-02-01','2002-06-03','Aliansa');
insert into libros (libroid,titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (6,'El drama de los Judios Europeos', 'Paul Rassiner', 'castellano','14','2001-02-01','2002-06-03','Aliansa');
insert into libros (libroid,titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (7,'La falsificacion historica de Anna Frank', 'Robert Faurison', 'castellano','14','2001-02-01','2002-06-03','Aliansa');
insert into libros (libroid,titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (8,'Mi lucha', 'Adolf Hitler', 'castellano','14','2001-02-01','2002-06-03','Aliansa');
insert into libros (libroid,titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (9,'Zweites buch', 'Adolf Hitler', 'castellano','14','2001-02-01','2002-06-03','Aliansa');
insert into libros (libroid,titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (10,'Guia de la Buena Esposa ', 'Ministerio de la Familia, Franquismo', 'castellano','14','2001-02-01','2002-06-03','Aliansa');
insert into libros (libroid,titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (11,'Como encontrar Rojos', 'Capitania General de Madrlibroid', 'castellano','14','2001-02-01','2002-06-03','Aliansa');
insert into libros (libroid,titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values (12,'Guia de Git ', 'Sergio Machado', 'castellano','14','2001-02-01','2002-06-03','Aliansa');
insert into resenas (resenaid,libroid, username, content) values (1,1, 'blas', 'Claramente se ve que esta escrito por una ama de casa mal follada que espera que un prehistorico le haga sentir como en un examen final de API');
insert into resenas (resenaid,libroid, username, content) values (2,1, 'alicia', 'Me gusta el concepto de convivencia y dos culturas de Neanertales y Sapiens pero no creo que este libro refleje la realidad sino mas bien, nuestro pesame historico');
insert into resenas (resenaid,libroid, username, content) values (3,2, 'blas', 'Un buen Libro, realmente senti la inspiracion bajo la luna, de la nuves de ceniza de los campos y el olor de la muerte');
insert into resenas (resenaid,libroid, username, content) values (4,2, 'alicia', 'Meparec eunlibro muy realista te muestra cuan malvados y maquiavelicos llegamos a ser');
insert into resenas (resenaid,libroid, username, content) values (5,3, 'blas', 'Claramente todo eso del Holocausto s una mentira que idearon los jodidos judios,para implantar su estado de Israel en palestina con al ayuda dela ONU');
insert into resenas (resenaid,libroid, username, content) values (6,6, 'alicia', 'Drama??? quereis decir el exterminio y fin de lso judios... o alguien conoce a alguno?');
insert into resenas (resenaid,libroid, username, content) values (7,7, 'blas', 'Anna frank sobrevivio, lo que pasa que queda feo decir que sobrevivio arrodillandose en los despachos de coronel del campo.');
insert into resenas (resenaid,libroid, username, content) values (8,8, 'alicia', 'Un gran libro que me inspira a seguir con la tarea iniciada por el creador, y continuar con la matanza indiscriminada de judios');
insert into resenas (resenaid,libroid, username, content) values (9,9, 'blas', 'La segunda secuela del gran libro lastima que el autor muriese antes del 3� libro');
insert into resenas (resenaid,libroid, username, content) values (10,10, 'alicia', 'Uno de los libros mas motivadores para que las mujeres empezaran a trabajar.');
insert into resenas (resenaid,libroid, username, content) values (11,11, 'blas', 'Facil ... ves a Rusia');
insert into resenas (resenaid,libroid, username, content) values (12,12, 'alicia', 'El mejor libroy que mas me ha ilustrado de toda la Carrera.');
insert into resenas (resenaid,libroid, username, content) values (13,12, 'blas', 'Un gran tutorial y muy util ... pero aun asi en el examen :s sufri...');