This is an extension to the Hikari datasource for IAM Role based access to RDS databases.<br/>
This is entirely based on the work of Tom de Vroomen in this article: https://blog.jdriven.com/2021/06/configure-hikari-connection-pool-when-using-rds-iam/ <br/>
The intention of this library is just to make his excellent work available as a maven dependency.

# rds-iam-hikari-datasource
Use IAM Role based access to RDS databases with Hikari datasource. (Spring Boot)

## Spring Boot Properties
>spring.datasource.type = io.volcanolabs.rds.RdsIamHikariDataSource
