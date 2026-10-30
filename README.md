This is an extension to the Hikari datasource for IAM Role based access to RDS databases.<br/>
This is entirely based on the work of Tom de Vroomen in this article: https://blog.jdriven.com/2021/06/configure-hikari-connection-pool-when-using-rds-iam/ <br/>
The intention of this library is just to make his excellent work available as a maven dependency.

# rds-iam-hikari-datasource
Use IAM Role based access to RDS databases with Hikari datasource. (Spring Boot)

## Spring Boot Properties
>spring.datasource.type = io.volcanolabs.rds.RdsIamHikariDataSource

This property is required. The library only activates when it is set to the value above; if it is unset or set to another
type, Spring Boot's own datasource configuration is used and no IAM tokens are generated.

The standard Hikari pool settings under `spring.datasource.hikari.*` are applied to the pool, for example:
>spring.datasource.hikari.minimum-idle = 2<br/>
>spring.datasource.hikari.maximum-pool-size = 5<br/>
>spring.datasource.hikari.idle-timeout = 30000

Connections above `minimum-idle` are only retired once they have been idle for `idle-timeout` (Hikari's default is 10
minutes), and only when `minimum-idle` is lower than `maximum-pool-size`. If `minimum-idle` is not set, Hikari keeps the
pool at `maximum-pool-size`, which defaults to 10.

## How it works
* A fresh IAM authentication token is generated each time the pool opens a new connection, using the AWS SDK default
  credentials provider chain.
* The region comes from the AWS SDK default region provider chain. Set the `RDS_REGION_OVERRIDE` environment variable to
  override it.
* The datasource is only created when the application has not defined its own `DataSource` bean.
* The auto-configuration is ordered before Spring Boot's `DataSourceAutoConfiguration`.

## Upgrading to 5.0.0
This release is built against Spring Boot 4.1.x. It contains two behavior changes:
* **`spring.datasource.hikari.*` is now honored.** In 4.0.4, setting `spring.datasource.type` to
  `io.volcanolabs.rds.RdsIamHikariDataSource` resulted in the datasource being created by Spring Boot's generic
  configuration, which does not bind `spring.datasource.hikari.*`. Settings such as `minimum-idle` were silently ignored
  and the pool ran on Hikari's defaults. If you had these properties set, they now take effect, so check the values.
* **`spring.datasource.type` must be set explicitly.** The auto-configuration no longer activates when the property is
  missing. Previously, `matchIfMissing` was enabled.


## CI/CD
![Maven Central Version](https://img.shields.io/maven-central/v/io.volcanolabs/rds-iam-hikari-datasource)

[![Maven Package](https://github.com/volcanolabs-io/rds-iam-hikari-datasource/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/volcanolabs-io/rds-iam-hikari-datasource/actions/workflows/maven-publish.yml)

[![CodeQL](https://github.com/volcanolabs-io/rds-iam-hikari-datasource/actions/workflows/codeql.yml/badge.svg)](https://github.com/volcanolabs-io/rds-iam-hikari-datasource/actions/workflows/codeql.yml)
