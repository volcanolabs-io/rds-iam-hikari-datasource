This is an extension to the Hikari datasource for IAM Role based access to RDS databases.<br/>
This is entirely based on the work of Tom de Vroomen in this article: https://blog.jdriven.com/2021/06/configure-hikari-connection-pool-when-using-rds-iam/ <br/>
The intention of this library is just to make his excellent work available as a maven dependency.

# rds-iam-hikari-datasource
Use IAM Role based access to RDS databases with Hikari datasource. (Spring Boot)

## Spring Boot Properties
>spring.datasource.type = io.volcanolabs.rds.RdsIamHikariDataSource


## CI/CD
![Maven Central Version](https://img.shields.io/maven-central/v/io.volcanolabs/rds-iam-hikari-datasource)

[![Maven Package](https://github.com/volcanolabs-io/rds-iam-hikari-datasource/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/volcanolabs-io/rds-iam-hikari-datasource/actions/workflows/maven-publish.yml)

[![CodeQL](https://github.com/volcanolabs-io/rds-iam-hikari-datasource/actions/workflows/codeql.yml/badge.svg)](https://github.com/volcanolabs-io/rds-iam-hikari-datasource/actions/workflows/codeql.yml)

[![Maven Central](https://img.shields.io/maven-central/v/io.volcanolabs/rds-iam-hikari-datasource)](https://central.sonatype.com/artifact/io.volcanolabs/rds-iam-hikari-datasource)
[![Maven Central Version](https://img.shields.io/maven-central/v/io.volcanolabs/rds-iam-hikari-datasource?style=flat-square)](https://central.sonatype.com/artifact/io.volcanolabs/rds-iam-hikari-datasource)
[![Maven Central](https://img.shields.io/maven-central/v/io.volcanolabs/rds-iam-hikari-datasource?style=flat-square&logo=apache-maven&logoColor=white)](https://central.sonatype.com/artifact/io.volcanolabs/rds-iam-hikari-datasource)
