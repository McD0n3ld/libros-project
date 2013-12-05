create user 'libros'@'localhost' identified by 'libros';
grant all privileges on librosdb.* to 'libros'@'localhost';
flush privileges;
exit;