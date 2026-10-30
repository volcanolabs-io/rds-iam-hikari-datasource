package org.springframework.boot.jdbc.autoconfigure;

import com.zaxxer.hikari.HikariDataSource;
import io.volcanolabs.rds.RdsIamHikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AutoConfigurations.of(...) applies the same before/after sorting as a real application, so these tests
 * fail if this configuration is ever ordered behind Boot's own DataSourceAutoConfiguration again.
 * The pool is lazy, so nothing connects to the database.
 */
class RdsDataSourceConfigTest {

	private final ApplicationContextRunner runner = new ApplicationContextRunner()
			.withConfiguration( AutoConfigurations.of( DataSourceAutoConfiguration.class, RdsDataSourceConfig.class ) )
			.withPropertyValues(
					"spring.datasource.url=jdbc:h2:mem:rdstest",
					"spring.datasource.username=placeholder_user",
					"spring.datasource.hikari.minimum-idle=2",
					"spring.datasource.hikari.maximum-pool-size=5"
			);

	@Test
	void rdsTypeCreatesIamDataSourceAndBindsHikariProperties() {
		runner.withPropertyValues( "spring.datasource.type=io.volcanolabs.rds.RdsIamHikariDataSource" )
				.run( context -> {
					assertThat( context ).hasSingleBean( DataSource.class );
					assertThat( context ).getBean( DataSource.class ).isExactlyInstanceOf( RdsIamHikariDataSource.class );

					HikariDataSource dataSource = context.getBean( HikariDataSource.class );
					assertThat( dataSource.getMinimumIdle() ).isEqualTo( 2 );
					assertThat( dataSource.getMaximumPoolSize() ).isEqualTo( 5 );
					assertThat( dataSource.getJdbcUrl() ).isEqualTo( "jdbc:h2:mem:rdstest" );
					assertThat( dataSource.getUsername() ).isEqualTo( "placeholder_user" );
				} );
	}

	@Test
	void unsetTypeLeavesBootsOwnHikariDataSource() {
		runner.run( context -> {
			assertThat( context ).hasSingleBean( DataSource.class );
			assertThat( context ).getBean( DataSource.class ).isExactlyInstanceOf( HikariDataSource.class );
			assertThat( context.getBean( HikariDataSource.class ).getMinimumIdle() ).isEqualTo( 2 );
		} );
	}

	@Test
	void explicitHikariTypeLeavesBootsOwnHikariDataSource() {
		runner.withPropertyValues( "spring.datasource.type=com.zaxxer.hikari.HikariDataSource" )
				.run( context -> assertThat( context ).getBean( DataSource.class ).isExactlyInstanceOf( HikariDataSource.class ) );
	}

	@Test
	void userDefinedDataSourceBacksOff() {
		runner.withPropertyValues( "spring.datasource.type=io.volcanolabs.rds.RdsIamHikariDataSource" )
				.withBean( "customDataSource", DataSource.class, () -> new HikariDataSource() )
				.run( context -> {
					assertThat( context ).hasSingleBean( DataSource.class );
					assertThat( context ).hasBean( "customDataSource" );
				} );
	}
}
