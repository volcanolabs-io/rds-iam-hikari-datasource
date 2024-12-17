package org.springframework.boot.autoconfigure.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@AutoConfiguration
@ConditionalOnMissingBean( DataSource.class )
@ConditionalOnProperty(
		name = "spring.datasource.type",
		havingValue = "io.volcanolabs.rds.RdsIamHikariDataSource",
		matchIfMissing = true
)
public class RdsDataSourceConfig {

	@Bean
	static HikariJdbcConnectionDetailsBeanPostProcessor jdbcConnectionDetailsHikariBeanPostProcessor(ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {
		return new HikariJdbcConnectionDetailsBeanPostProcessor( connectionDetailsProvider );
	}

	@Bean
	@ConfigurationProperties( prefix = "spring.datasource.hikari" )
	HikariDataSource dataSource(DataSourceProperties properties, JdbcConnectionDetails connectionDetails) {
		HikariDataSource dataSource = (HikariDataSource) createDataSource( connectionDetails, HikariDataSource.class, properties.getClassLoader() );
		if ( StringUtils.hasText( properties.getName() ) ) {
			dataSource.setPoolName( properties.getName() );
		}

		return dataSource;
	}


	@SuppressWarnings("unchecked")
	private static <T> T createDataSource(JdbcConnectionDetails connectionDetails, Class<? extends DataSource> type,
	                                      ClassLoader classLoader) {
		return (T) DataSourceBuilder.create(classLoader)
				.type(type)
				.driverClassName(connectionDetails.getDriverClassName())
				.url(connectionDetails.getJdbcUrl())
				.username(connectionDetails.getUsername())
				.password(connectionDetails.getPassword())
				.build();
	}
}
