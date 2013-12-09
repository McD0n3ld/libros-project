

1. Comprueba que el directorio de trabajo sea libros-project/sql/
2. Crear los usuarios sql necesarios:

create user 'libros'@'localhost' identified by 'libros';
grant all privileges on librosdb.* to 'libros'@'localhost';
flush privileges;

create user 'realm'@'localhost' identified by 'realm';
grant all privileges on realmdb.* to 'realm'@'localhost';
flush privileges;
exit;

3. Login SQL con libros y volcado de datos.

mysql -u libros -p
libros
source librosdb-schema.sql;
exit;

4. Login SQL con realm y volcado de datos.

mysql -u realm -p
realm
source realmdb-schema.sql;
exit;

5. cd ../libros-server/libros-api

maven build package libros-api
Cargar en tomcat el .war de libros-api

6. Hacer las comprobaciones oportunas con POSTMAN.