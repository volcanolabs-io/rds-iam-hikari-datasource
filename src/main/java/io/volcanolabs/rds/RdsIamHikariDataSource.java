package io.volcanolabs.rds;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * This Hikari datasource uses the AWS SDK to generate an auth token. That token is used as the password for an RDS
 * instance configured for IAM Roles based access.
 */
public class RdsIamHikariDataSource extends HikariDataSource {
	private static final Logger log = LoggerFactory.getLogger( RdsIamHikariDataSource.class );

	public RdsIamHikariDataSource() {
		log.trace( "RdsIamHikariDataSource created" );
	}

	@Override
	public String getPassword() {
		log.trace( "RdsIamHikariDataSource.getPassword() called." );
		return getToken();
	}

	private String getToken() {
		var region = new DefaultAwsRegionProviderChain().getRegion();
		log.trace( "AWS region: {}", region );

		RdsIamAuthTokenGenerator generator = RdsIamAuthTokenGenerator.builder()
				.credentials( new DefaultAWSCredentialsProviderChain() )
				.region( region )
				.build();

		// JDBC URL has a standard URL format, like: jdbc:postgresql://localhost:5432/test_database
		var cleanUrl = getJdbcUrl().substring( 5 );
		log.trace( "cleanUrl: {}", cleanUrl );
		var dbUri = URI.create( cleanUrl );

		GetIamAuthTokenRequest request = GetIamAuthTokenRequest.builder()
				.hostname( dbUri.getHost() )
				.port( dbUri.getPort() )
				.userName( getUsername() )
				.build();

		return generator.getAuthToken( request );
	}
}
