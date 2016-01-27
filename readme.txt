
1. install Eclipse EE, Tomcat 8, SQL Server 2014

2. create database by runing the init.sql in SQL Server 2014.

3. import source code to Eclipse

4. modify resources/config.properties, this file is used for hibernate

   edit the jdbc_url, for example, jdbc_url=jdbc:sqlserver://localhost:1433;DatabaseName=OTS, jdbc_username=sa, jdbc_password=mahelong

   change localhost to the ip of the database, if tomcat is in the same machine then do not need to change. change jdbc_username to your database username

   and change jdbc_password to your database password.
  
   also modify resources/dbconfig.properties, this file is used for jdbc
   
   the corresponding properties are DATABASE_URL_ENTIRE, USERNAME, PASSWORD

5. export war file by runing the function of export in Eclipse.

6. deploy the war file to Tomcat, then startup Tomcat.

7. use http://localhost:8080/ots access OTS, localhost can be replaced by the ip of the machine which installs Tomcat.


Group member: Helong Ma hxm151530,  Yupeng Gao yxg140730, Qingchuan Zhao qxz150730, Chaoshun Zuo cxz153430


shhhhhh
123




