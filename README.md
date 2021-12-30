# rds-iam-hikari-datasource
Use IAM Role based access to RDS databases with Hikari datasource. (Spring Boot)

## Spring Boot Properties
>spring.datasource.type = io.volcanolabs.rds.RdsIamHikariDataSource

Connection max lifetime to 14 minutes as the RDS IAM token is valid for 15 minutes
>spring.datasource.hikari.max-lifetime = 840000