package io.volcanolabs.rds;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;
import com.zaxxer.hikari.HikariDataSource;

import java.net.URI;

/**
 * This Hikari datasource uses the AWS SDK to generate an auth token. That token is used as the password for an RDS
 * instance configured for IAM Roles based access.
 */
public class RdsIamHikariDataSource extends HikariDataSource {

	@Override
	public String getPassword() {
		return getToken();
	}

	private String getToken() {
		var region = new DefaultAwsRegionProviderChain().getRegion();

		RdsIamAuthTokenGenerator generator = RdsIamAuthTokenGenerator.builder()
				.credentials( new DefaultAWSCredentialsProviderChain() )
				.region( region )
				.build();

		// JDBC URL has a standard URL format, like: jdbc:postgresql://localhost:5432/test_database
		var cleanUrl = getJdbcUrl().substring( 5 );
		var dbUri = URI.create( cleanUrl );

		GetIamAuthTokenRequest request = GetIamAuthTokenRequest.builder()
				.hostname( dbUri.getHost() )
				.port( dbUri.getPort() )
				.userName( getUsername() )
				.build();

		return generator.getAuthToken( request );
	}
}
